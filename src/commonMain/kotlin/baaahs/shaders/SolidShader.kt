package baaahs.shaders

import baaahs.Color
import baaahs.Surface

/**
 * A shader that sets all pixels to a single color.
 */
class SolidShader : Shader<SolidShader.Buffer>(ShaderId.SOLID) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun createRenderer(surface: Surface): Renderer = Renderer()

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@SolidShader

        var color: Color = Color.WHITE
    }

    class Renderer : Shader.Renderer<Buffer> {
        override fun draw(buffer: Buffer, pixelIndex: Int): Color = buffer.color
    }
}
