package baaahs.gl.preview

import baaahs.BaseShowPlayer
import baaahs.control.OpenButtonControl
import baaahs.control.OpenColorPickerControl
import baaahs.fixtures.*
import baaahs.getValue
import baaahs.gl.Toolchain
import baaahs.gl.data.Feed
import baaahs.gl.glsl.*
import baaahs.gl.openShader
import baaahs.gl.patch.ContentType
import baaahs.gl.patch.LinkedPatch
import baaahs.gl.render.RenderEngine
import baaahs.gl.shader.OpenShader
import baaahs.glsl.Shaders
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.Shader
import baaahs.show.live.OpenControl
import baaahs.show.live.PreviewOpenContext
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
    val shaderAnalysis: ShaderAnalysis?
    val openShader: OpenShader?
    val linkedPatch: LinkedPatch?
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
    private val modelInfo: ModelInfo,
    private val coroutineScope: CoroutineScope = GlobalScope
) : Observable(), ShaderBuilder {
    override var state: ShaderBuilder.State =
        ShaderBuilder.State.Unbuilt
        private set

    override var shaderAnalysis: ShaderAnalysis? = null
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

    override val glslErrors: List<GlslError> get() =
        (shaderAnalysis?.errors ?: emptyList()) + compileErrors

    private var compileErrors: List<GlslError> = emptyList()

    val feeds = mutableListOf<Feed>()

    private val previewShaders = PreviewShaders(toolchain)

    override fun startBuilding() {
        state = ShaderBuilder.State.Analyzing
        notifyChanged()

        coroutineScope.launch {
            analyze()
        }
    }

    fun analyze() {
        val shaderAnalysis = toolchain.analyze(shader)
        this.shaderAnalysis = shaderAnalysis
        openShader = toolchain.openShader(shaderAnalysis)
        state = ShaderBuilder.State.Linking
        notifyChanged()

        coroutineScope.launch {
            link()
        }
    }

    fun link() {
        try {
            val openShader = openShader!!
            val shaderType = openShader.shaderType
            val shaders = shaderType.pickPreviewShaders(openShader, previewShaders)
            val resultContentType = shaderType.previewResultContentType()
            val defaultPorts = if (shaderType.injectUvCoordinateForPreview) {
                mapOf(ContentType.UvCoordinate to MutableConstPort("gl_FragCoord", GlslType.Vec2))
            } else emptyMap()

            previewPatch = toolchain.autoWire(shaders, defaultPorts = defaultPorts)
//                .dumpOptions()
                .acceptSuggestedLinkOptions()
                .confirm()
            linkedPatch = previewPatch?.openForPreview(toolchain, resultContentType)
            state = ShaderBuilder.State.Linked
        } catch (e: GlslException) {
            compileErrors = e.errors
            state = ShaderBuilder.State.Errors
        } catch (e: Exception) {
            logger.warn(e) { "Failed to analyze shader." }
            compileErrors = listOf(GlslError(e.message ?: e.toString()))
            state = ShaderBuilder.State.Errors
        }
        notifyChanged()
    }

    override fun startCompile(renderEngine: RenderEngine) {
        state = ShaderBuilder.State.Compiling
        notifyChanged()

        coroutineScope.launch {
            val showPlayer = object : BaseShowPlayer(toolchain, modelInfo) {}
            val previewOpenContext = PreviewOpenContext()

            compile(renderEngine) { id, dataSource ->
                dataSource.buildControl()?.let {
                    // TODO: De-gnarl this mess.
                    val openControl = it.previewOpen(previewOpenContext)
                    val gadget = when (openControl) {
                        is OpenButtonControl -> openControl.switch
                        is OpenColorPickerControl -> openControl.colorPicker
// TODO                       is OpenSliderControl -> openControl.slider
                        else -> null
                    }
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
            glslProgram = linkedPatch?.let { renderEngine.compile(it, feedResolver) }
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
    }

    fun release() {
        feeds.forEach { feed -> feed.release() }
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

object ProjectionPreviewDevice: DeviceType {
    override val id: String get() = "ProjectionPreview"
    override val title: String get() = "Projection Preview"
    override val dataSourceBuilders: List<DataSourceBuilder<*>> get() = PixelArrayDevice.dataSourceBuilders
    override val resultParams: List<ResultParam>
        get() = listOf(
            ResultParam("Vertex Location", Vec2ResultType)
        )
    override val resultContentType: ContentType
        get() = ContentType.UvCoordinate

    override val likelyPipelines: List<Pair<ContentType, ContentType>>
        get() = emptyList()

    override val errorIndicatorShader: Shader
        get() = Shader(
            "Ω Guru Meditation Error Ω",
            ""
        )

    fun getVertexLocations(resultViews: List<ResultView>): Vec2ResultType.Vec2ResultView {
        return resultViews[0] as Vec2ResultType.Vec2ResultView
    }

    override fun toString(): String = id
}