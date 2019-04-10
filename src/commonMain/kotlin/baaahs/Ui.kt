package baaahs

import kotlinx.serialization.serializer

class Ui(val network: Network, val pinkyAddress: Network.Address, val display: UiDisplay) {
    val link = network.link()

    private lateinit var pubSub: PubSub

    fun connectTo(pinkyAddress: Network.Address) {
        val showsTopic = PubSub.Topic("/shows", String.serializer())
        val currentShowTopic = PubSub.Topic("/currentShow", String.serializer())

        val pubSub = PubSub.Client(link, pinkyAddress, Ports.PINKY_UI_TCP)

        val primaryColorChannel = pubSub.subscribe(Pinky.primaryColorTopic) { color: Color ->
            display.color = color
            println("UI: primary color is ${color}")
        }

        display.onColorChanged = { color -> primaryColorChannel.onChange(color) }
    }
}
