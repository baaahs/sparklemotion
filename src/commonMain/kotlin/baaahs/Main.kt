package baaahs

import kotlinx.coroutines.delay

lateinit var main: Main

fun main(args: Array<String>) {
    main = Main()
    main.start()
}

class Main {
    var display = getDisplay()
    var network = FakeNetwork(display = display.forNetwork())

    var sheepModel = SheepModel()

    val central = Central(network, display.forCentral())
    val mapper = Mapper(network, display.forMapper())

    fun start() {
        sheepModel.load()

        mapper.start()
        central.start()

        val panelCount = sheepModel.panels.size

        for (i in 0..panelCount) {
            val controller = SimController(network, display.forController())
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