package baaahs

import baaahs.net.Network
import baaahs.proto.*
import kotlinx.coroutines.delay

class Brain(
    private val network: Network,
    private val display: BrainDisplay,
    private val pixels: Pixels,
    private val illicitPanelHint: SheepModel.Panel
) : Network.UdpListener {
    private lateinit var link: Network.Link
    private var receivingInstructions: Boolean = false

    suspend fun run() {
        link = network.link()
        link.listenUdp(Ports.BRAIN, this)
        display.haveLink(link)

        sendHello()
    }

    private suspend fun sendHello() {
        while (true) {
            if (!receivingInstructions) {
                link.broadcastUdp(Ports.PINKY, BrainHelloMessage(illicitPanelHint.name))
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
                link.sendUdp(fromAddress, message.port, BrainIdResponse(illicitPanelHint.name))
            }
        }
    }
}
