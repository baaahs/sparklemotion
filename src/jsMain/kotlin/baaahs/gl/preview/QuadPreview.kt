package baaahs.gl.preview

import baaahs.gl.GlBase
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.PreviewRenderEngine

class QuadPreview(
    private val gl: GlBase.JsGlContext,
    private var width: Int,
    private var height: Int,
    private val preRenderCallback: ((QuadPreview) -> Unit)? = null
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

        preRenderCallback?.invoke(this)
        renderEngine.render()

        gl.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        renderEngine.onResize(width, height)
    }
}