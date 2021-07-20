package baaahs.app.ui

import baaahs.gl.GlContext
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.preview.ShaderPreview
import baaahs.jsx.useResizeListener
import baaahs.show.Shader
import baaahs.ui.addObserver
import baaahs.ui.inPixels
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.IntersectionObserver
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.typography
import materialui.icon
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.div
import styled.StyleSheet
import styled.inlineStyles
import kotlin.collections.set

val ShaderPreview = xComponent<ShaderPreviewProps>("ShaderPreview") { props ->
    val appContext = useContext(appContext)
    val sharedGlContext = useContext(appGlContext).sharedGlContext
    val toolchain = props.toolchain ?: appContext.toolchain

    val canvasParent = ref<HTMLDivElement>()
    var shaderPreview by state<ShaderPreview?> { null }
    var errorPopupAnchor by state<EventTarget?> { null }
    val preRenderHook = ref({})

    val shaderType = props.previewShaderBuilder?.openShader?.shaderType ?: run {
        // TODO: This is duplicating work that happens later in PreviewShaderBuilder, which is rotten.
        toolchain.openShader(props.shader!!).shaderType
    }
    val bootstrapper = shaderType.shaderPreviewBootstrapper
    val helper = memo(bootstrapper, sharedGlContext) { bootstrapper.createHelper(sharedGlContext) }
    val previewContainer = helper.container

    var gl by state<GlContext?> { null }

    onMount(canvasParent.current, previewContainer) {
        canvasParent.current?.let { parent ->
            parent.insertBefore(previewContainer, parent.firstChild)
        }
        val width = props.width ?: previewContainer.clientWidth.px
        val height = props.height ?: previewContainer.clientHeight.px
        helper.resize(width, height)
        shaderPreview?.resize(width.inPixels(), height.inPixels())

        withCleanup { canvasParent.current?.removeChild(previewContainer) }
    }

    onChange("shader type", helper, shaderType) {
        val preview = helper.bootstrap(appContext.webClient.model, preRenderHook)
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
        withCleanup { gl?.let { helper.release(it) } }
    }

    val builder = memo(gl, props.shader, props.previewShaderBuilder) {
        gl?.let {
            props.previewShaderBuilder
                ?: PreviewShaderBuilder(props.shader!!, toolchain, appContext.webClient.model)
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
                }
                else -> {
                }
            }
            forceRender()
        }
        withCleanup { observer.remove() }

        if (builder.state == ShaderBuilder.State.Unbuilt) {
            builder.startBuilding()
        }
    }

    useResizeListener(canvasParent) {
        // Tell Kotlin controller the window was resized
        shaderPreview?.resize(
            canvasParent.current!!.clientWidth,
            canvasParent.current!!.clientHeight
        )
    }

    div(+ShaderPreviewStyles.container) {
        ref = canvasParent
        if (props.width != null || props.height != null) {
            inlineStyles {
                props.width?.let { this.width = it }
                props.height?.let { this.height = it }
            }
        }

        when (builder?.state ?: ShaderBuilder.State.Unbuilt) {
            ShaderBuilder.State.Unbuilt,
            ShaderBuilder.State.Analyzing,
            ShaderBuilder.State.Linking,
            ShaderBuilder.State.Linked,
            ShaderBuilder.State.Compiling -> {
                div { +"Building..." }
            }

            ShaderBuilder.State.Success -> {
            }

            ShaderBuilder.State.Errors -> {
                div(+ShaderPreviewStyles.errorBox) {
                    attrs.onClickFunction = { event ->
                        errorPopupAnchor = event.currentTarget
                        event.stopPropagation()
                    }

                    icon(materialui.icons.Warning)
                    typography {
                        attrs.display = TypographyDisplay.block
                        +"Preview failed."
                    }
                }

                shaderDiagnostics {
                    attrs.anchor = errorPopupAnchor
                    attrs.builder = builder!!
                    attrs.onClose = { errorPopupAnchor = null }
                }
            }
        }
    }
}

object ShaderPreviewStyles : StyleSheet("ui-ShaderPreview", isStatic = true) {
    val container by css {
        position = Position.relative
        width = 100.pct
        height = 100.pct

        child("canvas") {
            position = Position.absolute
        }
        child("div") {
            position = Position.absolute
            width = 100.pct
            height = 100.pct
        }
    }

    val errorBox by css {
        backgroundColor = Color("#ccaaaacc")
        display = Display.flex
        alignItems = Align.center
        justifyContent = JustifyContent.center
        flexDirection = FlexDirection.column
        padding(1.em)
    }

    val errorPopup by css {
        child("div") {
            padding(0.em, 1.em)
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
                borderRight = "1px solid black"
                paddingRight = 1.em
                width = 4.em
                display = Display.inlineBlock
                textAlign = TextAlign.right
            }
        }
    }
}

external interface ShaderPreviewProps : RProps {
    var shader: Shader?
    var previewShaderBuilder: ShaderBuilder?
    var width: LinearDimension?
    var height: LinearDimension?
    var adjustGadgets: GadgetAdjuster.Mode?
    var toolchain: Toolchain?
}

fun RBuilder.shaderPreview(handler: RHandler<ShaderPreviewProps>) =
    child(ShaderPreview, handler = handler)