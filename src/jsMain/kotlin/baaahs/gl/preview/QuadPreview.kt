package baaahs.gl.preview

import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.PreviewRenderEngine
import baaahs.window

class QuadPreview(
    gl: GlContext,
    private var width: Int,
    private var height: Int,
    private val preRenderCallback: ((QuadPreview) -> Unit)? = null
) : ShaderPreview {
    private var running = false
    override var renderEngine = PreviewRenderEngine(gl, width, height)
    private var offsetLeft = 0
    private var offsetBottom = 0

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
        window.requestAnimationFrame { render() }
    }

    override fun resize(width: Int, height: Int) {
        this.width = width
        this.height = height
        renderEngine.onResize(width, height)
    }

    override fun rasterOffsetChanged(left: Int, bottom: Int) {
        if (offsetLeft != left || offsetBottom != bottom) {
            renderEngine.onRasterOffsetChange(left, bottom)
            offsetLeft = left
            offsetBottom = bottom
        }
    }
}