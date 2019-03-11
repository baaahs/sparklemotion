package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

class Mapper(val network: Network, val display: MapperDisplay) : Network.Listener {
    private lateinit var link: Network.Link

    fun start() {
        GlobalScope.launch {
            val timeMillis = 2000 + Random.nextInt() % 1000
            delay(timeMillis.toLong())
            run()
        }
    }

    fun run() {
        link = network.link()
        link.listen(Ports.MAPPER, this)
//        display.haveLink(link)
        link.broadcast(Ports.PINKY, MapperHelloMessage().toBytes())
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is PinkyPongMessage -> {
                println("Mapper: pong from pinky: ${message.brainIds}")
                message.brainIds.forEach { id ->
                    println("id = ${id}")
//                    display.
                }
            }
        }
    }

}
