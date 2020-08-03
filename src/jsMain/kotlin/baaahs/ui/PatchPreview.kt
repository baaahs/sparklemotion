package baaahs.ui

import baaahs.BaseShowPlayer
import baaahs.Gadget
import baaahs.GadgetData
import baaahs.getBang
import baaahs.glshaders.OpenPatch
import baaahs.glshaders.Plugins
import baaahs.glsl.*
import baaahs.jsx.useResizeListener
import baaahs.model.ModelInfo
import external.IntersectionObserver
import kotlinx.css.LinearDimension
import kotlinx.css.px
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas

val PatchPreview = xComponent<PatchPreviewProps>("PatchPreview") { props ->
    val canvas = useRef<HTMLCanvasElement?>()
    var gl by useState<GlslContext?>(null)
    var glslPreview by useState<GlslPreview?>(null)

    onMount(canvas.current) {
        val canvasEl = canvas.current ?: return@onMount
        val glslContext = GlslBase.jsManager.createContext(canvasEl)
        gl = glslContext

        val preview = GlslPreview(glslContext, canvasEl.width, canvasEl.height)

        val intersectionObserver = IntersectionObserver { entries ->
            if (entries.any { it.isIntersecting }) {
                preview.start()
            } else {
                preview.stop()
            }
        }
        intersectionObserver.observe(canvasEl)

        glslPreview = preview

        withCleanup {
            intersectionObserver.disconnect()
            preview.destroy()
        }
    }

    onChange("patch change", props.patch, gl, glslPreview) {
        if (gl == null) return@onChange
        val patch = props.patch ?: return@onChange

        val showPlayer = object : BaseShowPlayer(Plugins.safe(), ModelInfo.Empty) {
            val gadgets: MutableMap<String, Gadget> = hashMapOf()
            override val glslContext: GlslContext get() = gl!!

            override fun <T : Gadget> createdGadget(id: String, gadget: T) {
                gadgets[id] = gadget
            }

            override fun <T : Gadget> useGadget(id: String): T {
                val gadget = gadgets.getBang(id, "gadget")
                return gadget as? T ?: error("$id isn't a gadget: $gadget")
            }
        }

        later {
            try {
                patch.compile(gl!!) { id, dataSource ->
                    dataSource.createFeed(showPlayer, id)
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

            val gadgets = showPlayer.gadgets.map { (id, gadget) ->
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
        attrs.width = (props.width ?: 150.px).toString()
        attrs.height = (props.height ?: 100.px).toString()
    }
}

external interface PatchPreviewProps : RProps {
    var patch: OpenPatch?
    var width: LinearDimension?
    var height: LinearDimension?
    var onSuccess: () -> Unit
    var onGadgetsChange: (Array<GadgetData>) -> Unit
    var onError: (Array<GlslError>) -> Unit
}

fun RBuilder.patchPreview(handler: RHandler<PatchPreviewProps>): ReactElement =
    child(PatchPreview, handler = handler)