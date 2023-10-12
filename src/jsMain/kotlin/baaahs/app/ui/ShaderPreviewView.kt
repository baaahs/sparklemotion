package baaahs.app.ui

import baaahs.gl.GlContext
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.preview.ShaderPreview
import baaahs.gl.shader.type.PaintShader
import baaahs.show.Shader
import baaahs.ui.*
import baaahs.util.useResizeListener
import external.IntersectionObserver
import kotlinx.css.*
import materialui.icon
import mui.material.CircularProgress
import mui.material.CircularProgressVariant
import mui.material.Typography
import mui.system.sx
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.onClick
import react.useContext
import styled.StyleSheet
import styled.inlineStyles
import web.dom.Element
import web.html.HTMLDivElement

private val ShaderPreviewView = xComponent<ShaderPreviewProps>("ShaderPreview") { props ->
    val appContext = useContext(appContext)
    val sharedGlContext = if (props.noSharedGlContext == true) null else useContext(appGlContext).sharedGlContext
    val toolchain = props.toolchain
        ?: run { useContext(toolchainContext) }

    val canvasParent = ref<HTMLDivElement>()
    var shaderPreview by state<ShaderPreview?> { null }
    var errorPopupAnchor by state<Element?> { null }
    val preRenderHook = ref({})

    val shaderType = props.previewShaderBuilder?.openShader?.shaderType ?: run {
        // TODO: This is duplicating work that happens later in PreviewShaderBuilder, which is rotten.
        props.shader?.let { toolchain.openShader(it).shaderType }
    } ?: PaintShader
    val bootstrapper = shaderType.shaderPreviewBootstrapper
    val helper = memo(bootstrapper, sharedGlContext) {
//        console.log("Rememoize helper for ${props.shader?.title ?: props.previewShaderBuilder?.openShader?.title}")
        bootstrapper.createHelper(sharedGlContext)
    }
    val previewContainer = helper.container
    val sceneProvider = appContext.sceneProvider
    observe(sceneProvider)

    onMount(canvasParent.current, previewContainer, shaderPreview) {
        canvasParent.current?.let { parent ->
            parent.insertBefore(previewContainer, parent.firstChild)
        }
        val width = props.width ?: previewContainer.clientWidth.px
        val height = props.height ?: previewContainer.clientHeight.px
        helper.resize(width, height)
        shaderPreview?.resize(width.inPixels(), height.inPixels())

        withCleanup { canvasParent.current?.removeChild(previewContainer) }
    }

    val model = sceneProvider.openSceneOrFallback.model
    var gl by state<GlContext?> { null }
    onChange("shader type", helper, shaderType, model) {
        val preview = helper.bootstrap(model, preRenderHook)
        //            console.log("Rememoize preview for ${props.shader?.title ?: props.previewShaderBuilder?.openShader?.title}")

        gl = preview.renderEngine.gl

        val intersectionObserver = IntersectionObserver(callback = { entries ->
            if (entries.any { it.isIntersecting }) {
                preview.start()
            } else {
                preview.stop()
            }
        })
        intersectionObserver.observe(previewContainer)

        shaderPreview = preview

        withCleanup {
            intersectionObserver.disconnect()
            preview.destroy()
        }
    }

    onMount(helper, gl) {
        // 'gl' here is a state getter, so its value may have changed by the time we get to the cleanup.
        // Save it off so we're using the same value.
        val currentGl = gl

        withCleanup {
            currentGl?.let { helper.release(it) }
        }
    }

    val builder = memo(gl, props.shader, props.previewShaderBuilder) {
        gl?.let {
            props.previewShaderBuilder
                ?: props.shader?.let { shader ->
                    PreviewShaderBuilder(shader, toolchain, appContext.webClient.sceneProvider)
                }
        }
    }

    val gadgetAdjuster = memo(props.adjustGadgets) {
        props.adjustGadgets?.let { mode ->
            builder?.let { mode.build(builder.gadgets, appContext.clock) }
        }
    }
    preRenderHook.current = { gadgetAdjuster?.adjustGadgets() }

    onChange("different builder", gl, shaderPreview, builder, props.adjustGadgets) {
        if (gl == null) return@onChange
        if (shaderPreview == null) return@onChange
        if (builder == null) return@onChange

        val observer = builder.addObserver(fireImmediately = true) {
            when (it.state) {
                ShaderBuilder.State.Linked -> {
                    shaderPreview?.let { shaderPreview ->
                        it.startCompile(shaderPreview.renderEngine)
                    }
                }
                ShaderBuilder.State.Success -> {
                    shaderPreview?.setProgram(it.glslProgram)

                    if (props.dumpShader == true) {
                        println(
                            "Shader: ${it.glslProgram?.title} (${it.state})\n\n" +
                                    "${it.glslProgram?.fragShader?.source}"
                        )
                    }
                }
                else -> {
                }
            }
            forceRender()
        }
        withCleanup { observer.remove() }

        val intersectionObserver = IntersectionObserver(callback = { entries ->
            if (entries.any { it.isIntersecting }) {
                if (builder.state == ShaderBuilder.State.Unbuilt) {
                    builder.startBuilding()
                }
            }
        })
        intersectionObserver.observe(previewContainer)

        withCleanup {
            intersectionObserver.disconnect()
        }
    }

    useResizeListener(canvasParent) { _, _ ->
        // Tell Kotlin controller the window was resized
        canvasParent.current?.let { parent ->
            helper.resize(parent.clientWidth.px, parent.clientHeight.px)
            shaderPreview?.resize(parent.clientWidth, parent.clientHeight)
        }
    }

    div(+ShaderPreviewStyles.container) {
        ref = canvasParent
        if (props.width != null || props.height != null) {
            inlineStyles {
                props.width?.let { this.width = it }
                props.height?.let { this.height = it }
            }
        }

        val state = builder?.state ?: ShaderBuilder.State.Unbuilt
        when (state) {
            ShaderBuilder.State.Unbuilt,
            ShaderBuilder.State.Analyzing,
            ShaderBuilder.State.Linking,
            ShaderBuilder.State.Linked,
            ShaderBuilder.State.Compiling,
            ShaderBuilder.State.Binding -> {
                div(+ShaderPreviewStyles.progress) {
                    CircularProgress {
                        attrs.size = ".9em"
                        attrs.variant = CircularProgressVariant.determinate
                        attrs.value = state.ordinal.toFloat() / ShaderBuilder.State.Success.ordinal * 100
                    }

                    +" ${state.name}…"
                }
            }

            ShaderBuilder.State.Success -> {
            }

            ShaderBuilder.State.Errors -> {
                div(+ShaderPreviewStyles.errorBox) {
                    attrs.onClick = { event ->
                        errorPopupAnchor = event.currentTarget
                        event.stopPropagation()
                    }

                    icon(mui.icons.material.Warning)
                    Typography {
                        attrs.sx {
                            display = web.cssom.Display.block
                        }
                        +"Preview failed."
                    }
                }

                if (builder != null) {
                    shaderDiagnostics {
                        attrs.anchor = errorPopupAnchor
                        attrs.builder = builder
                        attrs.onClose = { errorPopupAnchor = null }
                    }
                }
            }
        }
    }
}

