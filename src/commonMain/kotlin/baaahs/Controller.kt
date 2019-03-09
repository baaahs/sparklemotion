package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

interface Controller {

}

public class SimController(val network: Network): Controller, Network.Listener {
    private lateinit var link: Network.Link

    fun run() {
        link = network.link(this)
        println("hi, i'm ${link.myAddress}!")

        link.broadcast(HelloMessage().toBytes())
    }

    fun start() {
        GlobalScope.launch { run() }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {

    }
}
