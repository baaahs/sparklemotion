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

}

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

    override fun allOff() {
        for (i in 0..512) channelsIn[i] = 0
        listeners.forEach { it() }
    }
}