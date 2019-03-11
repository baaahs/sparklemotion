package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

interface Brain {

}

class SimBrain(
    private val network: Network,
    private val display: BrainDisplay,
    private val jsPanel: JsPanel
) : Brain, Network.Listener {
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
        link.listen(Ports.BRAIN, this)
        display.haveLink(link)
        jsPanel.select()

        sendHello()
    }

    private suspend fun sendHello() {
        while (true) {
            if (!receivingInstructions) {
                link.broadcast(Ports.PINKY, BrainHelloMessage().toBytes())
            }

            delay(60000)
        }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {

    }
}
