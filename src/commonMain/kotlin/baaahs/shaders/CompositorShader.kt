package baaahs.shaders

import baaahs.*

class CompositorShader(val aShader: Shader, val bShader: Shader) : Shader(ShaderType.COMPOSITOR) {
    override val buffer = CompositorShaderBuffer()

    override fun serialize(writer: ByteArrayWriter) {
        super.serialize(writer)
        aShader.serialize(writer)
        bShader.serialize(writer)
    }

    override fun serializeBuffer(writer: ByteArrayWriter) {
        super.serializeBuffer(writer)
        aShader.serializeBuffer(writer)
        bShader.serializeBuffer(writer)
    }

    override fun createImpl(pixels: Pixels): ShaderImpl = CompositorShaderImpl(aShader, bShader, buffer, pixels)

    override fun readBuffer(reader: ByteArrayReader) {
        super.readBuffer(reader)
        aShader.readBuffer(reader)
        bShader.readBuffer(reader)
    }

    companion object {
        fun parse(reader: ByteArrayReader): CompositorShader {
            val shaderA = Shader.parse(reader)
            val shaderB = Shader.parse(reader)
            return CompositorShader(shaderA, shaderB)
        }
    }
}

class CompositorShaderImpl(
    val aShader: Shader,
    val bShader: Shader,
    val buffer: CompositorShaderBuffer,
    val pixels: Pixels
) : ShaderImpl {
    private val colors = Array(pixels.count) { Color.WHITE }
    private val aPixels = PixelBuf(pixels.count)
    private val bPixels = PixelBuf(pixels.count)
    private val shaderAImpl: ShaderImpl = aShader.createImpl(aPixels)
    private val shaderBImpl: ShaderImpl = bShader.createImpl(bPixels)

    override fun draw() {
        shaderAImpl.draw()
        shaderBImpl.draw()

        val operation: (aColor: Color, bColor: Color) -> Color
        operation = when (buffer.mode) {
            CompositingMode.ADD -> { a, b -> a.plus(b) }
            CompositingMode.OVERLAY -> { a, b -> b }
        }

        for (i in colors.indices) {
            val aColor = aPixels.colors[i]
            val bColor = bPixels.colors[i]

            colors[i] = aColor.fade(operation(aColor, bColor), buffer.fade)
        }
        pixels.set(colors)
    }
}

class PixelBuf(override val count: Int) : Pixels {
    val colors = Array(count) { Color.WHITE }

    override fun set(colors: Array<Color>) {
        colors.copyInto(this.colors)
    }
}

class CompositorShaderBuffer(
    var mode: CompositingMode = CompositingMode.OVERLAY,
    var fade: Float = 0.5f
) : ShaderBuffer {
    override fun serialize(writer: ByteArrayWriter) {
        writer.writeByte(mode.ordinal.toByte())
        writer.writeFloat(fade)
    }

    override fun read(reader: ByteArrayReader) {
        mode = CompositingMode.get(reader.readByte())
        fade = reader.readFloat()
    }
}

enum class CompositingMode {
    OVERLAY,
    ADD;

    companion object {
        val values = values()
        fun get(i: Byte) = values[i.toInt()]
    }
}