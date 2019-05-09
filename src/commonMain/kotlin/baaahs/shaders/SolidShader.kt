package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

class SolidShader() : Shader<SolidShader.Buffer>(ShaderId.SOLID) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createImpl(pixels: Pixels): Impl = Impl(pixels)

    companion object {
        fun parse(reader: ByteArrayReader) = SolidShader()
    }

    inner class Buffer : ShaderBuffer {
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

    class Impl(val pixels: Pixels) : ShaderImpl<Buffer> {
        private val colors = Array(pixels.count) { Color.WHITE }

        override fun draw(buffer: Buffer) {
            for (i in colors.indices) {
                colors[i] = buffer.color
            }
            pixels.set(colors)
        }
    }
}
