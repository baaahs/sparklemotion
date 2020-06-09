package baaahs

import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.glshaders.Plugins
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.ui.SaveAsFs
import kotlinext.js.jsObject
import react.ReactElement
import react.createElement

class WebUi(
    private val network: Network,
    private val pinkyAddress: Network.Address,
    private val filesystems: List<SaveAsFs>
) : HostedWebApp {

    override fun render(): ReactElement {
        val webUiClientLink = network.link("app")
        println("WebUi: my link is ${webUiClientLink.myAddress}")
        val pubSub = PubSub.Client(webUiClientLink, pinkyAddress, Ports.PINKY_UI_TCP).apply {
            install(gadgetModule)
            install(Plugins.findAll().serialModule)
        }

        return createElement(AppIndex, jsObject<AppIndexProps> {
            this.id = "Client Window"
            this.pubSub = pubSub
            this.filesystems = this@WebUi.filesystems
        })
    }

    override fun onClose() {
    }
}
