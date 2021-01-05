package baaahs.app.ui

import baaahs.document
import baaahs.gl.GlBase
import baaahs.gl.GlContext
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.render.ProjectionPreview
import baaahs.gl.render.QuadPreview
import baaahs.gl.render.ShaderPreview
import baaahs.gl.shader.type.ProjectionShader
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
import org.w3c.dom.events.EventTarget
import react.*
import react.dom.*
import styled.StyleSheet
import styled.css
import styled.styledDiv
import kotlin.collections.set

val ShaderPreview = xComponent<ShaderPreviewProps>("ShaderPreview") { props ->
    val appContext = useContext(appContext)
    val canvas = ref<HTMLCanvasElement?> { null }
    var gl by state<GlContext?> { null }
    var shaderPreview by state<ShaderPreview?> { null }
    var errorPopupAnchor by state<EventTarget?> { null }
    val preRenderHook = ref { {} }

    val width = props.width ?: 150.px
    val height = props.height ?: 150.px

    onMount(canvas.current) {
        val canvasEl = canvas.current ?: return@onMount

        val shaderType = props.previewShaderBuilder?.openShader?.shaderType ?: run {
            // TODO: This is duplicating work that happens later in PreviewShaderBuilder, which is rotten.
            appContext.toolchain.openShader(props.shader!!).shaderType
        }

        val preview = if (shaderType == ProjectionShader) {
            val canvas2d = canvasEl
            val canvas3d = document.createElement("canvas") as HTMLCanvasElement
            val glslContext = GlBase.jsManager.createContext(canvas3d)
            gl = glslContext

            ProjectionPreview(canvas2d, glslContext, canvasEl.width, canvasEl.height, appContext.webClient.model) {
                preRenderHook.current()
            }
        } else {
            val glslContext = GlBase.jsManager.createContext(canvasEl)
            gl = glslContext
            QuadPreview(glslContext, canvasEl.width, canvasEl.height) {
                preRenderHook.current()
            }
        }

        val intersectionObserver = IntersectionObserver { entries ->
            if (entries.any { it.isIntersecting }) {
                preview.start()
            } else {
                preview.stop()
            }
        }
        intersectionObserver.observe(canvasEl)

        shaderPreview = preview

        withCleanup {
            intersectionObserver.disconnect()
            preview.destroy()
        }
    }

    val builder = memo(gl, props.shader, props.previewShaderBuilder) {
        gl?.let {
            props.previewShaderBuilder
                ?: PreviewShaderBuilder(props.shader!!, appContext.toolchain, appContext.webClient.model)
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

    useResizeListener(canvas) {
        // Tell Kotlin controller the window was resized
        shaderPreview?.resize(canvas.current!!.width, canvas.current!!.height)
    }

    styledDiv {
        css { +ShaderPreviewStyles.container }
        css.width = width
        css.height = height

        canvas {
            ref = canvas
            attrs.width = width.toString()
            attrs.height = height.toString()
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
}

fun RBuilder.shaderPreview(handler: RHandler<ShaderPreviewProps>) =
    child(ShaderPreview, handler = handler)