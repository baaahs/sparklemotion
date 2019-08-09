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

    override fun createRenderer(surface: Surface, renderContext: RenderContext): Shader.Renderer<Buffer> {
        val rendererA: Shader.Renderer<*> = aShader.createRenderer(surface, renderContext)
        val rendererB: Shader.Renderer<*> = bShader.createRenderer(surface, renderContext)
        return Renderer(rendererA, rendererB)
    }

    override fun createRenderer(surface: Surface): Shader.Renderer<Buffer> {
        val rendererA: Shader.Renderer<*> = aShader.createRenderer(surface)
        val rendererB: Shader.Renderer<*> = bShader.createRenderer(surface)
        return Renderer(rendererA, rendererB)
    }

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
        private val rendererA: Shader.Renderer<A>,
        private val rendererB: Shader.Renderer<B>
    ) : Shader.Renderer<Buffer> {

        @Suppress("UNCHECKED_CAST")
        override fun beginFrame(buffer: Buffer, pixelCount: Int) {
            rendererA.beginFrame(buffer.bufferA as A, pixelCount)
            rendererB.beginFrame(buffer.bufferB as B, pixelCount)
        }

        @Suppress("UNCHECKED_CAST")
        override fun draw(buffer: Buffer, pixelIndex: Int): Color {
            val dest = rendererA.draw(buffer.bufferA as A, pixelIndex)
            val src = rendererB.draw(buffer.bufferB as B, pixelIndex)
            return dest.fade(buffer.mode.composite(src, dest), buffer.fade)
        }

        override fun endFrame() {
            rendererA.endFrame()
            rendererB.endFrame()
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