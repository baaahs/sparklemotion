package baaahs

import kotlinx.coroutines.delay

fun main(args: Array<String>) {
    Main().start()
}

class Main {
    var network = FakeNetwork()

    val central = Central(network)

    fun start() {
        central.start()

        val panelCount = 1000

        for (i in 0..panelCount) {
            val controller = SimController(network)
            controller.start()
        }

        doRunBlocking {
            delay(200000L)
        }

    }
}

expect fun doRunBlocking(block: suspend () -> Unit)
