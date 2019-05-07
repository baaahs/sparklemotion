package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.random.Random

class SparkleShader : Shader(ShaderType.SPARKLE) {
    override val buffer = SparkleShaderBuffer()

    override fun createImpl(pixels: Pixels): ShaderImpl = SparkleShaderImpl(buffer, pixels)

    companion object {
        fun parse(reader: ByteArrayReader) = SparkleShader()
    }
}

class SparkleShaderImpl(val buffer: SparkleShaderBuffer, val pixels: Pixels) : ShaderImpl {
    private val colors = Array(pixels.count) { Color.WHITE }

    override fun draw() {
        for (i in colors.indices) {
            colors[i] = if (Random.nextFloat() < buffer.sparkliness ) { buffer.color } else { Color.BLACK }
        }
        pixels.set(colors)
    }
}

class SparkleShaderBuffer : ShaderBuffer {
    var color: Color = Color.WHITE
    var sparkliness: Float = .1F

    override fun serialize(writer: ByteArrayWriter) {
        color.serialize(writer)
        writer.writeFloat(sparkliness)
    }

    override fun read(reader: ByteArrayReader) {
        color = Color.parse(reader)
        sparkliness = reader.readFloat()
    }

}
