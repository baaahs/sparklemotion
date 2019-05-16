package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlin.js.JsName
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random

/**
 * Canonical representation of a color.
 */
typealias Color = Int

val Color.argb : Int get() = this

/** Values are bounded at `0..255`. */
fun ColorFrom(red: Float, green: Float, blue: Float, alpha: Float = 1f) = asArgb(red, green, blue, alpha)

/** Values are bounded at `0f..1f`. */
fun ColorFrom(red: Int, green: Int, blue: Int, alpha: Int = 255) = asArgb(red, green, blue, alpha)

fun Color.serialize(writer: ByteArrayWriter) = writer.writeInt(argb)


val Color.alphaI: Int get() = alphaI(argb)
val Color.redI: Int get() = redI(argb)
val Color.greenI: Int get() = greenI(argb)
val Color.blueI: Int get() = blueI(argb)

val Color.alphaF: Float get() = alphaI.toFloat() / 255
val Color.redF: Float get() = redI.toFloat() / 255
val Color.greenF: Float get() = greenI.toFloat() / 255
val Color.blueF: Float get() = blueI.toFloat() / 255

fun Color.alphaI(value: Int) = value shr 24 and 0xff
fun Color.redI(value: Int) = value shr 16 and 0xff
fun Color.greenI(value: Int) = value shr 8 and 0xff
fun Color.blueI(value: Int) = value and 0xff

val Color.rgb: Int get() = argb and 0xffffff
//fun Color.toInt(): Int = argb

@JsName("toHexString")
fun Color.toHexString() =
    "#" + maybe(alphaI) + intToHexString(redI) + intToHexString(greenI) + intToHexString(blueI)

private fun maybe(alphaI: Int): String = if (alphaI == 255) "" else alphaI.toHexString()

private fun intToHexString(i: Int): String {
    if (i < 0) {
        throw Exception("can't toHexString() negative ints")
    }

    if (i < 16) {
        return "0" + i.toString(16)
    } else {
        return i.toString(16)
    }
}

/** Super-naive approximation of desaturation. */
fun Color.withSaturation(saturation: Float): Color {
    val desaturation = 1 - saturation
    return ColorFrom(
        redF + (1 - redF) * desaturation,
        greenF + (1 - greenF) * desaturation,
        blueF + (1 - blueF) * desaturation,
        alphaF
    )
}

fun Color.distanceTo(other: Color): Float {
    val dist = square(other.redF - redF) + square(other.greenF - greenF) + square(other.blueF - blueF)
    return sqrt(dist / 3)
}

private fun Color.square(f: Float) = f * f

fun Color.plus(other: Color): Color =
    ColorFrom(redI + other.redI, greenI + other.greenI, blueI + other.blueI, alphaI)

fun Color.fade(other: Color, amount: Float = 0.5f): Color {
    val amountThis = 1 - amount

    return ColorFrom(
        redF * amountThis + other.redF * amount,
        greenF * amountThis + other.greenF * amount,
        blueF * amountThis + other.blueF * amount,
        alphaF * amountThis + other.alphaF * amount
    )
}

//override fun toString(): String {
//    return "Color(${toHexString()})"
//}

class Colors {
    companion object {
        val BLACK = ColorFrom(0, 0, 0)
        val WHITE = ColorFrom(255, 255, 255)
        val RED = ColorFrom(255, 0, 0)
        val ORANGE = ColorFrom(255, 127, 0)
        val YELLOW = ColorFrom(255, 255, 0)
        val GREEN = ColorFrom(0, 255, 0)
        val BLUE = ColorFrom(0, 0, 255)
        val PURPLE = ColorFrom(200, 0, 212)

        fun parse(reader: ByteArrayReader) = reader.readInt()

        fun random() = ColorFrom(
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff
        )

        @JsName("fromInts")
        fun from(i: Int) = i

        @JsName("fromString")
        fun from(hex: String): Color {
            val hexDigits = hex.trimStart('#')
            if (hexDigits.length == 6) {
                val l: Int = 0xff000000.toInt()
                // huh? that's not an Int already? I'm supposed to do twos-complement math and negate? blech Kotlin.
                return (l or hexDigits.toInt(16))
            }
            throw IllegalArgumentException("unknown color \"$hex\"")
        }


    }
}

fun asArgb(red: Float, green: Float, blue: Float, alpha: Float = 1f): Int {
    return asArgb(asInt(red), asInt(green), asInt(blue), asInt(alpha))
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
//}
//}