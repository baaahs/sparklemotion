package baaahs.midi

import baaahs.document
import baaahs.sim.HostedWebApp
import dom.html.HTMLDivElement
import kotlinx.js.jso
import react.ReactElement
import react.createElement
import react.react

class MIDIUi() : HostedWebApp {
    private val container = document.createElement("div") as HTMLDivElement

    init {
        container.className = "adminMIDIDiagnosticContainer"
    }

    override fun render(): ReactElement<*> {
        return createElement(MIDIPage::class.react, jso {
            this.containerDiv = container
        })
    }

    override fun onClose() {
    }
}
