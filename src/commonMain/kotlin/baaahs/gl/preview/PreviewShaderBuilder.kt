package baaahs.gl.preview

import baaahs.BaseShowPlayer
import baaahs.device.FixtureType
import baaahs.device.PixelArrayFixtureType
import baaahs.fixtures.*
import baaahs.gl.Toolchain
import baaahs.gl.data.Feed
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
import baaahs.plugin.core.datasource.RasterCoordinateDataSource
import baaahs.scene.MutableFixtureConfig
import baaahs.scene.SceneProvider
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableDataSourcePort
import baaahs.show.mutable.MutablePatchSet
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.util.Logger
import baaahs.util.coroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

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
        Success,
        Errors
    }

    class GadgetPreview(
        val id: String,
        val openControl: OpenControl,
        val controlledDataSource: DataSource?
    )
}

class PreviewShaderBuilder(
    override val shader: Shader,
    private val toolchain: Toolchain,
    private val sceneProvider: SceneProvider,
    private val coroutineScope: CoroutineScope = GlobalScope
) : Observable(), ShaderBuilder {

    constructor(
        openShader: OpenShader,
        toolchain: Toolchain,
        sceneProvider: SceneProvider,
        coroutineScope: CoroutineScope = GlobalScope
    ) : this(openShader.shader, toolchain, sceneProvider, coroutineScope) {
        this.openShader = openShader
    }

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
    override var glslProgram: GlslProgram? = null
        private set

    override val gadgets: List<ShaderBuilder.GadgetPreview> get() = mutableGadgets
    private val mutableGadgets: MutableList<ShaderBuilder.GadgetPreview> = arrayListOf()

    override val glslErrors: List<GlslError> get() =
        (shaderAnalysis?.errors ?: emptyList()) + compileErrors

    private var compileErrors: List<GlslError> = emptyList()

    val feeds = mutableListOf<Feed>()

    private val previewShaders = PreviewShaders(toolchain)

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
                mapOf(ContentType.UvCoordinate to MutableDataSourcePort(RasterCoordinateDataSource()))
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

            compile(renderEngine) { id, dataSource ->
                dataSource.buildControl()?.let {
                    // TODO: De-gnarl this mess.
                    val openControl = it.previewOpen()
                    val gadget = openControl.gadget
                    if (gadget == null) {
                        logger.warn { "No gadget for $openControl" }
                    } else {
                        showPlayer.registerGadget(dataSource.suggestId(), gadget, dataSource)
                    }
                    mutableGadgets.add(ShaderBuilder.GadgetPreview(id, openControl, dataSource))
                }

                dataSource.createFeed(showPlayer, id)
                    .also { feeds.add(it) }
            }

            mutableGadgets.sortBy { it.id }
        }
    }

    private fun compile(renderEngine: RenderEngine, feedResolver: FeedResolver) {
        try {
            glslProgram = linkedProgram?.let { renderEngine.compile(it, feedResolver) }
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
        feeds.forEach { feed -> feed.disuse() }
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

    override val emptyConfig: FixtureConfig
        get() = Config()
    override val defaultConfig: FixtureConfig
        get() = Config()

    override fun createResultStorage(renderResults: RenderResults): ResultStorage {
        val resultBuffer = renderResults.allocate("Vertex Location", Vec2ResultType)
        return SingleResultStorage(resultBuffer)
    }

    override fun createFixture(
        modelEntity: Model.Entity?,
        componentCount: Int,
        fixtureConfig: FixtureConfig,
        name: String,
        transport: Transport,
        model: Model
    ): Fixture = object : Fixture(modelEntity, componentCount, name, transport) {
        override val fixtureType: FixtureType
            get() = ProjectionPreviewDevice
        override val remoteConfig: RemoteConfig
            get() = TODO("not implemented")
    }

    override fun toString(): String = id

    @Serializable
    class Config : FixtureConfig {
        override val componentCount: Int
            get() = 1
        override val bytesPerComponent: Int
            get() = error("bytesPerComponent not implemented for ProjectionPreviewDevice")

        override val fixtureType: FixtureType
            get() = ProjectionPreviewDevice

        override fun edit(): MutableFixtureConfig = TODO("not implemented")
        override fun plus(other: FixtureConfig?) = this
        override fun preview(): ConfigPreview = TODO("not implemented")
    }
}