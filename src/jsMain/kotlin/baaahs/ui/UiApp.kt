package baaahs.ui

import baaahs.HostedWebApp
import baaahs.PubSub
import baaahs.gadgetModule
import baaahs.jsx.AppIndex
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.sim.FakeNetwork
import kotlinext.js.jsObject
import react.ReactElement
import react.createElement

class UiApp(private val network: FakeNetwork, private val pinkyAddress: Network.Address) : HostedWebApp {
    override fun render(): ReactElement {
        val webUiClientLink = network.link()
        val pubSub = PubSub.Client(webUiClientLink, pinkyAddress, Ports.PINKY_UI_TCP).apply {
            install(gadgetModule)
        }

        return createElement(AppIndex::class.js, jsObject<AppIndex.Props> {
            this.pubSub = pubSub
        })
    }

    override fun onClose() {
    }
}
