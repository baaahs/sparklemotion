package baaahs

import kotlinx.coroutines.*

interface Controller {

}

public class SimController(val network: Network): Controller, Network.Listener {
    private lateinit var link: Network.Link

    fun run() {
        link = network.link(this)
        println("hi, i'm ${link.myAddress}!")

        link.broadcast(HelloMessage().toBytes())
    }

    fun start(): Controller {
        GlobalScope.launch { run() }
        return this
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {

    }
}
