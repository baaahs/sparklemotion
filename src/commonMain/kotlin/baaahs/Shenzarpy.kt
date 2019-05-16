package baaahs

import kotlin.math.abs

class Shenzarpy(private val buffer: Dmx.Buffer) : Dmx.DeviceType(16) {
    companion object {
        val panRange = toRadians(0f)..toRadians(540f)
        val tiltRange = toRadians(-110f)..toRadians(110f)
    }

    enum class WheelColor(val color: Color) {
        RED(0xc21e22),
        ORANGE(0xeb8236),
        AQUAMARINE(0x7cbc84),
        DEEP_GREEN(0x12812f),
        LIGHT_GREEN(0x9fc13f),
        LAVENDER(0x8f74ab),
        PINK(0xeb8182),
        YELLOW(0xfeeb34),
        MAGENTA(0xe11382),
        CYAN(0x1ba7e8),
        CTO2(0xf4c651),
        CTO1(0xf4d88a),
        CTB(0x97c7b8),
        DARK_BLUE(0x085197),
        WHITE(0xffffff);

        companion object {
            val values = values()
            fun get(i: Byte) = values[i.toInt()]
        }
    }

    enum class Channel {
        COLOR_WHEEL(),
        SHUTTER(),
        DIMMER(),
        GOBO_WHEEL(),
        PRISM(),
        PRISM_ROTATION(),
        MACRO(),
        FROST(),
        FOCUS(),
        PAN(),
        PAN_FINE(),
        TILT(),
        TILT_FINE(),
        PAN_TILT_SPEED(),
        RESET(),
        LAMP_CONTROL(),
        BLANK(),
        COLOR_WHEEL_SPEED(),
        DIM_PRISM_ATOM_SPEED(),
        GOBO_WHEEL_SPEED();


        companion object {
            val values = values()
            fun get(i: Byte) = values[i.toInt()]
        }
    }

    var colorWheel: Byte
        get() = buffer[Channel.COLOR_WHEEL]
        set(value) {
            buffer[Channel.COLOR_WHEEL] = value
        }

    var dimmer: Float
        get() = (buffer[Channel.DIMMER].toInt() and 0xff) / 255F
        set(value) {
            buffer[Channel.DIMMER] = ((value * 255).toInt() and 0xff).toByte()
        }

    var pan: Float
        get() {
            val firstByte = buffer[Channel.PAN].toInt() and 0xff
            val secondByte = buffer[Channel.PAN_FINE].toInt() and 0xff
            val scaled = firstByte * 256 + secondByte
            return scaled / 65535f
        }
        set(value) {
            val modVal = abs(value % panRange.endInclusive)
            val scaled = (modVal * 65535).toInt()
            buffer[Channel.PAN] = (scaled shr 8).toByte()
            buffer[Channel.PAN_FINE] = (scaled and 0xff).toByte()
        }

    var tilt: Float
        get() {
            val firstByte = buffer[Channel.TILT].toInt() and 0xff
            val secondByte = buffer[Channel.TILT_FINE].toInt() and 0xff
            val scaled = firstByte * 256 + secondByte
            return scaled / 65535f
        }
        set(value) {
            val modVal = abs(value % tiltRange.endInclusive)
            val scaled = (modVal * 65535).toInt()
            buffer[Channel.TILT] = (scaled shr 8).toByte()
            buffer[Channel.TILT_FINE] = (scaled and 0xff).toByte()
        }

    init {
        dimmer = 1f
    }

    private operator fun Dmx.Buffer.set(channel: Channel, value: Byte) {
        buffer[channel.ordinal] = value
    }

    private operator fun Dmx.Buffer.get(channel: Channel): Byte = buffer[channel.ordinal]

    fun closestColorFor(color: Color): Byte {
        var bestMatch = WheelColor.WHITE
        var bestDistance = 1f

        WheelColor.values.forEach { wheelColor ->
            val distance = wheelColor.color.distanceTo(color)
            if (distance < bestDistance) {
                bestMatch = wheelColor
                bestDistance = distance
            }
        }

        return bestMatch.ordinal.toByte()
    }
}