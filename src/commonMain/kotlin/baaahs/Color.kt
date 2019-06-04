package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.js.JsName
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Canonical representation of a color.
 */
@Serializable
data class Color(val argb: Int) {
    /** Values are bounded at `0..255`. */
    constructor(red: Float, green: Float, blue: Float, alpha: Float = 1f) : this(asArgb(red, green, blue, alpha))

    /** Values are bounded at `0f..1f`. */
    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255) : this(asArgb(red, green, blue, alpha))

    fun serialize(writer: ByteArrayWriter) = writer.writeInt(argb)

    @Transient val alphaI: Int get() = alphaI(argb)
    @Transient val redI: Int get() = redI(argb)
    @Transient val greenI: Int get() = greenI(argb)
    @Transient val blueI: Int get() = blueI(argb)

    @Transient val alphaF: Float get() = alphaI.toFloat() / 255
    @Transient val redF: Float get() = redI.toFloat() / 255
    @Transient val greenF: Float get() = greenI.toFloat() / 255
    @Transient val blueF: Float get() = blueI.toFloat() / 255

    fun alphaI(value: Int) = value shr 24 and 0xff
    fun redI(value: Int) = value shr 16 and 0xff
    fun greenI(value: Int) = value shr 8 and 0xff
    fun blueI(value: Int) = value and 0xff

    val rgb: Int get() = argb and 0xffffff
    fun toInt(): Int = argb

    @JsName("toHexString")
    fun toHexString() =
        "#" + maybe(alphaI) + redI.toHexString() + greenI.toHexString() + blueI.toHexString()

    private fun maybe(alphaI: Int): String = if (alphaI == 255) "" else alphaI.toHexString()

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

    /** Super-naive approximation of desaturation. */
    fun withSaturation(saturation: Float): Color {
        val desaturation = 1 - saturation
        return Color(
            redF + (1 - redF) * desaturation,
            greenF + (1 - greenF) * desaturation,
            blueF + (1 - blueF) * desaturation,
            alphaF
        )
    }

    fun distanceTo(other: Color): Float {
        val dist = square(other.redF - redF) + square(other.greenF - greenF) + square(other.blueF - blueF)
        return sqrt(dist / 3)
    }

    private fun square(f: Float) = f * f

    fun plus(other: Color): Color =
        Color(redI + other.redI, greenI + other.greenI, blueI + other.blueI, alphaI)

    fun fade(other: Color, amount: Float = 0.5f): Color {
        val amountThis = 1 - amount

        return Color(
            redF * amountThis + other.redF * amount,
            greenF * amountThis + other.greenF * amount,
            blueF * amountThis + other.blueF * amount,
            alphaF * amountThis + other.alphaF * amount
        )
    }

    fun opaque(): Color = Color(argb or 0xff000000.toInt())

    override fun toString(): String {
        return "Color(${toHexString()})"
    }

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

        fun parse(reader: ByteArrayReader) = Color(reader.readInt())

        @JsName("fromInts")
        fun from(i: Int) = Color(i)

        @JsName("fromString")
        fun from(hex: String): Color {
            val hexDigits = hex.trimStart('#')
            if (hexDigits.length == 6) {
                val l: Int = 0xff000000.toInt()
                // huh? that's not an Int already? I'm supposed to do twos-complement math and negate? blech Kotlin.
                return Color((l or hexDigits.toInt(16)).toInt())
            }
            throw IllegalArgumentException("unknown color \"$hex\"")
        }

        private fun asArgb(red: Float, green: Float, blue: Float, alpha: Float = 1f): Int {
            val asArgb = asArgb(asInt(red), asInt(green), asInt(blue), asInt(alpha))
            return asArgb
        }

        private fun asArgb(red: Int, green: Int, blue: Int, alpha: Int = 255): Int {
            return ((bounded(alpha) shl 24)
                    or (bounded(red) shl 16)
                    or (bounded(green) shl 8)
                    or (bounded(blue)))
        }

        private fun bounded(i: Int): Int = max(0, min(255, i))
        private fun bounded(f: Float): Float = max(0f, min(1f, f))
        private fun asInt(f: Float): Int = (bounded(f) * 255).toInt()
    }
}