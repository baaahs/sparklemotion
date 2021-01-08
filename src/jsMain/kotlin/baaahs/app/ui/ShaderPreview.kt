package baaahs.app.ui

import baaahs.document
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
import baaahs.ui.on
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.IntersectionObserver
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.components.divider.divider
import materialui.components.popover.enums.PopoverStyle
import materialui.components.popover.popover
import materialui.components.typography.enums.TypographyDisplay
import materialui.components.typography.typography
import materialui.icon
import materialui.icons.Icons
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.*
import styled.StyleSheet
import styled.css
import styled.styledDiv
import kotlin.collections.set

val ShaderPreview = xComponent<ShaderPreviewProps>("ShaderPreview") { props ->
    val appContext = useContext(appContext)
    val toolchain = props.toolchain ?: appContext.toolchain

    val canvasParent = ref<HTMLDivElement?> { null }
    var shaderPreview by state<ShaderPreview?> { null }
    var errorPopupAnchor by state<EventTarget?> { null }
    val preRenderHook = ref { {} }

    val width = props.width ?: 150.px
    val height = props.height ?: 150.px

    val canvas = memo(props.previewShaderBuilder?.openShader?.shaderType) {
        document.createElement("canvas").apply {
                this.setAttribute("width", width.toString())
                this.setAttribute("height", height.toString())
            } as HTMLCanvasElement
    }
    var gl by state<GlContext?> { null }

    onMount(canvasParent.current, canvas) {
        canvasParent.current?.let { parent ->
            parent.insertBefore(canvas, parent.firstChild)
        }

        withCleanup { canvasParent.current?.removeChild(canvas) }
    }

    onChange("shader type", canvas, props.previewShaderBuilder?.openShader?.shaderType) {
        val shaderType = props.previewShaderBuilder?.openShader?.shaderType ?: run {
            // TODO: This is duplicating work that happens later in PreviewShaderBuilder, which is rotten.
            toolchain.openShader(props.shader!!).shaderType
        }

        val preview = shaderType.shaderPreviewBootstrapper.bootstrap(
            canvas, appContext.webClient.model, preRenderHook
        )
        gl = preview.renderEngine.gl

        val intersectionObserver = IntersectionObserver { entries ->
            if (entries.any { it.isIntersecting }) {
                preview.start()
            } else {
                preview.stop()
            }
        }
        intersectionObserver.observe(canvas)

        shaderPreview = preview

        withCleanup {
            intersectionObserver.disconnect()
            preview.destroy()
        }
    }

    val builder = memo(gl, props.shader, props.previewShaderBuilder) {
        gl?.let {
            props.previewShaderBuilder
                ?: PreviewShaderBuilder(props.shader!!, toolchain, appContext.webClient.model)
        }
    }

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
                    val gadgetAdjuster =
                        GadgetAdjuster(builder.gadgets, appContext.clock)
                    preRenderHook.current = {
                        if (props.adjustGadgets) gadgetAdjuster.adjustGadgets()
                    }

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

    styledDiv {
        ref = canvasParent
        css { +ShaderPreviewStyles.container }
        css.width = width
        css.height = height

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

                    icon(Icons.Warning)
                    typography {
                        attrs.display = TypographyDisplay.block
                        +"Preview failed."
                    }
                }

                popover(ShaderPreviewStyles.errorPopup on PopoverStyle.paper) {
                    attrs.open = errorPopupAnchor != null
                    attrs.anchorEl(errorPopupAnchor)
                    attrs.onClose = { event, _ ->
                        errorPopupAnchor = null
                        event.stopPropagation()
                    }

                    header { +"Errors:" }

                    div {
                        if (errorPopupAnchor != null) {
                            pre(+ShaderPreviewStyles.errorMessage) {
                                +(builder?.glslErrors?.joinToString("\n") ?: "No errors!?")
                            }

                            divider {}

                            pre(+ShaderPreviewStyles.errorSourceCode) {
                                (builder?.linkedPatch?.toFullGlsl("x") ?: "No source!?")
                                    .split("\n")
                                    .forEach { code { +it }; +"\n" }
                            }
                        }
                    }
                }
            }
        }
    }
}

object ShaderPreviewStyles : StyleSheet("ui-ShaderPreview", isStatic = true) {
    val container by css {
        position = Position.relative

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
    var adjustGadgets: Boolean
    var toolchain: Toolchain?
}

fun RBuilder.shaderPreview(handler: RHandler<ShaderPreviewProps>) =
    child(ShaderPreview, handler = handler)