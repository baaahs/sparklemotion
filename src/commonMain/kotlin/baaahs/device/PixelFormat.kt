package baaahs.device

import baaahs.Color
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter

enum class PixelFormat {
    RGB8 {
        override val channelsPerPixel: Int = 3

        override fun readColor(reader: ByteArrayReader): Color {
            return Color.parseWithoutAlpha(reader)
        }

        override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
            val redF = reader.readByte().asUnsignedToInt() / 255f
            val greenF = reader.readByte().asUnsignedToInt() / 255f
            val blueF = reader.readByte().asUnsignedToInt() / 255f
            setter(redF, greenF, blueF)
        }

        override fun readColorInts(reader: ByteArrayReader, setter: (Int, Int, Int) -> Unit) {
            val redI = reader.readByte().asUnsignedToInt()
            val greenI = reader.readByte().asUnsignedToInt()
            val blueI = reader.readByte().asUnsignedToInt()
            setter(redI, greenI, blueI)
        }

        override fun writeColor(color: Color, buf: ByteArrayWriter) {
            buf.writeByte(color.redB)
            buf.writeByte(color.greenB)
            buf.writeByte(color.blueB)
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
            val greenB = reader.readByte()
            val redB = reader.readByte()
            val blueB = reader.readByte()

            // Using Color's int constructor fixes a bug in Safari causing
            // color values above 127 to be treated as 0. Untested. :-(
            return Color(
                redB.toInt() and 0xff,
                greenB.toInt() and 0xff,
                blueB.toInt() and 0xff
            )
        }

        override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
            val greenF = reader.readByte().asUnsignedToInt() / 255f
            val redF = reader.readByte().asUnsignedToInt() / 255f
            val blueF = reader.readByte().asUnsignedToInt() / 255f
            setter(redF, greenF, blueF)
        }

        override fun readColorInts(reader: ByteArrayReader, setter: (Int, Int, Int) -> Unit) {
            val greenI = reader.readByte().asUnsignedToInt()
            val redI = reader.readByte().asUnsignedToInt()
            val blueI = reader.readByte().asUnsignedToInt()
            setter(redI, greenI, blueI)
        }

        override fun writeColor(color: Color, buf: ByteArrayWriter) {
            buf.writeByte(color.greenB)
            buf.writeByte(color.redB)
            buf.writeByte(color.blueB)
        }
    },

    // See https://www.rockvilleaudio.com/content/Manuals/ROCKPAR%20TRI_Manual_v2_OL.pdf:
    ROCKPAR_RGB8 {
        override val channelsPerPixel: Int = 7

        override fun readColor(reader: ByteArrayReader): Color {
            reader.readByte() // Mode (garbage!)
            reader.readByte() // Palette (garbage!)
            reader.readByte() // Speed (garbage!)
            reader.readByte() // Dimmer (garbage!)
            return Color.parseWithoutAlpha(reader)
        }

        override fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit) {
            reader.readByte() // Mode (garbage!)
            reader.readByte() // Palette (garbage!)
            reader.readByte() // Speed (garbage!)
            reader.readByte() // Dimmer (garbage!)
            val redF = reader.readByte().asUnsignedToInt() / 255f
            val greenF = reader.readByte().asUnsignedToInt() / 255f
            val blueF = reader.readByte().asUnsignedToInt() / 255f
            setter(redF, greenF, blueF)
        }

        override fun writeColor(color: Color, buf: ByteArrayWriter) {
            buf.writeByte(1) // Mode: manual dimming
            buf.writeByte(0) // Palette (garbage!)
            buf.writeByte(0) // Speed (garbage!)
            buf.writeByte(0xFF.toByte()) // Dimmer: full brightness
            buf.writeByte(color.redB)
            buf.writeByte(color.greenB)
            buf.writeByte(color.blueB)
        }
    },
;

    abstract val channelsPerPixel: Int
    abstract fun readColor(reader: ByteArrayReader): Color
    abstract fun readColor(reader: ByteArrayReader, setter: (Float, Float, Float) -> Unit)
    abstract fun readColorInts(reader: ByteArrayReader, setter: (Int, Int, Int) -> Unit)
    abstract fun writeColor(color: Color, buf: ByteArrayWriter)

    companion object {
        val default = RGB8
        private fun Byte.asUnsignedToInt(): Int = this.toInt().and(0xFF)
    }
}