package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlinx.serialization.*
import kotlinx.serialization.PrimitiveKind.INT
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
    /** Values are bounded at `0f..1f`. */
    constructor(red: Float, green: Float, blue: Float, alpha: Float = 1f) : this(asArgb(red, green, blue, alpha))

    /** Values are bounded at `0..255`. */
    constructor(red: Int, green: Int, blue: Int, alpha: Int = 255) : this(asArgb(red, green, blue, alpha))

    /** Values are bounded at `0..255` (but really `-128..127` because signed). */
    // TODO: use UByte.
    constructor(red: Byte, green: Byte, blue: Byte, alpha: Byte = 255.toByte()) : this(asArgb(red, green, blue, alpha))

    fun serialize(writer: ByteArrayWriter) = writer.writeInt(argb)

    fun serializeWithoutAlpha(writer: ByteArrayWriter) {
        writer.writeByte(redB)
        writer.writeByte(greenB)
        writer.writeByte(blueB)
    }

    @Transient
    val alphaB: Byte
        get() = alphaI(argb).toByte()
    @Transient
    val redB: Byte
        get() = redI(argb).toByte()
    @Transient
    val greenB: Byte
        get() = greenI(argb).toByte()
    @Transient
    val blueB: Byte
        get() = blueI(argb).toByte()

    @Transient
    val alphaI: Int
        get() = alphaI(argb)
    @Transient
    val redI: Int
        get() = redI(argb)
    @Transient
    val greenI: Int
        get() = greenI(argb)
    @Transient
    val blueI: Int
        get() = blueI(argb)

    @Transient
    val alphaF: Float
        get() = alphaI.toFloat() / 255
    @Transient
    val redF: Float
        get() = redI.toFloat() / 255
    @Transient
    val greenF: Float
        get() = greenI.toFloat() / 255
    @Transient
    val blueF: Float
        get() = blueI.toFloat() / 255

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

    fun withBrightness(brightness: Float): Color {
        return Color(
            redF * brightness,
            greenF * brightness,
            blueF * brightness,
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

    @Serializer(forClass = Color::class)
    companion object : KSerializer<Color> {
        val BLACK = Color(0, 0, 0)
        val WHITE = Color(255, 255, 255)
        val RED = Color(255, 0, 0)
        val ORANGE = Color(255, 127, 0)
        val YELLOW = Color(255, 255, 0)
        val GREEN = Color(0, 255, 0)
        val CYAN = Color(0, 255, 255)
        val BLUE = Color(0, 0, 255)
        val MAGENTA = Color(255, 0, 255)
        val PURPLE = Color(200, 0, 212)
        val TRANSPARENT = Color(0, 0, 0, 0)

        fun random() = Color(
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff,
            Random.nextInt() and 0xff
        )

        fun parse(reader: ByteArrayReader) = Color(reader.readInt())

        fun parseWithoutAlpha(reader: ByteArrayReader) =
            Color(reader.readByte(), reader.readByte(), reader.readByte())

        @JsName("fromInt")
        fun from(i: Int) = Color(i)

        @JsName("fromInts")
        fun from(r: Int, g: Int, b: Int) = Color(r, g, b)

        @JsName("fromString")
        fun from(hex: String): Color {
            var hexDigits = hex.trimStart('#')

            val alpha = when (hexDigits.length) {
                8 -> hexDigits.substring(0, 2).toInt(16).also { hexDigits = hexDigits.substring(2) }
                4 -> (hexDigits.substring(0, 1).toInt(16) * 0x11).also { hexDigits = hexDigits.substring(1) }
                else -> 0xff
            }.shl(24)

            return when (hexDigits.length) {
                6 -> Color(alpha or hexDigits.toInt(16))
                3 -> Color(alpha
                        or hexDigits[0].toString().repeat(2).toInt(16).shl(16)
                        or hexDigits[1].toString().repeat(2).toInt(16).shl(8)
                        or hexDigits[2].toString().repeat(2).toInt(16)
                )
                else -> throw IllegalArgumentException("unknown color \"$hex\"")
            }
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

        private fun asArgb(red: Byte, green: Byte, blue: Byte, alpha: Byte = 255.toByte()): Int {
            return ((bounded(alpha) shl 24)
                    or (bounded(red) shl 16)
                    or (bounded(green) shl 8)
                    or (bounded(blue)))
        }

        private fun bounded(f: Float): Float = max(0f, min(1f, f))
        private fun bounded(i: Int): Int = max(0, min(255, i))
        private fun bounded(b: Byte): Int = b.toInt() and 0xff
        private fun asInt(f: Float): Int = (bounded(f) * 255).toInt()

        override val descriptor: SerialDescriptor = PrimitiveDescriptor("Color", INT)
        override fun serialize(encoder: Encoder, obj: Color) = encoder.encodeInt(obj.argb)
        override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())
    }
}