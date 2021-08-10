package baaahs.monitor

import baaahs.document
import baaahs.sim.HostedWebApp
import baaahs.visualizer.Visualizer
import baaahs.visualizer.remote.RemoteVisualizerClient
import kotlinext.js.jsObject
import org.w3c.dom.HTMLDivElement
import react.ReactElement
import react.createElement
import react.rClass

class MonitorUi(
    private val visualizer: Visualizer,
    private val remoteVisualizerClient: RemoteVisualizerClient
) : HostedWebApp {
    private val container = document.createElement("div") as HTMLDivElement

    init {
        container.className = "adminModelVisualizerContainer"
        visualizer.facade.container = container
        visualizer.render()
    }

    override fun render(): ReactElement {
        return createElement(MonitorPage::class.rClass, jsObject {
            this.containerDiv = container
            this.visualizer = this@MonitorUi.visualizer
            this.client = remoteVisualizerClient.facade
        })
    }

    override fun onClose() {
        visualizer.stopRendering = true
        remoteVisualizerClient.close()
    }
}
