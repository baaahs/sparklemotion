package baaahs.midi

import baaahs.document
import baaahs.sim.HostedWebApp
import js.objects.jso
import react.ReactElement
import react.createElement
import react.react
import web.html.HTMLDivElement

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
