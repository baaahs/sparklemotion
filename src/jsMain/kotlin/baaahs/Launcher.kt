package baaahs

import baaahs.sim.FakeClientDevice
import baaahs.sim.FakeClientDeviceProps
import kotlinext.js.jsObject
import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import react.ReactElement
import react.createElement
import react.dom.render
import kotlin.dom.appendElement
import kotlin.dom.appendText

class Launcher(val parentNode: Element) {

    fun add(name: String, onLaunch: () -> HostedWebApp): HTMLButtonElement {

        return parentNode.appendElement("button") {
            appendText(name)

            (this as HTMLElement).onclick = {
                console.log("Launch $name", this)

                val containerDiv = document.createElement("div").also {
                    document.body?.appendChild(it)
                }

                // Into the darkness.
                val props = jsObject<FakeClientDeviceProps> {
                    this.name = name
                    width = 1024
                    height = 768
                    this.hostedWebApp = onLaunch()
                    onClose = { document.body?.removeChild(containerDiv) }
                }
                render(createElement(FakeClientDevice, props), containerDiv)
            }
        } as HTMLButtonElement
    }
}

external interface HostedWebApp {
    @JsName("render")
    fun render(): ReactElement

    @JsName("onClose")
    fun onClose()
}
