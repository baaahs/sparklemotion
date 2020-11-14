package baaahs.gl.preview

import baaahs.BaseShowPlayer
import baaahs.Gadget
import baaahs.fixtures.*
import baaahs.gl.data.Feed
import baaahs.gl.glsl.FeedResolver
import baaahs.gl.glsl.GlslError
import baaahs.gl.glsl.GlslException
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.patch.AutoWirer
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.RenderEngine
import baaahs.gl.shader.OpenShader
import baaahs.glsl.Shaders
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.ShaderType
import baaahs.show.mutable.MutableConstPort
import baaahs.show.mutable.MutablePatch
import baaahs.ui.IObservable
import baaahs.ui.Observable
import baaahs.util.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface ShaderBuilder : IObservable {
    val shader: Shader
    val state: State
    val gadgets: List<GadgetPreview>
    val openShader: OpenShader?
    val linkedPatch: LinkedPatch?
    val glslProgram: GlslProgram?
    val glslErrors: List<GlslError>

    fun startBuilding()
    fun startCompile(renderEngine: RenderEngine)

    /** Contrary to expectations, linking happens before compiling in this world. */
    enum class State {
        Unbuilt,
        Linking,
        Linked,
        Compiling,
        Success,
        Errors
    }

    class GadgetPreview(
        val id: String,
        val gadget: Gadget,
        val controlledDataSource: DataSource?
    )
}

class PreviewShaderBuilder(
    override val shader: Shader,
    private val autoWirer: AutoWirer,
    private val modelInfo: ModelInfo,
    private val coroutineScope: CoroutineScope = GlobalScope
) : Observable(), ShaderBuilder {
    override var state: ShaderBuilder.State =
        ShaderBuilder.State.Unbuilt
        private set

    override var openShader: OpenShader? = null
        private set
    var previewPatch: MutablePatch? = null
        private set
    override var linkedPatch: LinkedPatch? = null
        private set
    override var glslProgram: GlslProgram? = null
        private set

    override val gadgets: List<ShaderBuilder.GadgetPreview> get() = mutableGadgets
    private val mutableGadgets: MutableList<ShaderBuilder.GadgetPreview> = arrayListOf()

    override var glslErrors: List<GlslError> = emptyList()
        private set

    val feeds = mutableListOf<Feed>()

    private fun analyze(shader: Shader) = autoWirer.glslAnalyzer.openShader(shader)
    private val screenCoordsProjection by lazy { analyze(PreviewShaderBuilder.screenCoordsProjection) }
    private val pixelUvIdentity by lazy { analyze(Shaders.pixelUvIdentity) }
    private val smpteColorBars by lazy { analyze(Shaders.smpteColorBars) }

    override fun startBuilding() {
        state = ShaderBuilder.State.Linking
        notifyChanged()

        coroutineScope.launch {
            link()
        }
    }

    fun link() {
        try {
            val openShader = analyze(shader)
            this.openShader = openShader
            val shaders: Array<OpenShader> = when (shader.type) {
                ShaderType.Projection -> arrayOf(openShader, pixelUvIdentity)
                ShaderType.Distortion -> arrayOf(screenCoordsProjection, openShader, smpteColorBars)
                ShaderType.Paint -> arrayOf(screenCoordsProjection, openShader)
                ShaderType.Filter -> arrayOf(screenCoordsProjection, openShader, smpteColorBars)
                ShaderType.Mover -> arrayOf(openShader)
            }

            val defaultPorts = when (shader.type) {
                ShaderType.Projection -> emptyMap()
                else -> mapOf(ContentType.UvCoordinateStream to MutableConstPort("gl_FragCoord"))
            }

            previewPatch = autoWirer.autoWire(*shaders, defaultPorts = defaultPorts)
                .acceptSuggestedLinkOptions()
                .resolve()
            linkedPatch = previewPatch?.openForPreview(autoWirer)
            state = ShaderBuilder.State.Linked
        } catch (e: GlslException) {
            glslErrors = e.errors
            state = ShaderBuilder.State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to analyze shader." }
            glslErrors = listOf(GlslError(e.message ?: e.toString()))
            state = ShaderBuilder.State.Errors
        }
        notifyChanged()
    }

    override fun startCompile(renderEngine: RenderEngine) {
        state = ShaderBuilder.State.Compiling
        notifyChanged()

        coroutineScope.launch {
            val showPlayer = object : BaseShowPlayer(autoWirer.plugins, modelInfo) {
                override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
                    mutableGadgets.add(ShaderBuilder.GadgetPreview(id, gadget, controlledDataSource))
                    super.registerGadget(id, gadget, controlledDataSource)
                }
            }

            compile(renderEngine) { id, dataSource ->
                dataSource.buildControl()?.let {
                    showPlayer.registerGadget(dataSource.suggestId(), it.gadget, dataSource)
                }

                dataSource.createFeed(showPlayer, autoWirer.plugins, id)
                    .also { feeds.add(it) }
            }
        }
    }

    private fun compile(renderEngine: RenderEngine, feedResolver: FeedResolver) {
        try {
            glslProgram = linkedPatch?.let { renderEngine.compile(it, feedResolver) }
            state = ShaderBuilder.State.Success
        } catch (e: GlslException) {
            glslErrors = e.errors
            state = ShaderBuilder.State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to compile patch." }
            glslErrors = listOf(GlslError(e.message ?: e.toString()))
            state = ShaderBuilder.State.Errors
        }
        notifyChanged()
    }

    fun release() {
        feeds.forEach { feed -> feed.release() }
    }

    fun finalize() {
        release()
    }

    companion object {
        private val logger = Logger("ShaderEditor")

        private val screenCoordsProjection by lazy {
            Shader(
                "Screen Coords", ShaderType.Projection, """
                    uniform vec2 previewResolution;
                    
                    vec2 mainProjection(vec2 fragCoords) {
                      return fragCoords / previewResolution;
                    }
                """.trimIndent()
            )
        }
    }
}

object ProjectionPreviewDevice: DeviceType {
    override val id: String get() = "ProjectionPreview"
    override val title: String get() = "Projection Preview"
    override val dataSources: List<DataSource> get() = PixelArrayDevice.dataSources
    override val resultParams: List<ResultParam>
        get() = listOf(
            ResultParam("Vertex Location", Vec2ResultType)
        )
    override val resultContentType: ContentType
        get() = ContentType.UvCoordinateStream

    override val errorIndicatorShader: Shader
        get() = Shader(
            "Ω Guru Meditation Error Ω",
            ShaderType.Paint,
            /**language=glsl*/
            ""
        )

    fun getVertexLocations(resultViews: List<ResultView>): Vec2ResultType.Vec2ResultView {
        return resultViews[0] as Vec2ResultType.Vec2ResultView
    }

    override fun toString(): String = id
}