package baaahs.sim

import baaahs.dmx.Dmx

class SimDmxDriver : Dmx.Driver {
    private val fakeDmxDevice = SimDmxDevice()

    override fun findDmxDevices(): List<Dmx.Device> = listOf(fakeDmxDevice)
}

class SimDmxDevice : Dmx.Device {
    override val id: String get() = "dmx-simulator"
    override val name: String get() = "DMX Simulator"

    private val fakeDmxUniverse = FakeDmxUniverse()

    override fun asUniverse(): Dmx.Universe = fakeDmxUniverse
}

class FakeDmxUniverse : Dmx.Universe() {
    private val channelsOut = ByteArray(512)
    private val channelsIn = ByteArray(512)
    private val listeners = mutableListOf<() -> Unit>()

    override fun writer(baseChannel: Int, channelCount: Int) =
        Dmx.Buffer(channelsOut, baseChannel, channelCount)

    override fun sendFrame() {
        channelsOut.copyInto(channelsIn)
        updateListeners()
    }

    override fun allOff() {
        for (i in 0 until 512) channelsIn[i] = 0
        updateListeners()
    }

    fun listen(baseChannel: Int, channelCount: Int, listener: (Dmx.Buffer) -> Unit): Dmx.Buffer {
        return Dmx.Buffer(channelsIn, baseChannel, channelCount).also {
            listeners.add { listener.invoke(it) }
        }
    }

    private fun updateListeners() {
        listeners.forEach { it() }
    }
}