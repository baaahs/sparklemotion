package baaahs.ui

import baaahs.BaseShowResources
import baaahs.Gadget
import baaahs.GadgetData
import baaahs.glshaders.DataBinding
import baaahs.glshaders.OpenPatch
import baaahs.glshaders.Plugins
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslPreview
import baaahs.jsx.useResizeListener
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas

val PatchPreview = functionalComponent<PatchPreviewProps> { props ->
    val canvas = useRef<HTMLCanvasElement>()
    var gl by useState<GlslContext>(nuffin())
    var glslPreview by useState<GlslPreview>(nuffin())

    val compile = useCallback({ openPatch: OpenPatch ->
        val showResources = object : BaseShowResources(Plugins.safe()) {
            val gadgets: MutableMap<String, Gadget> = hashMapOf()
            override val glslContext: GlslContext get() = gl

            override fun <T : Gadget> createdGadget(id: String, gadget: T) {
                gadgets[id] = gadget
            }

            override fun <T : Gadget> useGadget(id: String): T {
                return gadgets[id] as T
            }

        }
        val program =
            try {
                openPatch.compile(gl) { dataSource ->
                    val id = dataSource.suggestId()
                    DataBinding(id, dataSource.createFeed(showResources, id))
                }.also {
                    props.onSuccess()
                }
            } catch (e: CompiledShader.CompilationException) {
                props.onError.invoke(e.getErrors().toTypedArray())
                null
            } catch (e: Exception) {
                val glslError = CompiledShader.GlslError(e.message ?: e.toString())
                props.onError.invoke(arrayOf(glslError))
                null
            }
        props.onGadgetsChange(showResources.gadgets.map { (id, gadget) ->
            GadgetData(id, gadget, "/preview/gadgets/$id")
        }.toTypedArray())
        program
    }, arrayOf(gl, props.onError))

    useEffectWithCleanup(arrayListOf(canvas)) {
        val canvasEl = canvas.current
        val glslContext = GlslBase.jsManager.createContext(canvasEl)
        gl = glslContext

        val preview = GlslPreview(glslContext, canvasEl.width, canvasEl.height)
        preview.start()
        glslPreview = preview

        return@useEffectWithCleanup {
            preview.destroy()
        }
    }

    useEffect(props.patch, glslPreview, gl, name = "patch") {
        if (gl == null) return@useEffect
        props.patch?.let { patch ->
            compile(patch)?.let { program ->
                glslPreview.setProgram(program)
            }
        }
    }

    useResizeListener(canvas) {
        // Tell Kotlin controller the window was resized
        glslPreview?.resize(canvas.current.width, canvas.current.height)
    }

    canvas {
        ref = canvas
    }
}

external interface PatchPreviewProps : RProps {
    var patch: OpenPatch?
    var onSuccess: () -> Unit
    var onGadgetsChange: (Array<GadgetData>) -> Unit
    var onError: (Array<CompiledShader.GlslError>) -> Unit
}

fun RBuilder.patchPreview(handler: PatchPreviewProps.() -> Unit): ReactElement =
    child(PatchPreview) { attrs { handler() } }