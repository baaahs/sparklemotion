package baaahs.sim

import baaahs.document
import baaahs.sim.ui.FakeClientDevice
import baaahs.sim.ui.FakeClientDeviceProps
import baaahs.ui.ErrorDisplay
import baaahs.util.Logger
import baaahs.window
import kotlinx.js.jso
import react.ReactElement
import react.createElement
import react.dom.render

object Launcher {
    fun launch(name: String, buildWebApp: () -> HostedWebApp) {
        console.log("Launch $name", this)

        val containerDiv = document.createElement("div").also {
            document.body?.appendChild(it)
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
                        this.resetErrorBoundary = { window.location.reload() }
                    })

                override fun onClose() {}
            }
        }

        val props = jso<FakeClientDeviceProps> {
            this.name = name
            width = 1024
            height = 768
            hostedWebApp = webApp
            onClose = { document.body?.removeChild(containerDiv) }
        }
        render(createElement(FakeClientDevice, props), containerDiv)
    }

    private val logger = Logger<Launcher>()
}