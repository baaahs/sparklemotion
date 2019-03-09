package baaahs

import kotlinx.coroutines.delay

lateinit var main: Main

fun main(args: Array<String>) {
    main = Main()
    main.start()
}

class Main {
    var network = FakeNetwork()

    var sheepModel = SheepModel()
    val central = Central(network)

    fun start() {
        sheepModel.load()

        central.start()

        val panelCount = 3

        for (i in 0..panelCount) {
            val controller = SimController(network)
            controller.start()
        }

        doRunBlocking {
            delay(200000L)
        }

    }
}

fun getSheepModel(): SheepModel = main.sheepModel

expect fun doRunBlocking(block: suspend () -> Unit)

expect fun getResource(name: String): String