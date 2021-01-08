package baaahs.gl.preview

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.PreviewRenderEngine
import baaahs.window

class QuadPreview(
    private val gl: GlContext,
    private var width: Int,
    private var height: Int,
    private val preRenderCallback: (() -> Unit)? = null
) : ShaderPreview {
    private var running = false
    override var renderEngine = PreviewRenderEngine(gl, width, height)

    override fun start() {
        running = true
        render()
    }

    override fun stop() {
        running = false
    }

    override fun destroy() {
        stop()
        renderEngine.release()
    }

    override fun setProgram(program: GlslProgram?) {
        renderEngine.useProgram(program)
    }

    override fun render() {
        if (!running) return

        preRenderCallback?.invoke()
        renderEngine.render()
        window.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        renderEngine.onResize(width, height)
    }
}