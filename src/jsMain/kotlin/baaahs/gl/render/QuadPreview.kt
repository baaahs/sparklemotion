package baaahs.gl.render

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT
import kotlin.browser.window

class QuadPreview(
    private val gl: GlContext,
    private var width: Int,
    private var height: Int,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    private var scene: Scene? = null

    override fun start() {
        running = true
        render()
    }

    override fun stop() {
        running = false
    }

    override fun destroy() {
        stop()
        scene?.release()
    }

    override fun setProgram(program: GlslProgram) {
        scene?.release()
        scene = null
        scene = Scene(program)
    }

    override fun render() {
        if (!running) return

        preRenderCallback?.invoke()
        scene?.render()
        window.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
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