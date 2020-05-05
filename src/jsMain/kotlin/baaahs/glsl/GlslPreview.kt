package baaahs.glsl

import baaahs.*
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Plugins
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT
import org.w3c.dom.Element
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.dom.appendText
import kotlin.dom.clear

class GlslPreview(
    private val baseEl: Element,
    private val statusEl: Element,
    shaderSrc: String? = null
) {
    private var running = false
    private val canvas = document.createElement("canvas") as HTMLCanvasElement
    private val gl = GlslBase.jsManager.createContext(canvas)
    private val quadRect = Quad.Rect(1f, -1f, -1f, 1f)
    private var scene: Scene? = null

    init {
        baseEl.appendChild(canvas)
        setShaderSrc(shaderSrc)
    }

    fun start() {
        running = true
        render()
    }

    fun stop() {
        running = false
    }

    fun destroy() {
        baseEl.removeChild(canvas)
    }

    @JsName("setShaderSrc")
    fun setShaderSrc(
        src: String?,
        callback: (Array<GadgetData>, Array<CompiledShader.GlslError>) -> Unit = { _, _ -> }
    ) {
        scene?.release()
        scene = null
        statusEl.clear()

        src?.let {
            try {
                scene = Scene(src).also {
                    statusEl.appendText("Inputs:\n")
                    it.links.forEach { (from, to) ->
                        if (from is GlslProgram.InputPortRef) {
                            statusEl.appendText(from.toString())
                            statusEl.appendText("\n")
                        }
                    }

                    val gadgetDatas = it.gadgets.entries.map { (name, gadget) ->
                        GadgetData(name, gadget, "/preview/gadgets/$name")
                    }.toTypedArray()
                    callback.invoke(gadgetDatas, emptyArray())
                }
            } catch (e: CompiledShader.CompilationException) {
                callback.invoke(emptyArray(), e.getErrors().toTypedArray())
                statusEl.appendText(e.errorMessage)
            } catch (e: Exception) {
                statusEl.appendText(e.message ?: e.toString())
                logger.error("failed to compile shader", e)
            }
        }
    }

    fun render() {
        if (!running) return
        window.setTimeout({
            window.requestAnimationFrame { render() }
        }, 10)

        scene?.render()
    }

    @JsName("resize")
    fun resize() {
        console.log("resize!!!")
    }

    inner class Scene(shaderSrc: String) {
        val patch = AutoWirer().autoWire(
            mapOf("color" to GlslAnalyzer().asShader(shaderSrc))
        )
        val links = patch.links
        private val program = GlslProgram(gl, patch)
        val gadgets = linkedMapOf<String, Gadget>()

        init {
            val plugins = Plugins.findAll()
            program.bind { uniformPort ->
                plugins.matchUniformProvider(uniformPort, program, FakeShowContext())
            }
            program.setResolution(canvas.width.toFloat(), canvas.height.toFloat())
        }

        private var quad = Quad(gl, listOf(quadRect))
            .apply { bind(program.vertexAttribLocation) }

        fun render() {
            gl.runInContext {
                gl.check { viewport(0, 0, canvas.width, canvas.height) }
                gl.check { clearColor(1f, 0f, 0f, 1f) }
                gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

                program.prepareToDraw()
                quad.prepareToRender(program.vertexAttribLocation) {
                    quad.renderRect(0)
                }
            }
        }

        fun release() {
            quad.release()
            program.release()
        }

        inner class FakeShowContext : ShowContext {
            override val allSurfaces: List<Surface>
                get() = TODO("not implemented")
            override val allUnusedSurfaces: List<Surface>
                get() = TODO("not implemented")
            override val allMovingHeads: List<MovingHead>
                get() = TODO("not implemented")
            override val currentBeat: Float
                get() = TODO("not implemented")

            override fun getBeatSource(): BeatSource {
                TODO("not implemented")
            }

            override fun <B : Shader.Buffer> getShaderBuffer(surface: Surface, shader: Shader<B>): B {
                TODO("not implemented")
            }

            override fun getCompositorBuffer(
                surface: Surface,
                bufferA: Shader.Buffer,
                bufferB: Shader.Buffer,
                mode: CompositingMode,
                fade: Float
            ): CompositorShader.Buffer {
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

    }

    companion object {
        val logger = Logger("GlslPreview")
    }
}