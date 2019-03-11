package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

interface Controller {

}

class SimController(
    private val network: Network,
    private val display: ControllerDisplay,
    private val jsPanel: JsPanel
) : Controller, Network.Listener {
    private lateinit var link: Network.Link
    private var receivingInstructions: Boolean = false

    fun start() {
        GlobalScope.launch {
            val timeMillis = Random.nextInt() % 1000
            delay(timeMillis.toLong())
            run()
        }
    }

    suspend fun run() {
        link = network.link()
        link.listen(Ports.CONTROLLER, this)
        display.haveLink(link)
        jsPanel.select()

        sendHello()
    }

    private suspend fun sendHello() {
        while (true) {
            if (!receivingInstructions) {
                link.broadcast(Ports.CENTRAL, ControllerHelloMessage().toBytes())
            }

            delay(60000)
        }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {

    }
}
