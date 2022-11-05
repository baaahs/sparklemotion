package baaahs.device

import baaahs.Color
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

enum class PixelFormat {
    RGB8 {
        override val channelsPerPixel: Int = 3

        override fun readColor(reader: ByteArrayReader): Color {
            return Color.readWithoutAlpha(reader)
        }

        override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
            val redF = reader.readByte().asUnsignedToInt() / 255f
            val greenF = reader.readByte().asUnsignedToInt() / 255f
            val blueF = reader.readByte().asUnsignedToInt() / 255f
            setter(redF, greenF, blueF)
        }

        override fun writeColor(color: Color, buf: ByteArrayWriter) {
            buf.writeUByte(color.redB)
            buf.writeUByte(color.greenB)
            buf.writeUByte(color.blueB)
        }
    },
//    RBG8 {
//        override val channelsPerPixel: Int = 3
//
//        override fun readColor(reader: ByteArrayReader): Color {
//            val redB = reader.readByte()
//            val blueB = reader.readByte()
//            val greenB = reader.readByte()
//            return Color(redB, greenB, blueB)
//        }
//
//        override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
//            val redF = reader.readByte().asUnsignedToInt() / 255f
//            val blueF = reader.readByte().asUnsignedToInt() / 255f
//            val greenF = reader.readByte().asUnsignedToInt() / 255f
//            setter(redF, greenF, blueF)
//        }
//
//        override fun writeColor(color: Color, buf: ByteArrayWriter) {
//            buf.writeByte(color.redB)
//            buf.writeByte(color.blueB)
//            buf.writeByte(color.greenB)
//        }
//    },
    GRB8 {
        override val channelsPerPixel: Int = 3

        override fun readColor(reader: ByteArrayReader): Color {
            val greenB = reader.readUByte()
            val redB = reader.readUByte()
            val blueB = reader.readUByte()

            return Color.from(redB, greenB, blueB)
        }

        override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
            val greenF = reader.readByte().asUnsignedToInt() / 255f
            val redF = reader.readByte().asUnsignedToInt() / 255f
            val blueF = reader.readByte().asUnsignedToInt() / 255f
            setter(redF, greenF, blueF)
        }

        override fun writeColor(color: Color, buf: ByteArrayWriter) {
            buf.writeUByte(color.greenB)
            buf.writeUByte(color.redB)
            buf.writeUByte(color.blueB)
        }
    };

    abstract val channelsPerPixel: Int
    abstract fun readColor(reader: ByteArrayReader): Color
    abstract fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit)
    abstract fun writeColor(color: Color, buf: ByteArrayWriter)

    companion object {
        val default = RGB8
        private fun Byte.asUnsignedToInt(): Int = this.toInt().and(0xFF)
    }
}