package baaahs.ui

import baaahs.BaseShowResources
import baaahs.Gadget
import baaahs.GadgetData
import baaahs.glshaders.OpenPatch
import baaahs.glshaders.Plugins
import baaahs.glsl.*
import baaahs.jsx.useResizeListener
import baaahs.model.ModelInfo
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas

val PatchPreview = xComponent<PatchPreviewProps>("PatchPreview") { props ->
    val canvas = useRef<HTMLCanvasElement?>()
    var gl by useState<GlslContext?>(null)
    var glslPreview by useState<GlslPreview?>(null)

    sideEffect("canvas change", canvas.current) {
        val canvasEl = canvas.current ?: return@sideEffect
        val glslContext = GlslBase.jsManager.createContext(canvasEl)
        gl = glslContext

        val preview = GlslPreview(glslContext, canvasEl.width, canvasEl.height)
        preview.start()
        glslPreview = preview

        withCleanup {
            preview.destroy()
        }
    }

    sideEffect("patch change", props.patch, glslPreview) {
        if (gl == null) return@sideEffect
        val patch = props.patch ?: return@sideEffect

        val showResources = object : BaseShowResources(Plugins.safe(), ModelInfo.Empty) {
            val gadgets: MutableMap<String, Gadget> = hashMapOf()
            override val glslContext: GlslContext get() = gl!!

            override fun <T : Gadget> createdGadget(id: String, gadget: T) {
                gadgets[id] = gadget
            }

            override fun <T : Gadget> useGadget(id: String): T {
                @Suppress("UNCHECKED_CAST")
                return gadgets[id] as T
            }
        }

        later {
            try {
                patch.compile(gl!!) { id, dataSource ->
                    dataSource.createFeed(showResources, id)
                }.also { program ->
                    glslPreview!!.setProgram(program)
                    props.onSuccess()
                }
            } catch (e: GlslException) {
                props.onError.invoke(e.errors.toTypedArray())
            } catch (e: Exception) {
                logger.warn(e) { "Failed to compile patch." }
                props.onError.invoke(arrayOf(GlslError(e.message ?: e.toString())))
            }

            val gadgets = showResources.gadgets.map { (id, gadget) ->
                GadgetData(id, gadget, "/preview/gadgets/$id")
            }.toTypedArray()
            props.onGadgetsChange(gadgets)
        }
    }

    useResizeListener(canvas) {
        // Tell Kotlin controller the window was resized
        glslPreview?.resize(canvas.current!!.width, canvas.current!!.height)
    }

    canvas {
        ref = canvas
    }
}

external interface PatchPreviewProps : RProps {
    var patch: OpenPatch?
    var onSuccess: () -> Unit
    var onGadgetsChange: (Array<GadgetData>) -> Unit
    var onError: (Array<GlslError>) -> Unit
}

fun RBuilder.patchPreview(handler: PatchPreviewProps.() -> Unit): ReactElement =
    child(PatchPreview) { attrs { handler() } }