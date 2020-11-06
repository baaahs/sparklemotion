package baaahs

import baaahs.admin.AdminPage
import baaahs.model.Model
import baaahs.net.Network
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VisualizerListenerClient
import kotlinext.js.jsObject
import org.w3c.dom.HTMLDivElement
import react.ReactElement
import react.createElement

class AdminUi(network: Network, pinkyAddress: Network.Address, model: Model) : HostedWebApp {
    private val clientLink = network.link("admin")
    private val container = document.createElement("div") as HTMLDivElement
    private val visualizer = Visualizer(model)
    private val visualizerListenerClient = VisualizerListenerClient(clientLink, pinkyAddress, visualizer, model)

    init {
        container.className = "adminModelVisualizerContainer"
        visualizer.facade.container = container
        visualizer.render()
    }

    override fun render(): ReactElement {
        return createElement(AdminPage::class.js, jsObject<AdminPage.Props> {
            this.containerDiv = container
            this.visualizer = this@AdminUi.visualizer
        })
    }

    override fun onClose() {
        visualizer.stopRendering = true
        visualizerListenerClient.close()
    }

    private fun selectModel(): Model = Pluggables.getModel()
}
