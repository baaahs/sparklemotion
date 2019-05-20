package baaahs

import baaahs.net.Network
import baaahs.proto.Ports
import org.w3c.dom.HTMLElement
import kotlin.js.JsName

class Ui(val network: Network, val pinkyAddress: Network.Address) : HostedWebApp {
    val link = network.link()

    @JsName("pubSub")
    private lateinit var pubSub: PubSub.Client

    init {
        connect()
    }

    fun connect() {
        pubSub = PubSub.Client(link, pinkyAddress, Ports.PINKY_UI_TCP)
        pubSub.install(gadgetModule)
    }

    override fun onRender(container: HTMLElement) {
        js("ReactDOM.render(\"<UI/>\", container);")
    }

    override fun onResize(container: HTMLElement) {
    }

    override fun onClose() {
    }
}
