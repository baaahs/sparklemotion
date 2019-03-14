package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Mapper(val network: Network, val display: MapperDisplay) : Network.Listener {
    private lateinit var link: Network.Link
    private var isRunning: Boolean = false

    fun start() {
        link = network.link()
        link.listen(Ports.MAPPER, this)

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
        link.broadcast(Ports.PINKY, MapperHelloMessage(isRunning))
        delay(1000L)
        link.broadcast(Ports.BRAIN, BrainShaderMessage(SolidShaderBuffer().also { it.color = Color.BLACK }))
        link.broadcast(Ports.PINKY, MapperHelloMessage(isRunning))
        delay(1000L)
        link.broadcast(Ports.BRAIN, BrainShaderMessage(SolidShaderBuffer().also { it.color = Color.BLACK }))
        link.broadcast(Ports.BRAIN, BrainIdRequest(Ports.MAPPER))

        while (isRunning) {
            link.broadcast(Ports.PINKY, MapperHelloMessage(isRunning))

            delay(10000L)
        }

        link.broadcast(Ports.PINKY, MapperHelloMessage(isRunning))
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainIdResponse -> {
                link.send(fromAddress, Ports.BRAIN, BrainShaderMessage(SolidShaderBuffer().also { it.color = Color.WHITE }))
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
