package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

/**
 * A shader that allows control of individual pixels' colors directly from a show.
 *
 * This is a suboptimal shader for most purposes, consider writing a custom shader instead!
 */
class PixelShader() : Shader<PixelShader.Buffer>(ShaderId.PIXEL) {

    override fun createBuffer(surface: Surface): Buffer = Buffer(surface.pixelCount)

    override fun createRenderer(surface: Surface, pixels: Pixels): Shader.Renderer<Buffer> = Renderer(pixels)

    override fun readBuffer(reader: ByteArrayReader): Buffer {
        val incomingColorCount = reader.readInt()
        val buf = Buffer(incomingColorCount)
        (0 until incomingColorCount).forEach { index -> buf.colors[index] = Color.parse(reader) }
        return buf
    }

    companion object {
        fun parse(reader: ByteArrayReader) = PixelShader()
    }

    inner class Buffer(pixelCount: Int) : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@PixelShader

        var colors: Array<Color> = Array(pixelCount) { Color.WHITE }

        override fun serialize(writer: ByteArrayWriter) {
            writer.writeInt(colors.size)
            colors.forEach { color -> color.serialize(writer) }
        }

        override fun read(reader: ByteArrayReader) {
            val incomingColorCount = reader.readInt()
            if (incomingColorCount != colors.size) {
                throw IllegalStateException("incoming color count ($incomingColorCount) doesn't match buffer (${colors.size}")
            }
            (0 until incomingColorCount).forEach { index -> colors[index] = Color.parse(reader) }
        }

        fun setAll(color: Color) {
            for (i in colors.indices) {
                colors[i] = color
            }
        }
    }

    class Renderer(val pixels: Pixels) : Shader.Renderer<Buffer> {
        override fun draw(buffer: Buffer) {
            pixels.set(buffer.colors)
        }
    }

}
