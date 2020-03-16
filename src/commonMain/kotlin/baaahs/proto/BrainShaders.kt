package baaahs.proto

import baaahs.Color
import baaahs.Surface
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.shaders.RenderContext

enum class BrainShaderId(val reader: ShaderReader<*>) {
    SOLID(SolidBrainShader),
    PIXEL(PixelBrainShader);

    companion object {
        val values = values()
        fun get(i: Byte): BrainShaderId {
            if (i > values.size || i < 0) {
                throw Throwable("bad index for ShaderId: $i")
            }
            return values[i.toInt()]
        }
    }
}

interface ShaderReader<T : BrainShader<*>> {
    fun parse(reader: ByteArrayReader): T
}

abstract class BrainShader<B : BrainShader.Buffer>(val id: BrainShaderId) {
    open fun createRenderer(surface: Surface, renderContext: RenderContext): Renderer<B> {
        return createRenderer(surface)
    }

    abstract fun createRenderer(surface: Surface): Renderer<B>

    abstract fun createBuffer(surface: Surface): B

    val descriptorBytes: ByteArray by lazy { toBytes() }

    fun serialize(writer: ByteArrayWriter) {
        writer.writeByte(id.ordinal.toByte())
        serializeConfig(writer)
    }

    /** Override if your shader has static configuration that needs to be shared with the Renderer. */
    open fun serializeConfig(writer: ByteArrayWriter) {
    }

    private fun toBytes(): ByteArray {
        val writer = ByteArrayWriter()
        serialize(writer)
        return writer.toBytes()
    }

    abstract fun readBuffer(reader: ByteArrayReader): B

    companion object {
        fun parse(reader: ByteArrayReader): BrainShader<*> {
            val shaderTypeI = reader.readByte()
            val shaderType = BrainShaderId.get(shaderTypeI)
            return shaderType.reader.parse(reader)
        }
    }

    interface Buffer {
        val shader: BrainShader<*>

        fun serialize(writer: ByteArrayWriter)

        /**
         * Read new data into an existing buffer, as efficiently as possible.
         */
        fun read(reader: ByteArrayReader)
    }

    interface Renderer<B : Buffer> {
        fun beginFrame(buffer: B, pixelCount: Int) {}
        fun draw(buffer: B, pixelIndex: Int): Color
        fun endFrame() {}
        fun release() {}
    }
}