package baaahs.sim

import baaahs.document
import baaahs.sim.ui.FakeClientDevice
import baaahs.sim.ui.FakeClientDeviceProps
import baaahs.ui.ErrorDisplay
import baaahs.util.Logger
import js.core.jso
import react.ReactElement
import react.createElement
import react.dom.client.Root
import react.dom.client.createRoot
import web.location.location

object Launcher {
    fun launch(name: String, buildWebApp: () -> HostedWebApp): Window {
        console.log("Launch $name", this)

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

        val containerDiv = document.createElement("div").also {
            document.body.appendChild(it)
        }

        val root = createRoot(containerDiv)
        val props = jso<FakeClientDeviceProps> {
            this.name = name
            width = 1024
            height = 768
            render = { webApp.render() }
            onClose = {
                document.body.removeChild(containerDiv)
                root.unmount()
                webApp.onClose()
            }
        }
        root.render(createElement(FakeClientDevice, props))
        return Window(name, webApp, root)
    }

    class Window(val name: String, val webApp: HostedWebApp, root: Root)

    private val logger = Logger<Launcher>()
}