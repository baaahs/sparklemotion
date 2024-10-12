package baaahs.sim

import baaahs.dmx.Dmx

class SimDmxDriver(fakeDmxUniverse: FakeDmxUniverse) : Dmx.Driver {
    private val fakeDmxDevice = SimDmxDevice(fakeDmxUniverse)

    override fun findDmxDevices(): List<Dmx.Device> = listOf(fakeDmxDevice)
}

class SimDmxDevice(private val fakeDmxUniverse: FakeDmxUniverse) : Dmx.Device {
    override val id: String get() = "dmx-simulator"
    override val name: String get() = "DMX Simulator"

    override fun asUniverse(universeListener: Dmx.UniverseListener?) =
        object : Dmx.Universe() {
            override fun writer(baseChannel: Int, channelCount: Int): Dmx.Buffer =
                fakeDmxUniverse.writer(baseChannel, channelCount)

            override fun sendFrame() {
                fakeDmxUniverse.sendFrame()
                universeListener?.onSend(id, fakeDmxUniverse.channels)
            }

            override fun allOff() {
                fakeDmxUniverse.allOff()
            }
        }
}

class FakeDmxUniverse : Dmx.Universe() {
    internal val channels = ByteArray(512)
    private val listeners = mutableListOf<() -> Unit>()

    override fun writer(baseChannel: Int, channelCount: Int) =
        Dmx.Buffer(channels, baseChannel, channelCount)

    override fun sendFrame() {
        updateListeners()
    }

    override fun allOff() {
        for (i in 0 until 512) channels[i] = 0
        updateListeners()
    }

    fun buffer(baseChannel: Int, channelCount: Int): Dmx.Buffer {
        return Dmx.Buffer(channels, baseChannel, channelCount)
    }

    fun listen(listener: () -> Unit) {
        listeners.add { listener.invoke() }
    }

    fun listen(baseChannel: Int, channelCount: Int, listener: (Dmx.Buffer) -> Unit): Dmx.Buffer {
        return Dmx.Buffer(channels, baseChannel, channelCount).also {
            listeners.add { listener.invoke(it) }
        }
    }

    private fun updateListeners() {
        listeners.forEach { it() }
    }
}