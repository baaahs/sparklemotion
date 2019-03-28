package baaahs

import baaahs.shows.RandomShow
import baaahs.shows.SomeDumbShow
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

lateinit var sheepSimulator: SheepSimulator

//fun main(args: Array<String>) {
//    main = Main()
//    main.start()
//}

class SheepSimulator {
    var display = getDisplay()
    var network = FakeNetwork(display = display.forNetwork())

    var dmxUniverse = FakeDmxUniverse()
    var sheepModel = SheepModel()

    val showMetas = listOf(
        SomeDumbShow.Meta(),
        RandomShow.Meta()
    )

    val pinky = Pinky(sheepModel, showMetas, network, dmxUniverse, display.forPinky())
    val mapper = Mapper(network, display.forMapper())
    val visualizer = Visualizer(sheepModel, dmxUniverse)

    fun start() {
        sheepModel.load()

        mapper.start()
        PinkyScope.launch { pinky.run() }

        visualizer.start()

        sheepModel.panels.forEach { panel ->
            val jsPanel = visualizer.showPanel(panel)
            val brain = Brain(network, display.forBrain(), jsPanel, panel)
            BrainScope.launch { randomDelay(1000); brain.run() }
        }

        sheepModel.eyes.forEach { eye ->
            visualizer.addEye(eye)
            Config.DMX_DEVICES[eye.name]
        }

        doRunBlocking {
            delay(200000L)
        }
    }
}

object PinkyScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}

object BrainScope : CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = EmptyCoroutineContext
}

expect fun getTimeMillis(): Long
expect fun doRunBlocking(block: suspend () -> Unit)

expect fun getResource(name: String): String