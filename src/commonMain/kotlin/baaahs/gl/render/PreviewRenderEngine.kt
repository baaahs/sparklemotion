package baaahs.gl.render

import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeed
import baaahs.gl.glsl.GlslProgram
import com.danielgergely.kgl.GL_COLOR_BUFFER_BIT
import com.danielgergely.kgl.GL_DEPTH_BUFFER_BIT

class PreviewRenderEngine(
    gl: GlContext,
    private var width: Int,
    private var height: Int
) : RenderEngine(gl) {
    private var quad = calcQuad(width, height)
    private var program: GlslProgram? = null

    private fun calcQuad(height: Int, width: Int) =
        gl.runInContext { Quad(gl, listOf(Quad.Rect(height.toFloat(), 0f, 0f, width.toFloat()))) }

    fun useProgram(glslProgram: GlslProgram?) {
        this.program?.release()

        this.program = glslProgram
        glslProgram?.setResolution()
    }

    private fun GlslProgram.setResolution() {
        setPixDimens(width, height)
        setResolution(width.toFloat(), height.toFloat())
    }

    fun onResize(width: Int, height: Int) {
        this.width = width
        this.height = height
        program?.setResolution()
        quad = calcQuad(height, width)
    }

    override fun onBind(engineFeed: EngineFeed) {
    }

    override fun beforeRender() {
    }

    override fun bindResults() {
    }

    public override fun render() {
        val program = program ?: return

        gl.useProgram(program)
        program.aboutToRenderFrame()

        gl.check { viewport(0, 0, width, height) }
        gl.check { clearColor(1f, 0f, 0f, 1f) }
        gl.check { clear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT) }

        program.aboutToRenderFrame()
        quad.prepareToRender(program.vertexAttribLocation) {
            quad.renderRect(0)
        }
    }

    override fun afterRender() {
    }

    override suspend fun awaitResults() {
    }

    override fun onRelease() {
        program?.release()
        quad.release()
    }
}