package baaahs

import kotlinx.coroutines.delay

lateinit var main: Main

//fun main(args: Array<String>) {
//    main = Main()
//    main.start()
//}

class Main {
    var display = getDisplay()
    var network = FakeNetwork(display = display.forNetwork())

    var sheepModel = SheepModel()
    val pinky = Pinky(sheepModel, network, display.forPinky())
    val mapper = Mapper(network, display.forMapper())
    val visualizer = Visualizer(sheepModel)

    fun start() {
        sheepModel.load()

        mapper.start()
        pinky.start()

        visualizer.start()

        sheepModel.panels.forEach { panel ->
            val jsPanel = visualizer.showPanel(panel)
            SimBrain(network, display.forBrain(), jsPanel, panel).start()
        }
        startRender()

        doRunBlocking {
            delay(200000L)
        }
    }
}

expect fun getTimeMillis(): Long
expect fun doRunBlocking(block: suspend () -> Unit)

expect fun getResource(name: String): String