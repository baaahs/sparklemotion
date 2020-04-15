package baaahs.glsl

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
    fun setShaderSrc(src: String?, errorCallback: (Array<CompiledShader.GlslError>) -> Unit = {}) {
        scene?.release()
        scene = null
        statusEl.clear()

        try {
            scene = createScene(src)
        } catch (e: CompiledShader.CompilationException) {
            errorCallback.invoke(e.getErrors().toTypedArray())
            statusEl.appendText(e.errorMessage)
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
        val patch = GlslProgram.Patch(
            mapOf(
                "color" to GlslAnalyzer().asShader(shaderSrc)
            ),
            listOf(
                GlslProgram.UvCoord to GlslProgram.ShaderPort("color", "gl_FragCoord"),
                GlslProgram.Resolution to GlslProgram.ShaderPort("color", "resolution"),
                GlslProgram.Time to GlslProgram.ShaderPort("color", "time"),
                GlslProgram.UniformInput("float", "blueness") to GlslProgram.ShaderPort("color", "blueness"),
                GlslProgram.UniformInput("float", "sm_beat") to GlslProgram.ShaderPort("color", "sm_beat"),
                GlslProgram.ShaderPort("color", "gl_FragColor") to GlslProgram.PixelColor
            )
        )
        private val program = GlslProgram(gl, patch)
        private var quad = Quad(gl, program.vertexAttribLocation, listOf(quadRect))

        fun render() {
            gl.runInContext {
                gl.check { viewport(0, 0, canvas.width, canvas.height) }
                gl.check { clearColor(1f, 0f, 0f, 1f) }
                gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

                program.bind()
                quad.prepareToRender {
                    quad.renderRect(0)
                }
            }
        }

        fun release() {
            quad.release()
            program.release()
        }
    }

    private fun createScene(shaderSrc: String?): Scene? {
        return if (shaderSrc != null && shaderSrc.isNotBlank()) {
            Scene(shaderSrc)
        } else {
            null
        }
    }
}