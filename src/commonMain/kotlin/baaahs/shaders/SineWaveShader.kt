package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.math.PI
import kotlin.math.sin

class SineWaveShader : Shader(ShaderType.SINE_WAVE) {
    override val buffer = SineWaveShaderBuffer()

    override fun createImpl(pixels: Pixels): ShaderImpl =
        SineWaveShaderImpl(buffer, pixels)

    companion object {
        fun parse(reader: ByteArrayReader) = SineWaveShader()
    }
}

class SineWaveShaderImpl(
    val buffer: SineWaveShaderBuffer,
    val pixels: Pixels
) : ShaderImpl {
    private val colors = Array(pixels.count) { Color.WHITE }

    override fun draw() {
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

class SineWaveShaderBuffer : ShaderBuffer {
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

    companion object {
        fun parse(reader: ByteArrayReader): SineWaveShaderBuffer {
            val buf = SineWaveShaderBuffer()
            buf.color = Color.parse(reader)
            buf.theta = reader.readFloat()
            buf.density = reader.readFloat()
            return buf
        }
    }
}