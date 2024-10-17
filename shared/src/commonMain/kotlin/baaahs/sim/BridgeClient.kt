package baaahs.sim

import baaahs.PubSub
import baaahs.net.Network
import baaahs.sm.brain.proto.Ports
import baaahs.util.Logger

class BridgeClient(
    network: Network,
    pinkyAddress: Network.Address
) {
    val pubSub: PubSub.Client

    init {
        val link = network.link("BridgeClient")
        pubSub = PubSub.Client(link, pinkyAddress, Ports.SIMULATOR_BRIDGE_TCP)
    }

    companion object {
        private val logger = Logger<BridgeClient>()
    }
}
