package baaahs

interface Dmx {
    abstract class Universe {
        private val channelsOut = ByteArray(512)

        abstract fun writer(baseChannel: Int, channelCount: Int): Buffer

        abstract fun sendFrame()
    }

    class Buffer(private val channels: ByteArray, val baseChannel: Int, val channelCount: Int) {
        operator fun get(index: Int): Byte {
            boundsCheck(index)
            return channels[baseChannel + index]
        }

        operator fun set(index: Int, value: Byte): Unit {
            boundsCheck(index)
            channels[baseChannel + index] = value
        }

        private fun boundsCheck(index: Int) {
            if (index < 0 || index >= channelCount) {
                throw Exception("index out of bounds: $index >= ${channelCount}")
            }
        }
    }

    open class DeviceType(val channelCount: Int) {
    }

    class Shenzarpy(private val buffer: Buffer) : DeviceType(16) {
        val panRange = 0f..540f
        val tiltRange = -110f..110f

        enum class WheelColor(val color: Color) {
            RED(Color.from(0xc21e22)),
            ORANGE(Color.from(0xeb8236)),
            AQUAMARINE(Color.from(0x7cbc84)),
            DEEP_GREEN(Color.from(0x12812f)),
            LIGHT_GREEN(Color.from(0x9fc13f)),
            LAVENDER(Color.from(0x8f74ab)),
            PINK(Color.from(0xeb8182)),
            YELLOW(Color.from(0xfeeb34)),
            MAGENTA(Color.from(0xe11382)),
            CYAN(Color.from(0x1ba7e8)),
            CTO2(Color.from(0xf4c651)),
            CTO1(Color.from(0xf4d88a)),
            CTB(Color.from(0x97c7b8)),
            DARK_BLUE(Color.from(0x085197)),
            WHITE(Color.from(0xffffff))
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
            GOBO_WHEEL_SPEED()
        }

        private operator fun Buffer.set(channel: Channel, value: Byte) {
            buffer[channel.ordinal] = value
        }

        private operator fun Buffer.get(channel: Channel): Byte = buffer[channel.ordinal]

        fun closestColorFor(color: Color): Byte {
            var bestMatch = WheelColor.WHITE
            var bestDistance = 1f

            WheelColor.values().forEach { wheelColor ->
                val distance = wheelColor.color.distanceTo(color)
                if (distance < bestDistance) {
                    bestMatch = wheelColor
                    bestDistance = distance
                }
            }

            println("bestMatch = ${bestMatch} ${bestMatch.ordinal.toByte()}")

            return bestMatch.ordinal.toByte()
        }

        var colorWheel: Byte
            get() = buffer[Channel.COLOR_WHEEL]
            set(value) {
                buffer[Channel.COLOR_WHEEL] = value
            }

        var pan: Float
            get() = buffer[Channel.PAN].toFloat()
            set(value) {
                buffer[Channel.PAN] = value.toByte()
            }

        var tilt: Float
            get() = buffer[Channel.TILT].toFloat()
            set(value) {
                buffer[Channel.TILT] = value.toByte()
            }
    }


}

class MovingHeadBuffer(
    private val byteArray: ByteArray,
    var colorIllicitDontUse: Color,
    var rotAIllicitDontUse: Float,
    var rotBIllicitDontUse: Float
)

class FakeDmxUniverse : Dmx.Universe() {
    private val channelsOut = ByteArray(512)
    private val channelsIn = ByteArray(512)
    private val listeners = mutableListOf<() -> Unit>()

    override fun writer(baseChannel: Int, channelCount: Int) =
        Dmx.Buffer(channelsOut, baseChannel, channelCount)

    fun reader(baseChannel: Int, channelCount: Int, listener: () -> Unit): Dmx.Buffer {
        listeners.add(listener)
        return Dmx.Buffer(channelsIn, baseChannel, channelCount)
    }

    override fun sendFrame() {
        channelsOut.copyInto(channelsIn)
        listeners.forEach { it() }
    }
}