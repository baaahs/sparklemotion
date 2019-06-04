package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.PI
import kotlin.math.sin

/**
 * A shader that treats a surface's pixels as a linear strip and applies a configurable sine wave along the strip.
 */
class SineWaveShader() : Shader<SineWaveShader.Buffer>(ShaderId.SINE_WAVE) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createRenderer(surface: Surface): Shader.Renderer<Buffer> = Renderer()

    companion object : ShaderReader<SineWaveShader> {
        override fun parse(reader: ByteArrayReader) = SineWaveShader()
    }

    inner class Buffer : Shader.Buffer {
        override val shader: Shader<*>
            get() = this@SineWaveShader

        var color: Color = Color.WHITE
        var theta: Float = 0f
        var density: Float = 1f

        override fun serialize(writer: ByteArrayWriter) {
            color.serialize(writer)
            writer.writeFloat(theta)
            writer.writeFloat(density)
        }

        override fun read(reader: ByteArrayReader) {
            color = Color.parse(reader)
            theta = reader.readFloat()
            density = reader.readFloat()
        }
    }

    class Renderer : Shader.Renderer<Buffer> {
        override fun draw(buffer: Buffer, pixels: Pixels) {
            val theta = buffer.theta
            val pixelCount = pixels.size
            val density = buffer.density

            for (i in pixels.indices) {
                val v = sin(theta + 2 * PI * (i.toFloat() / pixelCount * density)) / 2 + .5
                pixels[i] = Color.BLACK.fade(buffer.color, v.toFloat())
            }
        }
    }
}
