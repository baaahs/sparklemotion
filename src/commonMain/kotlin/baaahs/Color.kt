package baaahs

import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Serializer
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.js.JsName
import kotlin.math.*
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

    fun withRed(redB: Byte): Color = Color(redB, greenB, blueB, alphaB)
    fun withGreen(greenB: Byte): Color = Color(redB, greenB, blueB, alphaB)
    fun withBlue(blueB: Byte): Color = Color(redB, greenB, blueB, alphaB)
    fun withAlpha(alphaB: Byte): Color = Color(redB, greenB, blueB, alphaB)

    fun withSaturation(saturation: Float): Color =
        toHSB().withSaturation(saturation).toRGB(alphaF)

    fun withBrightness(brightness: Float): Color =
        toHSB().withBrightness(brightness).toRGB(alphaF)

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

    fun gammaCorrected(invGamma: Float): Color =
        if (invGamma == 1f) this else Color(
            redF.pow(invGamma),
            greenF.pow(invGamma),
            blueF.pow(invGamma),
            alphaF
        )

    /**
     * Converts the components of a color, as specified by the default RGB
     * model, to an equivalent set of values for hue, saturation, and
     * brightness that are the three components of the HSB model.
     *
     * Adapted from OpenJDK's [java.awt.Color#RGBtoHSB].
     *
     * @return    an HSB containing the hue, saturation,
     * and brightness (in that order), of the color with
     * the indicated red, green, and blue components.
     * @see java.awt.Color.getRGB
     * @see java.awt.Color.Color
     * @see java.awt.image.ColorModel.getRGBdefault
     * @since     1.0
     */
    fun toHSB(): HSB {
        var hue: Float
        val saturation: Float
        val brightness: Float
        var cmax = if (redI > greenI) redI else greenI
        if (blueI > cmax) cmax = blueI
        var cmin = if (redI < greenI) redI else greenI
        if (blueI < cmin) cmin = blueI
        brightness = cmax.toFloat() / 255.0f
        saturation = if (cmax != 0) (cmax - cmin).toFloat() / cmax.toFloat() else 0f
        if (saturation == 0f) hue = 0f else {
            val redc = (cmax - redI).toFloat() / (cmax - cmin).toFloat()
            val greenc = (cmax - greenI).toFloat() / (cmax - cmin).toFloat()
            val bluec = (cmax - blueI).toFloat() / (cmax - cmin).toFloat()
            hue = if (redI == cmax) bluec - greenc else if (greenI == cmax) 2.0f + redc - bluec else 4.0f + greenc - redc
            hue /= 6.0f
            if (hue < 0) hue += 1.0f
        }
        return HSB(hue, saturation, brightness)
    }

    data class HSB(
        val hue: Float,
        val saturation: Float,
        val brightness: Float
    ) {
        /**
         * Converts the components of a color, as specified by the HSB
         * model, to an equivalent set of values for the default RGB model.
         *
         * The `saturation` and `brightness` components
         * should be floating-point values between zero and one
         * (numbers in the range 0.0-1.0).  The `hue` component
         * can be any floating-point number.  The floor of this number is
         * subtracted from it to create a fraction between 0 and 1.  This
         * fractional number is then multiplied by 360 to produce the hue
         * angle in the HSB color model.
         *
         * A [Color] is returned.
         *
         * Adapted from OpenJDK's [java.awt.Color#HSBtoRGB].
         *
         * @return    the RGB value of the color with the indicated hue,
         * saturation, and brightness.
         * @see java.awt.Color.getRGB
         * @see java.awt.Color.Color
         * @see java.awt.image.ColorModel.getRGBdefault
         * @since     1.0
         */
        fun toRGB(alpha: Float = 1f): Color {
            var r = 0
            var g = 0
            var b = 0
            if (saturation == 0f) {
                b = (brightness * 255.0f + 0.5f).toInt()
                g = b
                r = g
            } else {
                val h = (hue - floor(hue)) * 6.0f
                val f = h - floor(h)
                val p = brightness * (1.0f - saturation)
                val q = brightness * (1.0f - saturation * f)
                val t = brightness * (1.0f - saturation * (1.0f - f))
                when (h.toInt()) {
                    0 -> {
                        r = (brightness * 255.0f + 0.5f).toInt()
                        g = (t * 255.0f + 0.5f).toInt()
                        b = (p * 255.0f + 0.5f).toInt()
                    }
                    1 -> {
                        r = (q * 255.0f + 0.5f).toInt()
                        g = (brightness * 255.0f + 0.5f).toInt()
                        b = (p * 255.0f + 0.5f).toInt()
                    }
                    2 -> {
                        r = (p * 255.0f + 0.5f).toInt()
                        g = (brightness * 255.0f + 0.5f).toInt()
                        b = (t * 255.0f + 0.5f).toInt()
                    }
                    3 -> {
                        r = (p * 255.0f + 0.5f).toInt()
                        g = (q * 255.0f + 0.5f).toInt()
                        b = (brightness * 255.0f + 0.5f).toInt()
                    }
                    4 -> {
                        r = (t * 255.0f + 0.5f).toInt()
                        g = (p * 255.0f + 0.5f).toInt()
                        b = (brightness * 255.0f + 0.5f).toInt()
                    }
                    5 -> {
                        r = (brightness * 255.0f + 0.5f).toInt()
                        g = (p * 255.0f + 0.5f).toInt()
                        b = (q * 255.0f + 0.5f).toInt()
                    }
                }
            }
            return Color(r, g, b, (alpha * 255).toInt())
        }

        fun withHue(newHue: Float): HSB = copy(hue = newHue)
        fun withSaturation(newSaturation: Float): HSB = copy(saturation = newSaturation)
        fun withBrightness(newBrightness: Float): HSB = copy(brightness = newBrightness)
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

        fun fromHex(i: Int) = Color(i or 0xff000000.toInt())

        @JsName("fromInts")
        fun from(r: Int, g: Int, b: Int) = Color(r, g, b)

        @JsName("fromBytes")
        fun from(r: Byte, g: Byte, b: Byte) = Color(r, g, b)

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
            return asArgb(asInt(red), asInt(green), asInt(blue), asInt(alpha))
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

        override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Color", PrimitiveKind.INT)
        override fun serialize(encoder: Encoder, value: Color) = encoder.encodeInt(value.argb)
        override fun deserialize(decoder: Decoder): Color = Color(decoder.decodeInt())
    }
}