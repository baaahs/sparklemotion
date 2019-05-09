package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.random.Random

class SparkleShader : Shader<SparkleShader.Buffer>(ShaderId.SPARKLE) {
    override fun createBuffer(surface: Surface): Buffer = Buffer()

    override fun readBuffer(reader: ByteArrayReader): Buffer = Buffer().apply { read(reader) }

    override fun createImpl(pixels: Pixels): ShaderImpl<Buffer> = Impl(pixels)

    companion object {
        fun parse(reader: ByteArrayReader) = SparkleShader()
    }

    inner class Buffer : ShaderBuffer {
        override val shader: Shader<*> = this@SparkleShader

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

    class Impl(val pixels: Pixels) : ShaderImpl<Buffer> {
        private val colors = Array(pixels.count) { Color.WHITE }

        override fun draw(buffer: Buffer) {
            for (i in colors.indices) {
                colors[i] = if (Random.nextFloat() < buffer.sparkliness ) { buffer.color } else { Color.BLACK }
            }
            pixels.set(colors)
        }
    }

}
