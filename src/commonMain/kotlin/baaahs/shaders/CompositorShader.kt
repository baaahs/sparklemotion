package baaahs.shaders

import baaahs.*
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

/**
 * A shader which combines the results of two sub-shaders according to a specified compositing mode and cross-fade
 * value.
 */
class CompositorShader(val aShader: Shader<*>, val bShader: Shader<*>) :
    Shader<CompositorShader.Buffer>(ShaderId.COMPOSITOR) {

    override fun createBuffer(surface: Surface) = Buffer(aShader.createBuffer(surface), bShader.createBuffer(surface))

    override fun serializeConfig(writer: ByteArrayWriter) {
        aShader.serialize(writer)
        bShader.serialize(writer)
    }

    override fun createRenderer(surface: Surface): Shader.Renderer<Buffer> =
        Renderer(surface, aShader, bShader)

    override fun readBuffer(reader: ByteArrayReader): Buffer =
        Buffer(
            aShader.readBuffer(reader),
            bShader.readBuffer(reader),
            CompositingMode.get(reader.readByte()),
            reader.readFloat()
        )

    fun createBuffer(bufferA: Shader.Buffer, bufferB: Shader.Buffer): Buffer =
        Buffer(bufferA, bufferB)

    companion object : ShaderReader<CompositorShader> {
        override fun parse(reader: ByteArrayReader): CompositorShader {
            val shaderA = Shader.parse(reader)
            val shaderB = Shader.parse(reader)
            return CompositorShader(shaderA, shaderB)
        }
    }

    inner class Buffer(
        val bufferA: Shader.Buffer, val bufferB: Shader.Buffer,
        var mode: CompositingMode = CompositingMode.NORMAL,
        var fade: Float = 0.5f
    ) : Shader.Buffer {
        override val shader: Shader<*> = this@CompositorShader

        override fun serialize(writer: ByteArrayWriter) {
            bufferA.serialize(writer)
            bufferB.serialize(writer)
            writer.writeByte(mode.ordinal.toByte())
            writer.writeFloat(fade)
        }

        override fun read(reader: ByteArrayReader) {
            bufferA.read(reader)
            bufferB.read(reader)
            mode = CompositingMode.get(reader.readByte())
            fade = reader.readFloat()
        }
    }

    class Renderer<A : Shader.Buffer, B : Shader.Buffer>(
        surface: Surface,
        aShader: Shader<A>,
        bShader: Shader<B>
    ) : Shader.Renderer<Buffer> {
        private val pixelCount = surface.pixelCount
        private val srcPixels = PixelBuf(pixelCount)
        private val rendererA: Shader.Renderer<A> = aShader.createRenderer(surface)
        private val rendererB: Shader.Renderer<B> = bShader.createRenderer(surface)

        @Suppress("UNCHECKED_CAST")
        override fun draw(buffer: Buffer, pixels: Pixels) {
            @Suppress("UnnecessaryVariable")
            val destPixels = pixels

            rendererA.draw(buffer.bufferA as A, destPixels)
            rendererB.draw(buffer.bufferB as B, srcPixels)

            val mode = buffer.mode
            for (i in 0 until pixelCount) {
                val src = srcPixels[i]
                val dest = destPixels[i]
                destPixels[i] = dest.fade(mode.composite(src, dest), buffer.fade)
            }
        }

        class PixelBuf(override val size: Int) : Pixels {
            val colors = Array(size) { Color.WHITE }

            override fun get(i: Int): Color = colors[i]

            override fun set(i: Int, color: Color) {
                colors[i] = color
            }

            override fun set(colors: Array<Color>) {
                colors.copyInto(this.colors)
            }
        }
    }
}

enum class CompositingMode {
    NORMAL {
        override fun composite(src: Color, dest: Color) = src
    },
    ADD {
        override fun composite(src: Color, dest: Color) = dest.plus(src)
    };

    abstract fun composite(src: Color, dest: Color): Color

    companion object {
        val values = values()
        fun get(i: Byte): CompositingMode {
            return values[i.toInt()]
        }
    }
}