package baaahs

interface Dmx {
    abstract class Universe {
        abstract fun writer(baseChannel: Int, channelCount: Int): Buffer
        abstract fun sendFrame()
        abstract fun allOff()
    }

    class Buffer(private val channels: ByteArray, val baseChannel: Int, val channelCount: Int) {
        operator fun get(index: Int): Byte {
            boundsCheck(index)
            return channels[baseChannel + index]
        }

        operator fun set(index: Int, value: Byte) {
            boundsCheck(index)
            channels[baseChannel + index] = value
        }

        private fun boundsCheck(index: Int) {
            if (index < 0 || index >= channelCount) {
                throw Exception("index out of bounds: $index >= ${channelCount}")
            }
        }
    }

    open class DeviceType(val channelCount: Int)
}
