package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.min

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

    companion object : ShaderReader<PixelShader> {
        override fun parse(reader: ByteArrayReader) = PixelShader()
    }

    inner class Buffer(pixelCount: Int) : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@PixelShader

        var colors: Array<Color>

        init {
            val bufPixelCount = if (pixelCount == SparkleMotion.PIXEL_COUNT_UNKNOWN) {
                SparkleMotion.MAX_PIXEL_COUNT
            } else {
                pixelCount
            }
            colors = Array(bufPixelCount) { Color.WHITE }
        }

        override fun serialize(writer: ByteArrayWriter) {
            writer.writeInt(colors.size)
            colors.forEach { color -> color.serialize(writer) }
        }

        override fun read(reader: ByteArrayReader) {
            val incomingColorCount = reader.readInt()

            // if there are more colors in the buffer than pixels, drop from the end
            val countFromBuffer = min(colors.size, incomingColorCount)
            for (i in 0 until countFromBuffer) {
                colors[i] = Color.parse(reader)
            }

            // if there are more pixels than colors in the buffer, repeat
            for (i in countFromBuffer until colors.size) {
                colors[i] = colors[i % countFromBuffer]
            }
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
