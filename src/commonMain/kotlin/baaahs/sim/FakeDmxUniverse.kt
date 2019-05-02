package baaahs.sim

import baaahs.Dmx

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