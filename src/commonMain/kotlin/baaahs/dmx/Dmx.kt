package baaahs.dmx

interface Dmx {
    abstract class Universe {
        abstract fun writer(baseChannel: Int, channelCount: Int): Buffer
        abstract fun sendFrame()
        abstract fun allOff()
    }

    class Buffer(
        private val channels: ByteArray,
        val baseChannel: Int,
        val channelCount: Int
    ) {
        operator fun get(channel: Channel): Byte = get(channel.offset)

        operator fun get(index: Int): Byte {
            boundsCheck(index)
            return channels[baseChannel + index]
        }

        operator fun set(channel: Channel, value: Byte) = set(channel.offset, value)

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

    interface Adapter

    interface AdapterBuilder {
        fun build(buffer: Buffer): Adapter
    }
}
