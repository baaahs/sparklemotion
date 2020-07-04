package baaahs

import baaahs.app.ui.AppIndex
import baaahs.app.ui.AppIndexProps
import baaahs.client.ClientShowResources
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslBase
import baaahs.net.Network
import baaahs.proto.Ports
import baaahs.ui.SaveAsFs
import kotlinext.js.jsObject
import react.ReactElement
import react.createElement

/** Changes here should also be applied to [baaahs.sim.ui.AppWindow]. */
class WebUi(
    network: Network,
    pinkyAddress: Network.Address,
    private val filesystems: List<SaveAsFs>,
    plugins: Plugins = Plugins.findAll()
) : HostedWebApp {
    private val webUiClientLink = network.link("app")
    private val pubSub = PubSub.Client(webUiClientLink, pinkyAddress, Ports.PINKY_UI_TCP)
    private val glslContext = GlslBase.jsManager.createContext()
    private val showResources = ClientShowResources(plugins, glslContext, pubSub)

    override fun render(): ReactElement {
        println("WebUi: my link is ${webUiClientLink.myAddress}")

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
