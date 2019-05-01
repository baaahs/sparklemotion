package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

class PixelShader : Shader(ShaderType.PIXEL) {
    override val buffer = PixelShaderBuffer()

    override fun createImpl(pixels: Pixels): ShaderImpl = PixelShaderImpl(buffer, pixels)

    companion object {
        fun parse(reader: ByteArrayReader) = PixelShader()
    }
}

class PixelShaderImpl(val buffer: PixelShaderBuffer, val pixels: Pixels) : ShaderImpl {
    private val colors = Array(pixels.count) { Color.WHITE }

    override fun draw() {
        for (i in colors.indices) {
            colors[i] = buffer.colors[i]
        }
        pixels.set(colors)
    }
}

class PixelShaderBuffer : ShaderBuffer {
    private var fakeyTerribleHardCodedNumberOfPixels: Int = 1337
    var colors: Array<Color> = Array(fakeyTerribleHardCodedNumberOfPixels) { Color.WHITE }

    override fun serialize(writer: ByteArrayWriter) {
        writer.writeInt(colors.size)
        colors.forEach { color -> color.serialize(writer) }
    }

    override fun read(reader: ByteArrayReader) {
        val incomingColorCount = reader.readInt()
        (0 until incomingColorCount).forEach { index -> colors[index] = Color.parse(reader) }
    }

    fun setAll(color: Color) {
        for (i in colors.indices) { colors[i] = color }
    }
}