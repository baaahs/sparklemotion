package baaahs.shaders

import baaahs.Color
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.sm.brain.proto.BrainShader
import baaahs.sm.brain.proto.BrainShaderId
import baaahs.sm.brain.proto.BrainShaderReader
import kotlin.math.min

/**
 * A shader that allows control of individual pixels' colors directly from a show.
 *
 * This is a suboptimal shader for most purposes, consider writing a custom shader instead!
 */
class PixelBrainShader(private val encoding: Encoding = Encoding.DIRECT_ARGB) : BrainShader<PixelBrainShader.Buffer>(
    BrainShaderId.PIXEL) {

    enum class Encoding {
        DIRECT_ARGB {
            override fun createBuffer(shader: PixelBrainShader, pixelCount: Int) =
                shader.DirectColorBuffer(pixelCount)
        },
        DIRECT_RGB {
            override fun createBuffer(shader: PixelBrainShader, pixelCount: Int) =
                shader.DirectColorBuffer(pixelCount, true)
        },
        INDEXED_2 {
            override fun createBuffer(shader: PixelBrainShader, pixelCount: Int) =
                shader.IndexedBuffer(1, pixelCount)
        },
        INDEXED_4 {
            override fun createBuffer(shader: PixelBrainShader, pixelCount: Int) =
                shader.IndexedBuffer(2, pixelCount)
        },
        INDEXED_16 {
            override fun createBuffer(shader: PixelBrainShader, pixelCount: Int) =
                shader.IndexedBuffer(4, pixelCount)
        };

        abstract fun createBuffer(shader: PixelBrainShader, pixelCount: Int): Buffer

        companion object {
            val values = values()
            fun get(i: Byte): Encoding {
                return values[i.toInt()]
            }
        }
    }

    override fun serializeConfig(writer: ByteArrayWriter) {
        writer.writeByte(encoding.ordinal.toByte())
    }

    override fun createBuffer(pixelCount: Int): Buffer {
//        if (pixelCount == SparkleMotion.PIXEL_COUNT_UNKNOWN) {
//            SparkleMotion.DEFAULT_PIXEL_COUNT
//        } else {
//            pixelCount
//        }

        return encoding.createBuffer(this, pixelCount)
    }

    override fun createRenderer(): BrainShader.Renderer<Buffer> = Renderer()

    override fun readBuffer(reader: ByteArrayReader): Buffer {
        val incomingPixelCount = reader.readShort().toInt()
        val buf = encoding.createBuffer(this, incomingPixelCount)
        buf.read(reader, incomingPixelCount)
        return buf
    }

    companion object : BrainShaderReader<PixelBrainShader> {
        override fun parse(reader: ByteArrayReader): PixelBrainShader {
            val encoding = Encoding.get(reader.readByte())
            return PixelBrainShader(encoding)
        }
    }

    abstract inner class Buffer : BrainShader.Buffer {
        override val brainShader: BrainShader<*>
            get() = this@PixelBrainShader

        override fun read(reader: ByteArrayReader) {
            val incomingPixelCount = reader.readShort().toInt()
            read(reader, incomingPixelCount)
        }

        abstract val colors: MutableList<Color>
        abstract val palette: Array<Color>
        protected abstract operator fun get(pixelIndex: Int): Color
        protected abstract operator fun set(pixelIndex: Int, color: Color)
        internal abstract operator fun set(pixelIndex: Int, paletteIndex: Int)
        abstract fun setAll(color: Color)
        abstract fun setAll(paletteIndex: Int)
        abstract val indices: IntRange
        abstract fun read(reader: ByteArrayReader, incomingPixelCount: Int)
    }

    inner class DirectColorBuffer(private val pixelCount: Int, private val rgb24BitMode: Boolean = false) : Buffer() {
        override val palette: Array<Color> = emptyArray()
        private var colorsBuf: Array<Color> = Array(pixelCount) { Color.WHITE }
        override val colors: MutableList<Color> =
            object : AbstractMutableList<Color>() {
                override fun add(index: Int, element: Color): Unit = throw UnsupportedOperationException()

                override fun removeAt(index: Int): Color = throw UnsupportedOperationException()

                override fun set(index: Int, element: Color): Color {
                    val oldValue = get(index)
                    this@DirectColorBuffer.set(index, element)
                    return oldValue
                }

                override val size = pixelCount

                override fun get(index: Int): Color = this@DirectColorBuffer.get(index)
            }

        /** [serialize] and [read] are asymmetrical because pixel count is read in [Buffer.read]. */
        override fun serialize(writer: ByteArrayWriter) {
            writer.writeShort(pixelCount)
            colorsBuf.forEach { color -> writeColor(color, writer) }
        }

        /** [serialize] and [read] are asymmetrical because pixel count is read in [Buffer.read]. */
        override fun read(reader: ByteArrayReader, incomingPixelCount: Int) {
            // if there are more colors in the buffer than pixels, drop from the end
            val countFromBuffer = min(colorsBuf.size, incomingPixelCount)
            for (i in 0 until countFromBuffer) {
                colorsBuf[i] = readColor(reader)
            }

            // if there are more pixels than colors in the buffer, repeat
            for (i in countFromBuffer until colorsBuf.size) {
                colorsBuf[i] = colorsBuf[i % countFromBuffer]
            }
        }

        private fun writeColor(color: Color, writer: ByteArrayWriter) {
            if (rgb24BitMode) {
                writer.writeUByte(color.redB)
                writer.writeUByte(color.greenB)
                writer.writeUByte(color.blueB)
            } else {
                writer.writeUInt(color.argb)
            }
        }

        private fun readColor(reader: ByteArrayReader): Color {
            return if (rgb24BitMode) {
                Color(reader.readUByte(), reader.readUByte(), reader.readUByte())
            } else {
                Color(reader.readUInt())
            }
        }

        override operator fun get(pixelIndex: Int): Color = colorsBuf[pixelIndex]
        override fun set(pixelIndex: Int, color: Color) {
            colorsBuf[pixelIndex] = color
        }

        override fun set(pixelIndex: Int, paletteIndex: Int): Unit =
            throw UnsupportedOperationException("Indexed colors aren't available in this mode")

        override fun setAll(color: Color) {
            for (i in colorsBuf.indices) set(i, color)
        }

        override fun setAll(paletteIndex: Int): Unit =
            throw UnsupportedOperationException("Indexed colors aren't available in this mode")

        override val indices = colorsBuf.indices
    }

    inner class IndexedBuffer(private val bitsPerPixel: Int, private val pixelCount: Int) : Buffer() {
        override val palette: Array<Color> = Array(1 shl bitsPerPixel) { Color.WHITE }
        internal val dataBuf: ByteArray = ByteArray(bufferSizeFor(pixelCount)) { 0 }

        override val colors: MutableList<Color>
            get() = object : AbstractMutableList<Color>() {
                override val size: Int = pixelCount

                override fun add(index: Int, element: Color): Unit = throw UnsupportedOperationException()

                override fun removeAt(index: Int): Color = throw UnsupportedOperationException()

                override fun set(index: Int, element: Color): Color =
                    throw IllegalArgumentException("Can't set color directly when using indexed color buffers")

                override fun get(index: Int): Color = this@IndexedBuffer.get(index)
            }

        override fun get(pixelIndex: Int): Color {
            return palette[paletteIndex(pixelIndex)]
        }

        override fun set(pixelIndex: Int, color: Color): Unit =
            throw IllegalArgumentException("Can't set color directly when using indexed color buffers")

        override fun set(pixelIndex: Int, paletteIndex: Int) {
            val mask: Int
            val pixelsPerByte: Int
            val maxIndex: Int
            when (bitsPerPixel) {
                1 -> {
                    mask = 0x01; pixelsPerByte = 8; maxIndex = 1
                }
                2 -> {
                    mask = 0x03; pixelsPerByte = 4; maxIndex = 3
                }
                4 -> {
                    mask = 0x0F; pixelsPerByte = 2; maxIndex = 15
                }
                else -> throw IllegalStateException()
            }

            if (paletteIndex < 0 || paletteIndex > maxIndex)
                throw IllegalArgumentException("Invalid color index $paletteIndex")


            val bufOffset = pixelIndex / pixelsPerByte % dataBuf.size
            val positionInByte = pixelsPerByte - pixelIndex % pixelsPerByte - 1
            val bitShift = positionInByte * bitsPerPixel
            val byte = (dataBuf[bufOffset].toInt() and (mask shl bitShift).inv()) or (paletteIndex shl bitShift)
            dataBuf[bufOffset] = byte.toByte()
        }

        /** [serialize] and [read] are asymmetrical because pixel count is read in [Buffer.read]. */
        override fun serialize(writer: ByteArrayWriter) {
            writer.writeShort(pixelCount)
            palette.forEach { paletteColor -> writer.writeUInt(paletteColor.argb) }
            writer.writeBytes(dataBuf)
        }

        /** [serialize] and [read] are asymmetrical because pixel count is read in [Buffer.read]. */
        override fun read(reader: ByteArrayReader, incomingPixelCount: Int) {
            palette.indices.forEach { i -> palette[i] = Color.from(reader.readInt()) }
            reader.readBytes(dataBuf)
        }

        override fun setAll(color: Color): Unit =
            throw IllegalArgumentException("Can't set color directly when using indexed color buffers")

        override fun setAll(paletteIndex: Int) {
            for (i in indices) set(i, paletteIndex)
        }

        override val indices = 0 until pixelCount

        private fun paletteIndex(pixelIndex: Int): Int {
            val mask: Int
            val pixelsPerByte: Int
            when (bitsPerPixel) {
                1 -> {
                    mask = 0x01; pixelsPerByte = 8
                }
                2 -> {
                    mask = 0x03; pixelsPerByte = 4
                }
                4 -> {
                    mask = 0x0F; pixelsPerByte = 2
                }
                else -> throw IllegalStateException()
            }

            val bufOffset = pixelIndex / pixelsPerByte % dataBuf.size
            val positionInByte = pixelsPerByte - pixelIndex % pixelsPerByte - 1
            val bitShift = positionInByte * bitsPerPixel
            return dataBuf[bufOffset].toInt() shr bitShift and mask
        }

        private fun bufferSizeFor(pixelCount: Int): Int {
            return when (bitsPerPixel) {
                1 -> (pixelCount + 7) / 8
                2 -> (pixelCount + 3) / 4
                4 -> (pixelCount + 1) / 2
                else -> throw IllegalStateException()
            }
        }
    }

    class Renderer : BrainShader.Renderer<Buffer> {
        override fun draw(buffer: Buffer, pixelIndex: Int): Color? {
            return if (pixelIndex < buffer.colors.size) buffer.colors[pixelIndex] else null
        }
    }

}
