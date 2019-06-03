package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

/**
 * A shader that sets all pixels to a single color.
 */
class SolidShader : Shader<SolidShader.Buffer>(ShaderId.SOLID) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(surface: Surface): Renderer = Renderer()

    companion object : ShaderReader<SolidShader> {
        override fun parse(reader: ByteArrayReader) = SolidShader()
    }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@SolidShader

        var color: Color = Color.WHITE

        override fun serialize(writer: ByteArrayWriter) {
            color.serialize(writer)
        }

        override fun read(reader: ByteArrayReader) {
            color = Color.parse(reader)
        }
    }

    class Renderer : Shader.Renderer<Buffer> {

        override fun draw(buffer: Buffer, pixels: Pixels) {
            for (i in pixels.indices) {
                pixels[i] = buffer.color
            }
        }
    }
}
