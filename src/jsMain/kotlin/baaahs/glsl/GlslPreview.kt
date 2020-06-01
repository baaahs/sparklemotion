package baaahs.glsl

import baaahs.glshaders.GlslProgram
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT
import kotlin.browser.window

class GlslPreview(
    private val gl: GlslContext,
    private var width: Int,
    private var height: Int
) {
    private var running = false
    private var scene: Scene? = null

    fun start() {
        running = true
        render()
    }

    fun stop() {
        running = false
    }

    fun destroy() {
        scene?.release()
    }

    @JsName("setShaderSrc")
    fun setProgram(program: GlslProgram) {
        scene?.release()
        scene = null
        scene = Scene(program)
    }

    fun render() {
        if (!running) return
        window.setTimeout({
            window.requestAnimationFrame { render() }
        }, 10)

        scene?.render()
    }

    @JsName("resize")
    fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        scene?.resize()
    }

    inner class Scene(private val program: GlslProgram) {
        private var quad = Quad(gl, listOf(quadRect))

        init { resize() }

        fun resize() {
            program.setResolution(width.toFloat(), height.toFloat())
        }

        fun render() {
            gl.runInContext {
                gl.check { viewport(0, 0, width, height) }
                gl.check { clearColor(1f, 0f, 0f, 1f) }
                gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

                program.updateUniforms()
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
        private val quadRect = Quad.Rect(1f, -1f, -1f, 1f)
    }
}