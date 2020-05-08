package baaahs

import baaahs.glsl.RenderSurface
import baaahs.shaders.GlslShader

class SurfaceBinder(val surface: Surface, val renderSurface: RenderSurface) {
    private var buffer: GlslShader.Buffer? = null
    val receivers = mutableListOf<ShowRunner.SurfaceReceiver>()

    fun release() {
        releaseBuffer()
    }

    fun setBuffer(buffer: GlslShader.Buffer) {
        if (this.buffer != null)
            throw IllegalStateException("buffer already bound for $surface")
        this.buffer = buffer
        this.renderSurface.program = buffer.shader.glslProgram
    }

    fun releaseBuffer() {
        buffer?.release()
        buffer = null
    }

    fun hasBuffer(): Boolean = buffer != null

    fun updateRenderSurface() {
        renderSurface.program = buffer?.shader?.glslProgram
    }
}