object ShaderPreviewStyles : StyleSheet("ui-ShaderPreview", isStatic = true) {
    val canvas by css {}
    val progress by css {}

    val container by css {
        position = Position.relative
        width = 100.pct
        height = 100.pct
        userSelect = UserSelect.none

        child(this@ShaderPreviewStyles, ::canvas) {
            position = Position.absolute
            width = 100.pct
            height = 100.pct
        }

        child(this@ShaderPreviewStyles, ::progress) {
            position = Position.absolute
            left = 3.px
            bottom = 0.px
            fontSize = .8.em
            zIndex = StyleConstants.Layers.aboveSharedGlCanvas
        }
    }

    val errorBox by css {
        backgroundColor = Color("#ccaaaacc")
        display = Display.flex
        alignItems = Align.center
        justifyContent = JustifyContent.center
        flexDirection = FlexDirection.column
        padding = Padding(1.em)
    }

    val errorPopup by css {
        child("div") {
            padding = Padding(0.em, 1.em)
        }
    }

    val errorMessage by css {
    }

    val errorSourceCode by css {
        maxHeight = 30.vh
        overflow = Overflow.scroll

        declarations["counter-reset"] = "line"

        child("code") {
            before {
                declarations["counter-increment"] = "line"
                declarations["content"] = "counter(line)"
                declarations["-webkit-user-select"] = "none"
                marginRight = 1.em
                borderRight = Border(1.px, BorderStyle.solid, Color.black)
                paddingRight = 1.em
                width = 4.em
                display = Display.inlineBlock
                textAlign = TextAlign.right
            }
        }
    }
}

external interface ShaderPreviewProps : Props {
    var shader: Shader?
    var previewShaderBuilder: ShaderBuilder?
    var width: LinearDimension?
    var height: LinearDimension?
    var adjustGadgets: GadgetAdjuster.Mode?
    var toolchain: Toolchain?
    var dumpShader: Boolean?
    var noSharedGlContext: Boolean?
}

fun RBuilder.shaderPreview(handler: RHandler<ShaderPreviewProps>) =
    child(ShaderPreviewView, handler = handler)