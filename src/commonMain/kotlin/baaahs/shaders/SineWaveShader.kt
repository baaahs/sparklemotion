package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.PI
import kotlin.math.sin

class SineWaveShader() : Shader<SineWaveShader.Buffer>(ShaderId.SINE_WAVE) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createImpl(pixels: Pixels): ShaderImpl<Buffer> = Impl(pixels)

    companion object {
        fun parse(reader: ByteArrayReader) = SineWaveShader()
    }

    inner class Buffer : ShaderBuffer {
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

    class Impl(val pixels: Pixels) : ShaderImpl<Buffer> {
        private val colors = Array(pixels.count) { Color.WHITE }

        override fun draw(buffer: Buffer) {
            val theta = buffer.theta
            val pixelCount = pixels.count.toFloat()
            val density = buffer.density

            for (i in colors.indices) {
                val v = sin(theta + 2 * PI * (i / pixelCount * density)) / 2 + .5
                colors[i] = Color.BLACK.fade(buffer.color, v.toFloat())
            }
            pixels.set(colors)
        }
    }
}
