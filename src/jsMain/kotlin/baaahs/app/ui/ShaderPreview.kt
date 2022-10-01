package baaahs.app.ui

import baaahs.gl.GlContext
import baaahs.gl.Toolchain
import baaahs.gl.openShader
import baaahs.gl.preview.GadgetAdjuster
import baaahs.gl.preview.PreviewShaderBuilder
import baaahs.gl.preview.ShaderBuilder
import baaahs.gl.preview.ShaderPreview
import baaahs.show.Shader
import baaahs.ui.addObserver
import baaahs.ui.inPixels
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import baaahs.util.useResizeListener
import external.IntersectionObserver
import kotlinx.css.*
import kotlinx.html.js.onClickFunction
import materialui.icon
import mui.material.Typography
import mui.system.sx
import org.w3c.dom.Element
import org.w3c.dom.HTMLDivElement
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.StyleSheet
import styled.inlineStyles

val ShaderPreview = xComponent<ShaderPreviewProps>("ShaderPreview") { props ->
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
        toolchain.openShader(props.shader!!).shaderType
    }
    val bootstrapper = shaderType.shaderPreviewBootstrapper
    val helper = memo(bootstrapper, sharedGlContext) {
//        console.log("Rememoize helper for ${props.shader?.title ?: props.previewShaderBuilder?.openShader?.title}")
        bootstrapper.createHelper(sharedGlContext)
    }
    val previewContainer = helper.container
    val sceneProvider = appContext.sceneProvider
    observe(sceneProvider)
    val model = sceneProvider.openScene?.model
//    var model by state { sceneManager.openScene?.model }
//    onMount {
//        val listener = sceneManager.addSceneChangeListener { newScene ->
//            model = newScene?.model
//        }
//        withCleanup { sceneManager.removeSceneChangeListener(listener) }
//    }

    var gl by state<GlContext?> { null }

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

    onChange("shader type", helper, shaderType, model) {
        model?.let { model ->
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
                ?: PreviewShaderBuilder(props.shader!!, toolchain, appContext.webClient.sceneProvider)
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

        if (builder.state == ShaderBuilder.State.Unbuilt) {
            builder.startBuilding()
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

        when (builder?.state ?: ShaderBuilder.State.Unbuilt) {
            ShaderBuilder.State.Unbuilt,
            ShaderBuilder.State.Analyzing,
            ShaderBuilder.State.Linking,
            ShaderBuilder.State.Linked,
            ShaderBuilder.State.Compiling -> {
                div { +"Building…" }
            }

            ShaderBuilder.State.Success -> {
            }

            ShaderBuilder.State.Errors -> {
                div(+ShaderPreviewStyles.errorBox) {
                    attrs.onClickFunction = { event ->
                        errorPopupAnchor = event.currentTarget as Element?
                        event.stopPropagation()
                    }

                    icon(mui.icons.material.Warning)
                    Typography {
                        attrs.sx {
                            display = csstype.Display.block
                        }
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
        userSelect = UserSelect.none

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
    child(ShaderPreview, handler = handler)