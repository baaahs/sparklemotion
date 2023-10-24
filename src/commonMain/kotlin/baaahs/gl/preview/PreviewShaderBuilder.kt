package baaahs.gl.preview

import baaahs.BaseShowPlayer
import baaahs.device.FixtureType
import baaahs.device.PixelArrayFixtureType
import baaahs.fixtures.ConfigPreview
import baaahs.fixtures.FixtureConfig
import baaahs.fixtures.FixtureOptions
import baaahs.gl.Toolchain
import baaahs.gl.data.FeedContext
import baaahs.gl.glsl.*
import baaahs.gl.openShader
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedProgram
import baaahs.gl.render.RenderEngine
import baaahs.gl.render.RenderResults
import baaahs.gl.result.ResultStorage
import baaahs.gl.result.SingleResultStorage
import baaahs.gl.result.Vec2ResultType
import baaahs.gl.shader.OpenShader
import baaahs.glsl.Shaders
import baaahs.model.Model
import baaahs.plugin.core.feed.RasterCoordinateFeed
import baaahs.scene.MutableFixtureOptions
import baaahs.scene.SceneProvider
import baaahs.show.Feed
import baaahs.show.Shader
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableFeedPort
import baaahs.show.mutable.MutablePatchSet
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.util.Logger
import baaahs.util.coroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.milliseconds

interface ShaderBuilder : IObservable {
    val shader: Shader
    val state: State
    val gadgets: List<GadgetPreview>
    val shaderAnalysis: ShaderAnalysis?
    val openShader: OpenShader?
    val linkedProgram: LinkedProgram?
    val glslProgram: GlslProgram?
    val glslErrors: List<GlslError>

    fun startBuilding()
    fun startCompile(renderEngine: RenderEngine)

    /** Contrary to expectations, linking happens before compiling in this world. */
    enum class State {
        Unbuilt,
        Analyzing,
        Linking,
        Linked,
        Compiling,
        Binding,
        Success,
        Errors
    }

    class GadgetPreview(
        val id: String,
        val openControl: OpenControl,
        val controlledFeed: Feed?
    )
}

class PreviewShaderBuilder(
    override val shader: Shader,
    private val toolchain: Toolchain,
    private val sceneProvider: SceneProvider,
    private val previewShaders: PreviewShaders = toolchain.defaultPreviewShaders,
    private val coroutineScope: CoroutineScope = GlobalScope
) : Observable(), ShaderBuilder {
    override var state: ShaderBuilder.State =
        ShaderBuilder.State.Unbuilt
        private set

    override var shaderAnalysis: ShaderAnalysis? = null
        private set
    override var openShader: OpenShader? = null
        private set
    var previewPatchSet: MutablePatchSet? = null
        private set
    override var linkedProgram: LinkedProgram? = null
        private set
    private var compiledProgram: GlslCompilingProgram? = null
        private set
    override var glslProgram: GlslProgram? = null
        private set

    override val gadgets: List<ShaderBuilder.GadgetPreview> get() = mutableGadgets
    private val mutableGadgets: MutableList<ShaderBuilder.GadgetPreview> = arrayListOf()

    override val glslErrors: List<GlslError> get() =
        (shaderAnalysis?.errors ?: emptyList()) + compileErrors

    private var compileErrors: List<GlslError> = emptyList()

    val feedContexts = mutableListOf<FeedContext>()

    override fun startBuilding() {
        transitionTo(
            if (openShader == null) {
                ShaderBuilder.State.Analyzing
            } else {
                ShaderBuilder.State.Linking
            }
        )
    }

    private fun transitionTo(newState: ShaderBuilder.State) {
        logger.debug { "Transition to $newState for ${shader.title}"}
        state = newState
        notifyChanged()

        fun unsupported(): Unit = error("transitionTo($newState) not supported")
        when (newState) {
            ShaderBuilder.State.Unbuilt -> unsupported()
            ShaderBuilder.State.Analyzing -> coroutineScope.launch(coroutineExceptionHandler) { analyze() }
            ShaderBuilder.State.Linking -> coroutineScope.launch(coroutineExceptionHandler) { link() }
            ShaderBuilder.State.Linked -> { } // No-op; an observer will handle it.
            ShaderBuilder.State.Compiling -> unsupported()
            ShaderBuilder.State.Binding -> coroutineScope.launch(coroutineExceptionHandler) { bind() }
            ShaderBuilder.State.Success -> unsupported()
            ShaderBuilder.State.Errors -> { } // No-op; an observer will handle it.
        }
    }

    fun analyze() {
        val shaderAnalysis = toolchain.analyze(shader)
        this.shaderAnalysis = shaderAnalysis
        openShader = toolchain.openShader(shaderAnalysis)

        if (!shaderAnalysis.isValid) {
            transitionTo(ShaderBuilder.State.Errors)
        } else {
            transitionTo(ShaderBuilder.State.Linking)
        }
    }

    fun link() {
        val newState = try {
            val openShader = openShader!!
            val shaderType = openShader.shaderType
            val shaders = shaderType.pickPreviewShaders(openShader, previewShaders)
            val resultContentType = shaderType.previewResultContentType()
            val defaultPorts = if (shaderType.injectUvCoordinateForPreview) {
                mapOf(ContentType.UvCoordinate to MutableFeedPort(RasterCoordinateFeed()))
            } else emptyMap()

            previewPatchSet = toolchain.autoWire(shaders, defaultPorts = defaultPorts)
//                .dumpOptions()
                .acceptSuggestedLinkOptions()
                .confirm()
            linkedProgram = previewPatchSet?.openForPreview(toolchain, resultContentType)
            ShaderBuilder.State.Linked
        } catch (e: GlslException) {
            logger.warn(e) { "Failed to compile shader." }
            e.errors.forEach { logger.warn { it.message } }
            compileErrors = e.errors
            ShaderBuilder.State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to analyze shader." }
            compileErrors = listOf(GlslError(e.message ?: e.toString()))
            ShaderBuilder.State.Errors
        }

        transitionTo(newState)
    }

    override fun startCompile(renderEngine: RenderEngine) {
        state = ShaderBuilder.State.Compiling
        notifyChanged()
        logger.debug { "Transition to $state for ${shader.title}"}

        coroutineScope.launch(coroutineExceptionHandler) {
            val showPlayer = object : BaseShowPlayer(toolchain, sceneProvider) {}

            compile(renderEngine) { id, feed ->
                feed.buildControl()?.let {
                    // TODO: De-gnarl this mess.
                    val openControl = it.previewOpen()
                    val gadget = openControl.gadget
                    if (gadget == null) {
                        logger.warn { "No gadget for $openControl" }
                    } else {
                        showPlayer.registerGadget(feed.suggestId(), gadget, feed)
                    }
                    mutableGadgets.add(ShaderBuilder.GadgetPreview(id, openControl, feed))
                }

                feed.open(showPlayer, id)
                    .also { feedContexts.add(it) }
            }

            mutableGadgets.sortBy { it.id }
        }
    }

    private fun compile(renderEngine: RenderEngine, feedResolver: FeedResolver) {
        val newState = try {
            compiledProgram = linkedProgram?.let { renderEngine.compile(it, feedResolver) }
            glslProgram = null
            ShaderBuilder.State.Binding
        } catch (e: GlslException) {
            compileErrors = e.errors
            ShaderBuilder.State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to compile patch." }
            compileErrors = listOf(GlslError(e.message ?: e.toString()))
            ShaderBuilder.State.Errors
        }
        transitionTo(newState)
    }

    private suspend fun bind() {
        while (compiledProgram?.isReady() == false) {
            logger.debug { "${shader.title} not yet ready, delay..." }
            delay(10.milliseconds)
        }

        try {
            glslProgram = compiledProgram?.bind()
            state = ShaderBuilder.State.Success
        } catch (e: GlslException) {
            compileErrors = e.errors
            state = ShaderBuilder.State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to compile patch." }
            compileErrors = listOf(GlslError(e.message ?: e.toString()))
            state = ShaderBuilder.State.Errors
        }
        notifyChanged()
        logger.debug { "Transition to $state for ${shader.title}"}
    }

    fun release() {
        feedContexts.forEach { feed -> feed.disuse() }
    }

    fun finalize() {
        release()
    }

    companion object {
        private val logger = Logger("ShaderEditor")

        val screenCoordsProjection: Shader =
            Shader(
                "Screen Coords",
                /**language=glsl*/
                """
                    uniform vec2 previewResolution; // @@baaahs.Core:PreviewResolution @type preview-resolution
                    
                    // @return uv-coordinate
                    vec2 main(
                        vec4 fragCoords // @@baaahs.Core:RasterCoordinate @type raster-coordinate
                    ) {
                      return fragCoords.xy / previewResolution;
                    }
                """.trimIndent()
            )
    }
}

