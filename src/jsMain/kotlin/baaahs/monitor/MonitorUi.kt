package baaahs.monitor

import baaahs.Pluggables
import baaahs.document
import baaahs.model.Model
import baaahs.net.Network
import baaahs.sim.HostedWebApp
import baaahs.util.Clock
import baaahs.util.JsClock
import baaahs.visualizer.Visualizer
import baaahs.visualizer.remote.RemoteVisualizerClient
import kotlinext.js.jsObject
import org.w3c.dom.HTMLDivElement
import react.ReactElement
import react.createElement

class MonitorUi(
    network: Network,
    pinkyAddress: Network.Address,
    model: Model,
    clock: Clock = JsClock
) : HostedWebApp {
    private val clientLink = network.link("monitor")
    private val container = document.createElement("div") as HTMLDivElement
    private val visualizer = Visualizer(model, clock)
    private val visualizerListenerClient =
        RemoteVisualizerClient(clientLink, pinkyAddress, visualizer, model, clock)

    init {
        container.className = "adminModelVisualizerContainer"
        visualizer.facade.container = container
        visualizer.render()
    }

    override fun render(): ReactElement {
        return createElement(MonitorPage::class.js, jsObject<MonitorPage.Props> {
            this.containerDiv = container
            this.visualizer = this@MonitorUi.visualizer
        })
    }

    override fun onClose() {
        visualizer.stopRendering = true
        visualizerListenerClient.close()
    }

    private fun selectModel(): Model = Pluggables.getModel()
}
