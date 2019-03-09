package baaahs

import kotlinx.coroutines.*

fun main(args : Array<String>) {
    Main().start()
}

class Main {
    var network = FakeNetwork()
    val central = Central(network)
    val controllers: MutableSet<Controller> = HashSet()

    fun start() {
        central.start()

        for (i in 0..100) {
            controllers.add(SimController(network).start())
        }

        doRunBlocking {
            delay(2000L)
        }

    }
}

expect fun doRunBlocking(block: suspend () -> Unit)