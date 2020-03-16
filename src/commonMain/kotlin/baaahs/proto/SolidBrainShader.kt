package baaahs.proto

import baaahs.Color
import baaahs.Surface
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

/**
 * A shader that sets all pixels to a single color.
 */
class SolidBrainShader : BrainShader<SolidBrainShader.Buffer>(BrainShaderId.SOLID) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(surface: Surface): Renderer = Renderer()

    companion object : ShaderReader<SolidBrainShader> {
        override fun parse(reader: ByteArrayReader) = SolidBrainShader()
    }

    inner class Buffer : BrainShader.Buffer {
        override val shader: BrainShader<*>
            get() = this@SolidBrainShader

        var color: Color = Color.WHITE

        override fun serialize(writer: ByteArrayWriter) {
            color.serialize(writer)
        }

        override fun read(reader: ByteArrayReader) {
            color = Color.parse(reader)
        }
    }

    class Renderer : BrainShader.Renderer<Buffer> {
        override fun draw(buffer: Buffer, pixelIndex: Int): Color = buffer.color
    }
}
