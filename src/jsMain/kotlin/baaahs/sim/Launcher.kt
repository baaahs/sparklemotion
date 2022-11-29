package baaahs.sim

import baaahs.document
import baaahs.sim.ui.FakeClientDevice
import baaahs.sim.ui.FakeClientDeviceProps
import baaahs.ui.ErrorDisplay
import baaahs.util.Logger
import kotlinx.js.jso
import react.ReactElement
import react.createElement
import react.dom.client.createRoot
import web.location.location

object Launcher {
    fun launch(name: String, buildWebApp: () -> HostedWebApp) {
        console.log("Launch $name", this)

        val containerDiv = document.createElement("div").also {
            document.body.appendChild(it)
        }

        val webApp = try {
            // Into the darkness.
            buildWebApp().also { it.onLaunch() }
        } catch (e: Exception) {
            logger.error(e) { "Failed to launch $name." }
            object : HostedWebApp {
                override fun render(): ReactElement<*> =
                    createElement(ErrorDisplay, jso {
                        this.error = e.asDynamic()
                        this.componentStack = e.stackTraceToString()
                        this.resetErrorBoundary = { location.reload() }
                    })

                override fun onClose() {}
            }
        }

        val root = createRoot(containerDiv)
        val props = jso<FakeClientDeviceProps> {
            this.name = name
            width = 1024
            height = 768
            hostedWebApp = webApp
            onClose = {
                document.body.removeChild(containerDiv)
                root.unmount()
            }
        }
        root.render(createElement(FakeClientDevice, props))
    }

    private val logger = Logger<Launcher>()
}