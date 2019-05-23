package baaahs

import baaahs.net.Network
import baaahs.proto.Ports

object WebUi {
    @JsName("createPubSubClient")
    fun createPubSubClient(network: Network, pinkyAddress: Network.Address): PubSub.Client =
        PubSub.Client(network.link(), pinkyAddress, Ports.PINKY_UI_TCP).apply {
            install(gadgetModule)
        }
}