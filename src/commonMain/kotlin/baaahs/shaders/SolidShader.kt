package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

class SolidShader : Shader(ShaderId.SOLID) {
    override val buffer = SolidShaderBuffer()

    override fun createImpl(pixels: Pixels): ShaderImpl = SolidShaderImpl(buffer, pixels)

    companion object {
        fun parse(reader: ByteArrayReader) = SolidShader()
    }
}

class SolidShaderImpl(val buffer: SolidShaderBuffer, val pixels: Pixels) : ShaderImpl {
    private val colors = Array(pixels.count) { Color.WHITE }

    override fun draw() {
        for (i in colors.indices) {
            colors[i] = buffer.color
        }
        pixels.set(colors)
    }
}

class SolidShaderBuffer : ShaderBuffer {
    var color: Color = Color.WHITE

    override fun serialize(writer: ByteArrayWriter) {
        color.serialize(writer)
    }

    override fun read(reader: ByteArrayReader) {
        color = Color.parse(reader)
    }
}