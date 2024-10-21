package baaahs.gl.preview

import baaahs.gl.GlBase
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.PreviewRenderEngine

class QuadPreview(
    private val gl: GlBase.JsGlContext,
    private var width: Int,
    private var height: Int,
    private val preRenderCallback: ((ShaderPreview) -> Unit)? = null
) : ShaderPreview {
    private var running = false
    override val renderEngine = PreviewRenderEngine(gl, width, height)
    override var program: GlslProgram? = null
        set(value) {
            field = value
            renderEngine.useProgram(value)
        }

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

    override fun render() {
        if (!running) return

        preRenderCallback?.invoke(this)
        renderEngine.render()

        gl.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        if (this.width == width && this.height == height) return
        this.width = width
        this.height = height
        renderEngine.onResize(width, height)
    }
}