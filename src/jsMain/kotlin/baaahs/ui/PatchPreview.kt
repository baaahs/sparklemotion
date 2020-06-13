package baaahs.ui

import baaahs.*
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Patch
import baaahs.glshaders.ShaderFragment
import baaahs.glsl.CompiledShader
import baaahs.glsl.GlslBase
import baaahs.glsl.GlslContext
import baaahs.glsl.GlslPreview
import baaahs.jsx.useResizeListener
import baaahs.model.MovingHead
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
            override val glslContext: GlslContext get() = gl
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
        val fakeShowContext = FakeShowContext()
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
        props.onGadgetsChange(fakeShowContext.gadgets.map { (name, gadget) ->
            GadgetData(name, gadget, "/preview/gadgets/$name")
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

private class FakeShowContext : ShowContext {
    val gadgets = linkedMapOf<String, Gadget>()

    override val allSurfaces: List<Surface>
        get() = TODO("not implemented")
    override val allMovingHeads: List<MovingHead>
        get() = TODO("not implemented")
    override val currentBeat: Float
        get() = TODO("not implemented")

    override fun getBeatSource(): BeatSource {
        TODO("not implemented")
    }

    override fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
        TODO("not implemented")
    }

    override fun <T : Gadget> getGadget(name: String, gadget: T): T {
        if (gadgets.containsKey(name)) {
            throw CompiledShader.LinkException("multiple gadgets with the same name ($name)")
        }
        gadgets[name] = gadget
        return gadget
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