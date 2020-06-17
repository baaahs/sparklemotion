package baaahs

import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.ui.SaveAsFs
import kotlinext.js.jsObject
import react.ReactElement
import react.createElement

/** Changes here should also be applied to [baaahs.sim.ui.AppWindow]. */
class WebUi(
    private val network: Network,
    private val pinkyAddress: Network.Address,
    private val filesystems: List<SaveAsFs>,
    private val showResources: MutableShowResources
) : HostedWebApp {

    override fun render(): ReactElement {
        val webUiClientLink = network.link("app")
        println("WebUi: my link is ${webUiClientLink.myAddress}")
        val pubSub = PubSub.Client(webUiClientLink, pinkyAddress, Ports.PINKY_UI_TCP)

        return createElement(AppIndex, jsObject<AppIndexProps> {
            this.id = "Client Window"
            this.pubSub = pubSub
            this.filesystems = this@WebUi.filesystems
            this.showResources = this@WebUi.showResources
        })
    }

    override fun onClose() {
    }
}
