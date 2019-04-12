package baaahs

import baaahs.shaders.SolidShader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Mapper(val network: Network, val display: MapperDisplay) : Network.UdpListener {
    private lateinit var link: Network.Link
    private var isRunning: Boolean = false

    fun start() {
        link = network.link()
        link.listenUdp(Ports.MAPPER, this)

        display.onStart = {
            if (!isRunning) {
                isRunning = true

                GlobalScope.launch {
                    run()
                }
            }
        }

        display.onStop = {
            if (isRunning) {
                isRunning = false
            }
        }

    }

    suspend fun run() {
        // shut down Pinky, advertise for Brains...
        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
        delay(1000L)
        link.broadcastUdp(Ports.BRAIN, BrainShaderMessage(SolidShader().apply { buffer.color = Color.BLACK }))
        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
        delay(1000L)
        link.broadcastUdp(Ports.BRAIN, BrainShaderMessage(SolidShader().apply { buffer.color = Color.BLACK }))
        link.broadcastUdp(Ports.BRAIN, BrainIdRequest(Ports.MAPPER))

        while (isRunning) {
            link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))

            delay(10000L)
        }

        link.broadcastUdp(Ports.PINKY, MapperHelloMessage(isRunning))
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainIdResponse -> {
                link.sendUdp(
                    fromAddress,
                    Ports.BRAIN,
                    BrainShaderMessage(SolidShader().apply { buffer.color = Color.WHITE })
                )
            }

            is PinkyPongMessage -> {
                message.brainIds.forEach { id ->
                    println("id = ${id}")
//                    display.
                }
            }
        }
    }

}
