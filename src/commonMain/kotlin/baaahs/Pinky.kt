package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

class Pinky(val network: Network, val display: PinkyDisplay) : Network.Listener {
    private lateinit var link: Network.Link
    private val brains: MutableMap<Network.Address, RemoteBrain> = mutableMapOf()

    fun run() {
        link = network.link()
        link.listen(Ports.PINKY, this)
    }

    fun start() {
        GlobalScope.launch { run() }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        when (parse(bytes)) {
            is BrainHelloMessage -> {
                foundBrain(RemoteBrain(fromAddress))
            }

            is MapperHelloMessage -> {
                sendMapperPong(fromAddress)
            }
        }

    }

    @Synchronized
    private fun sendMapperPong(fromAddress: Network.Address) {
        link.send(
            fromAddress,
            Ports.MAPPER,
            PinkyPongMessage(brains.values.map { it.fromAddress.toString() }).toBytes()
        )
    }

    @Synchronized
    private fun foundBrain(remoteBrain: RemoteBrain) {
        brains.put(remoteBrain.fromAddress, remoteBrain)
        display.brainCount = brains.size
    }
}

class RemoteBrain(val fromAddress: Network.Address) {
}