package baaahs.client

import baaahs.HostedWebApp
import baaahs.PubSub
import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslBase
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.ui.SaveAsFs
import kotlinext.js.jsObject
import react.ReactElement
import react.createElement

class WebClient(
    network: Network,
    pinkyAddress: Network.Address,
    private val filesystems: List<SaveAsFs>,
    plugins: Plugins = Plugins.findAll()
) : HostedWebApp {
    private val webClientLink = network.link("app")
    private val pubSub = PubSub.Client(webClientLink, pinkyAddress, Ports.PINKY_UI_TCP)
    private val glslContext = GlslBase.jsManager.createContext()
    private val showResources = ClientShowResources(plugins, glslContext, pubSub)

    override fun render(): ReactElement {
        println("WebClient: my link is ${webClientLink.myAddress}")

        return createElement(AppIndex, jsObject<AppIndexProps> {
            this.id = "Client Window"
            this.pubSub = this@WebClient.pubSub
            this.filesystems = this@WebClient.filesystems
            this.showResources = this@WebClient.showResources
        })
    }

    override fun onClose() {
    }
}
