package baaahs.glsl

import baaahs.Logger
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.GlslProgram
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
    fun setShaderSrc(src: String?, callback: (Array<CompiledShader.GlslError>) -> Unit = {}) {
        scene?.release()
        scene = null
        statusEl.clear()

        src?.let {
            try {
                scene = Scene(src).also {
                    statusEl.appendText("Inputs:\n")
                    it.links.forEach { (from, to) ->
                        if (from is GlslProgram.UserUniformInput) {
                            statusEl.appendText(from.toString())
                            statusEl.appendText("\n")
                        }
                    }
                }
                callback.invoke(emptyArray())
            } catch (e: CompiledShader.CompilationException) {
                callback.invoke(e.getErrors().toTypedArray())
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
        val patch = GlslProgram.autoWire(
            mapOf("color" to GlslAnalyzer().asShader(shaderSrc))
        )
        val links = patch.links
        private val program = GlslProgram(gl, patch).apply {
            setResolution(canvas.width.toFloat(), canvas.height.toFloat())
        }
        private var quad = Quad(gl, listOf(quadRect))
            .apply { bind(program.vertexAttribLocation) }

        fun render() {
            gl.runInContext {
                gl.check { viewport(0, 0, canvas.width, canvas.height) }
                gl.check { clearColor(1f, 0f, 0f, 1f) }
                gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

                program.bind()
                quad.prepareToRender(program.vertexAttribLocation) {
                    quad.renderRect(0)
                }
            }
        }

        fun release() {
            quad.release()
            program.release()
        }
    }

    companion object {
        val logger = Logger("GlslPreview")
    }
}