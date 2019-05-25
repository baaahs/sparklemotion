package baaahs

import org.w3c.dom.Element
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLElement
import kotlin.dom.appendElement
import kotlin.dom.appendText

class Launcher(val parentNode: Element) {
    private val fakeDomContainer = FakeDomContainer()

    fun add(name: String, onLaunch: () -> HostedWebApp): HTMLButtonElement {
        return parentNode.appendElement("button") {
            console.log("launcher for $name", this)
            appendText(name)

            (this as HTMLElement).onclick = {
                fakeDomContainer.createFrame(name, onLaunch())
            }
        } as HTMLButtonElement
    }

}
