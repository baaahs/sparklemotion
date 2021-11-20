package baaahs.admin

import baaahs.PubSub
import baaahs.controller.SacnDevice
import baaahs.dmx.DmxInfo
import baaahs.net.Network
import baaahs.plugin.ClientPlugins
import baaahs.sm.brain.BrainInfo
import baaahs.sm.brain.proto.Ports
import baaahs.sm.webapi.Topics
import baaahs.subscribeProperty

class AdminClient(
    private val plugins: ClientPlugins,
    network: Network,
    pinkyAddress: Network.Address
) {
    val facade = Facade()

    private val adminClientLink = network.link("admin")
    private val pubSub = PubSub.Client(adminClientLink, pinkyAddress, Ports.PINKY_UI_TCP)
    private val pubSubListener = { facade.notifyChanged() }.also {
        pubSub.addStateChangeListener(it)
    }

    private val brains by subscribeProperty(pubSub, Topics.brains, emptyMap()) { facade.notifyChanged() }
    private val dmxDevices by subscribeProperty(pubSub, Topics.dmxDevices, emptyMap()) { facade.notifyChanged() }
    private val sacnDevices by subscribeProperty(pubSub, Topics.sacnDevices, emptyMap()) { facade.notifyChanged() }

    inner class Facade : baaahs.ui.Facade() {
        val plugins: ClientPlugins
            get() = this@AdminClient.plugins

        val brains: Map<String, BrainInfo>
            get() = this@AdminClient.brains

        val dmxDevices: Map<String, DmxInfo>
            get() = this@AdminClient.dmxDevices

        val sacnDevices: Map<String, SacnDevice>
            get() = this@AdminClient.sacnDevices
    }

    fun onClose() {
        pubSub.removeStateChangeListener(pubSubListener)
    }
}
