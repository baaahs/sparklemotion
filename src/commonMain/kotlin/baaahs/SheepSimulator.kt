package baaahs

import baaahs.shows.CompositeShow
import baaahs.shows.RandomShow
import baaahs.shows.SomeDumbShow
import kotlinx.coroutines.*

class SheepSimulator {
    var display = getDisplay()
    var network = FakeNetwork(display = display.forNetwork())

    var dmxUniverse = FakeDmxUniverse()
    var sheepModel = SheepModel()

    val showMetas = listOf(
        SomeDumbShow.Meta(),
        RandomShow.Meta(),
        CompositeShow.Meta()
    )

    val pinky = Pinky(sheepModel, showMetas, network, dmxUniverse, display.forPinky())
    val mapper = Mapper(network, display.forMapper())
    val visualizer = Visualizer(sheepModel, dmxUniverse)

    fun start() = doRunBlocking {
        sheepModel.load()

        mapperScope.launch { mapper.start() }
        pinkyScope.launch { pinky.run() }

        visualizer.start()

        sheepModel.panels.forEach { panel ->
            val jsPanel = visualizer.showPanel(panel)
            val brain = Brain(network, display.forBrain(), JsPixels(jsPanel), panel)
            brainScope.launch { randomDelay(1000); brain.run() }
        }

        sheepModel.eyes.forEach { eye ->
            visualizer.addEye(eye)
            Config.DMX_DEVICES[eye.name]
        }

        GlobalScope.launch {
            Ui(network, pinky.address, display.forUi())
        }

        doRunBlocking {
            delay(200000L)
        }
    }

    val pinkyScope = CoroutineScope(Dispatchers.Main)
    val brainScope = CoroutineScope(Dispatchers.Main)
    val mapperScope = CoroutineScope(Dispatchers.Main)
}
