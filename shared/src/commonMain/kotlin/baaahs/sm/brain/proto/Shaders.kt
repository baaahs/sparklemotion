package baaahs.sm.brain.proto

import baaahs.Color
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.shaders.PixelBrainShader
import baaahs.shaders.SolidBrainShader

enum class BrainShaderId(val reader: BrainShaderReader<*>) {
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

interface BrainShaderReader<T : BrainShader<*>> {
    fun parse(reader: ByteArrayReader): T
}

abstract class BrainShader<B : BrainShader.Buffer>(val idBrain: BrainShaderId) {
    abstract fun createRenderer(): Renderer<B>

    abstract fun createBuffer(pixelCount: Int): B

    val descriptorBytes: ByteArray by lazy { toBytes() }

    fun serialize(writer: ByteArrayWriter) {
        writer.writeByte(idBrain.ordinal.toByte())
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
        val brainShader: BrainShader<*>

        fun serialize(writer: ByteArrayWriter)

        /**
         * Read new data into an existing buffer, as efficiently as possible.
         */
        fun read(reader: ByteArrayReader)
    }

    interface Renderer<B : Buffer> {
        fun beginFrame(buffer: B, pixelCount: Int) {}
        fun draw(buffer: B, pixelIndex: Int): Color?
        fun endFrame() {}
        fun release() {}
    }
}

interface Pixels : Iterable<Color> {
    val size: Int

    val indices: IntRange
        get() = IntRange(0, size - 1)

    operator fun get(i: Int): Color
    operator fun set(i: Int, color: Color)

    fun set(colors: Array<Color>)

    fun finishedFrame() {}

    override fun iterator(): Iterator<Color> {
        return object : Iterator<Color> {
            private var i = 0

            override fun hasNext(): Boolean = i < size

            override fun next(): Color = get(i++)
        }
    }

    object NullPixels : Pixels {
        override val size = 0

        override fun get(i: Int): Color = Color.BLACK
        override fun set(i: Int, color: Color) {}
        override fun set(colors: Array<Color>) {}
    }
}