class PreviewShaders(val toolchain: Toolchain) {
    private fun analyze(shader: Shader) = toolchain.openShader(shader)

    val screenCoordsProjection by lazy { analyze(PreviewShaderBuilder.screenCoordsProjection) }
    val pixelUvIdentity by lazy { analyze(Shaders.pixelUvIdentity) }
    val smpteColorBars by lazy { analyze(Shaders.smpteColorBars) }
    val earthLikePlanet by lazy { analyze(Shaders.earthLikePlanet) }
    val backgroundImage by lazy { earthLikePlanet }
}

object ProjectionPreviewDevice: PixelArrayFixtureType() {
    override val id: String
        get() = "ProjectionPreview"
    override val title: String
        get() = "Projection Preview"
    override val resultContentType: ContentType
        get() = ContentType.UvCoordinate

    override val likelyPipelines: List<Pair<ContentType, ContentType>>
        get() = emptyList()

    override val errorIndicatorShader: Shader
        get() = Shader(
            "Ω Guru Meditation Error Ω",
            ""
        )

    override val emptyOptions: FixtureOptions
        get() = Options()
    override val defaultOptions: FixtureOptions
        get() = Options()

    override fun createResultStorage(renderResults: RenderResults): ResultStorage {
        val resultBuffer = renderResults.allocate("Vertex Location", Vec2ResultType)
        return SingleResultStorage(resultBuffer)
    }

    override fun toString(): String = id

    @Serializable
    class Options : FixtureOptions {
        override val componentCount: Int
            get() = 1
        override val bytesPerComponent: Int
            get() = error("bytesPerComponent not implemented for ProjectionPreviewDevice")

        override val fixtureType: FixtureType
            get() = ProjectionPreviewDevice

        override fun edit(): MutableFixtureOptions = TODO("not implemented")
        override fun plus(other: FixtureOptions?) = this
        override fun preview(): ConfigPreview = TODO("not implemented")
        override fun toConfig(entity: Model.Entity?, model: Model, defaultComponentCount: Int?): FixtureConfig =
            TODO("not implemented")
    }

    object NullConfig : FixtureConfig {
        override val componentCount: Int
            get() = TODO("not implemented")
        override val bytesPerComponent: Int
            get() = TODO("not implemented")
        override val fixtureType: FixtureType
            get() = TODO("not implemented")
    }
}