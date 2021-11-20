package baaahs.shaders

import baaahs.Color
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.sm.brain.proto.BrainShader
import baaahs.sm.brain.proto.BrainShaderId
import baaahs.sm.brain.proto.BrainShaderReader

/**
 * A shader that sets all pixels to a single color.
 */
class SolidBrainShader : BrainShader<SolidBrainShader.Buffer>(BrainShaderId.SOLID) {
    override fun createBuffer(pixelCount: Int): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(): Renderer = Renderer()

    companion object : BrainShaderReader<SolidBrainShader> {
        override fun parse(reader: ByteArrayReader) = SolidBrainShader()
    }

    inner class Buffer : BrainShader.Buffer {
        override val brainShader: BrainShader<*>
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
        override fun draw(buffer: Buffer, pixelIndex: Int): Color? = buffer.color
    }
}
