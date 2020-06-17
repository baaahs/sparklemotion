package baaahs.ui

import baaahs.Gadget
import baaahs.GadgetData
import baaahs.PubSub
import baaahs.ShowResources
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Patch
import baaahs.glshaders.Plugins
import baaahs.glshaders.ShaderFragment
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslPreview
import baaahs.jsx.useResizeListener
import baaahs.show.Show
import org.w3c.dom.HTMLCanvasElement
import react.*
import react.dom.canvas

val PatchPreview = functionalComponent<PatchPreviewProps> { props ->
    val canvas = useRef<HTMLCanvasElement>()
    var gl by useState<GlslContext>(nuffin())
    var glslPreview by useState<GlslPreview>(nuffin())

    val compile = useCallback({ patch: Patch ->
        val showResources = object : ShowResources {
            val gadgets: MutableMap<String, Gadget> = hashMapOf()
            override val plugins: Plugins
                get() = Plugins.safe()
            override val glslContext: GlslContext get() = gl
            override val currentShowTopic: PubSub.Topic<Show>
                get() = TODO("Not yet implemented")
            override val dataFeeds: Map<String, GlslProgram.DataFeed>
                get() = emptyMap()
            override val shaders: Map<String, ShaderFragment>
                get() = patch.components.mapValues { (_, component) -> component.shaderFragment }

            override fun <T : Gadget> createdGadget(id: String, gadget: T) {
                gadgets[id] = gadget
            }

            override fun <T : Gadget> useGadget(id: String): T {
                return gadgets[id] as T
            }

        }
        val program =
            try {
                patch.compile(gl) { dataSource ->
                    dataSource.create(showResources)
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

    useEffect(props.patch, glslPreview, name = "patch") {
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
    var patch: Patch?
    var onSuccess: () -> Unit
    var onGadgetsChange: (Array<GadgetData>) -> Unit
    var onError: (Array<CompiledShader.GlslError>) -> Unit
}

fun RBuilder.patchPreview(handler: PatchPreviewProps.() -> Unit): ReactElement =
    child(PatchPreview) { attrs { handler() } }