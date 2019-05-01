package baaahs

import baaahs.net.Network
import kotlin.js.JsName

class Ui(val network: Network, val pinkyAddress: Network.Address, val display: UiDisplay) {
    val link = network.link()

    private lateinit var pubSub: PubSub

    init {
        connect()
    }

    fun connect() {
        val pubSub = PubSub.Client(link, pinkyAddress, Ports.PINKY_UI_TCP)
        val context = UiContext(pubSub)
        display.createApp(context)
    }
}

class UiContext(
    @JsName("pubSub") val pubSub: PubSub.Client
)

expect fun createUiApp(elementId: String, uiContext: UiContext): Any

interface UiDisplay {
    fun createApp(uiContext: UiContext)
}