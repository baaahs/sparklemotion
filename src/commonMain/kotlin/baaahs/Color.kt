package baaahs

import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

data class Color(val red: Int, val green: Int, val blue: Int) {
    fun serialize(writer: ByteArrayWriter) {
        writer.writeByte((red and 0xff).toByte())
        writer.writeByte((green and 0xff).toByte())
        writer.writeByte((blue and 0xff).toByte())
    }

    val redF: Float
        get() = red.toFloat() / 255
    val greenF: Float
        get() = green.toFloat() / 255
    val blueF: Float
        get() = blue.toFloat() / 255

    fun toInt(): Int =
        (red shl 16 and 0xff0000)
            .or(green shl 8 and 0xff00)
            .or(blue and 0xff)

    fun toHexString() =
        red.toHexString() + green.toHexString() + blue.toHexString()

    fun Int.toHexString(): String {
        if (this < 0) {
            throw Exception("can't toHexString() negative ints")
        }

        if (this < 16) {
            return "0" + toString(16)
        } else {
            return toString(16)
        }
    }

    fun withSaturation(saturation: Float): Color {
        val desaturation = 1 - saturation
        return Color(
            min(255, red + ((255 - red) * desaturation).toInt()),
            min(255, green + ((255 - green) * desaturation).toInt()),
            min(255, blue + ((255 - blue) * desaturation).toInt())
        )
    }

    fun distanceTo(other: Color): Float {
        val dist = square(other.redF - redF) + square(other.greenF - greenF) + square(other.blueF - blueF)
        return sqrt(dist / 3)
    }

    private fun square(f: Float) = f * f

    companion object {
        val BLACK = Color(0, 0, 0)
        val WHITE = Color(255, 255, 255)
        val RED = Color(255, 0, 0)
        val ORANGE = Color(255, 127, 0)
        val YELLOW = Color(255, 255, 0)
        val GREEN = Color(0, 255, 0)
        val BLUE = Color(0, 0, 255)
        val PURPLE = Color(200, 0, 212)

        fun random() = Color(
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff
        )

        fun parse(reader: ByteArrayReader) = Color(
            reader.readByte().toInt() and 0xff,
            reader.readByte().toInt() and 0xff,
            reader.readByte().toInt() and 0xff
        )

        fun from(i: Int) =
            Color(
                i shr 16 and 0xff,
                i shr 8 and 0xff,
                i and 0xff
            )
    }
}