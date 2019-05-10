package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

class CompositorShader(val aShader: Shader<*>, val bShader: Shader<*>) :
    Shader<CompositorShader.Buffer>(ShaderId.COMPOSITOR) {

    override fun createBuffer(surface: Surface) = Buffer(aShader.createBuffer(surface), bShader.createBuffer(surface))

    override fun serializeConfig(writer: ByteArrayWriter) {
        aShader.serialize(writer)
        bShader.serialize(writer)
    }

    override fun createImpl(pixels: Pixels): ShaderImpl<Buffer> = Impl(aShader, bShader, pixels)

    override fun readBuffer(reader: ByteArrayReader): Buffer =
        Buffer(
            aShader.readBuffer(reader),
            bShader.readBuffer(reader),
            CompositingMode.get(reader.readByte()),
            reader.readFloat()
        )

    fun createBuffer(aShaderBuffer: ShaderBuffer, bShaderBuffer: ShaderBuffer): Buffer =
        Buffer(aShaderBuffer, bShaderBuffer)

    companion object {
        fun parse(reader: ByteArrayReader): CompositorShader {
            val shaderA = Shader.parse(reader)
            val shaderB = Shader.parse(reader)
            return CompositorShader(shaderA, shaderB)
        }
    }

    inner class Buffer(
        val aShaderBuffer: ShaderBuffer, val bShaderBuffer: ShaderBuffer,
        var mode: CompositingMode = CompositingMode.OVERLAY,
        var fade: Float = 0.5f
    ) : ShaderBuffer {
        override val shader: Shader<*> = this@CompositorShader

        override fun serialize(writer: ByteArrayWriter) {
            aShaderBuffer.serialize(writer)
            bShaderBuffer.serialize(writer)
            writer.writeByte(mode.ordinal.toByte())
            writer.writeFloat(fade)
        }

        override fun read(reader: ByteArrayReader) {
            aShaderBuffer.read(reader)
            bShaderBuffer.read(reader)
            mode = CompositingMode.get(reader.readByte())
            fade = reader.readFloat()
        }
    }

    class Impl<A : ShaderBuffer, B : ShaderBuffer>(
        aShader: Shader<A>,
        bShader: Shader<B>,
        val pixels: Pixels
    ) : ShaderImpl<Buffer> {
        private val colors = Array(pixels.count) { Color.WHITE }
        private val aPixels = PixelBuf(pixels.count)
        private val bPixels = PixelBuf(pixels.count)
        private val shaderAImpl: ShaderImpl<A> = aShader.createImpl(aPixels)
        private val shaderBImpl: ShaderImpl<B> = bShader.createImpl(bPixels)

        @Suppress("UNCHECKED_CAST")
        override fun draw(buffer: Buffer) {
            shaderAImpl.draw(buffer.aShaderBuffer as A)
            shaderBImpl.draw(buffer.bShaderBuffer as B)

            val mode = buffer.mode
            for (i in colors.indices) {
                val aColor = aPixels.colors[i]
                val bColor = bPixels.colors[i]
                colors[i] = aColor.fade(mode.composite(aColor, bColor), buffer.fade)
            }
            pixels.set(colors)
        }

        class PixelBuf(override val count: Int) : Pixels {
            val colors = Array(count) { Color.WHITE }

            override fun set(colors: Array<Color>) {
                colors.copyInto(this.colors)
            }
        }
    }
}

enum class CompositingMode {
    OVERLAY { override fun composite(src: Color, dest: Color) = src },
    ADD { override fun composite(src: Color, dest: Color) = dest.plus(src) };

    abstract fun composite(src: Color, dest: Color): Color

    companion object {
        val values = values()
        fun get(i: Byte): CompositingMode {
            return values[i.toInt()]
        }
    }
}