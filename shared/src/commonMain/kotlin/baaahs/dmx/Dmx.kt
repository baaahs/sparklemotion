package baaahs.dmx

import baaahs.util.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

interface Dmx {
    interface Driver {
        fun findDmxDevices(): List<Device>

        companion object {
            val NoOpDmxDriver = object : Driver {
                override fun findDmxDevices(): List<Device> = emptyList()
            }
        }
    }

    interface Device {
        val id: String
        val name: String
        fun asUniverse(universeListener: UniverseListener?): Universe
    }

    abstract class Universe {
        /** @param baseChannel Zero-based. */
        abstract fun writer(baseChannel: Int, channelCount: Int): Buffer
        abstract fun sendFrame()
        abstract fun allOff()
    }

    class Buffer(
        private val channels: ByteArray,
        private val baseChannel: Int = 0,
        private val channelCount: Int = channels.size
    ) {
        operator fun get(channel: Channel): Byte =
            get(channel.offset)

        operator fun get(index: Int): Byte {
            boundsCheck(index)
            return channels[baseChannel + index]
        }

        operator fun set(channel: Channel, value: Byte) =
            set(channel.offset, value)

        operator fun set(index: Int, value: Byte) {
            boundsCheck(index)
            channels[baseChannel + index] = value
        }

        private fun boundsCheck(index: Int) {
            if (index < 0 || index >= channelCount) {
                throw Exception("index out of bounds: $index >= $channelCount")
            }
        }
    }

    interface Channel {
        val offset: Int
    }

    interface UniverseListener {
        fun onSend(name: String, channels: ByteArray)
    }

    companion object {
        const val channelsPerUniverse = 512
    }
}

class DmxUniverseListener(
    val clock: Clock
) : Dmx.UniverseListener {
    val lastFrames = mutableMapOf<String, LastFrame>()

    override fun onSend(name: String, channels: ByteArray) {
        lastFrames[name] = LastFrame(clock.now(), channels.copyOf())
    }

    @Serializable
    class LastFrame(
        val time: Instant,
        val channels: ByteArray
    )
}
