package baaahs

import kotlinx.serialization.serializer
import kotlin.js.JsName

class Ui(val network: Network, val pinkyAddress: Network.Address, val display: UiDisplay) {
    val link = network.link()

    private lateinit var pubSub: PubSub

    init {
        connect()
    }

    fun connect() {
        val showsTopic = PubSub.Topic("/shows", String.serializer())
        val currentShowTopic = PubSub.Topic("/currentShow", String.serializer())

        val pubSub = PubSub.Client(link, pinkyAddress, Ports.PINKY_UI_TCP)
        val context = UiContext(pubSub)
        val uiApp = createUiApp("uiView1", context)
        println("uiApp: $uiApp")

        val primaryColorChannel = pubSub.subscribe(Topics.primaryColor) { color: Color ->
            display.color = color
            println("UI: primary color is ${color}")
        }

        display.onColorChanged = { color -> primaryColorChannel.onChange(color) }
    }
}

class UiContext(
    @JsName("pubSub") val pubSub: PubSub.Client
)

expect fun createUiApp(elementId: String, uiContext: UiContext): Any

