package baaahs

import kotlinx.coroutines.delay

class Brain(
    private val network: Network,
    private val display: BrainDisplay,
    private val pixels: Pixels,
    private val illicitPanelHint: SheepModel.Panel
) : Network.Listener {
    private lateinit var link: Network.Link
    private var receivingInstructions: Boolean = false

    suspend fun run() {
        link = network.link()
        link.listen(Ports.BRAIN, this)
        display.haveLink(link)

        sendHello()
    }

    private suspend fun sendHello() {
        while (true) {
            if (!receivingInstructions) {
                link.broadcast(Ports.PINKY, BrainHelloMessage(illicitPanelHint.name))
            }

            delay(60000)
        }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainShaderMessage -> {
                val shaderImpl = message.shader.createImpl(pixels)
                shaderImpl.draw()
            }
            is BrainIdRequest -> {
                link.send(fromAddress, message.port, BrainIdResponse(""))
            }
        }
    }
}
