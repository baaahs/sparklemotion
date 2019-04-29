package baaahs

import baaahs.shows.CompositeShow
import baaahs.shows.RandomShow
import baaahs.shows.SomeDumbShow
import baaahs.shows.ThumpShow
import kotlinx.coroutines.*

class SheepSimulator {
    var display = getDisplay()
    var network = FakeNetwork(display = display.forNetwork())

    var dmxUniverse = FakeDmxUniverse()
    var sheepModel = SheepModel()

    val showMetas = listOf(
        SomeDumbShow.Meta(),
        RandomShow.Meta(),
        CompositeShow.Meta(),
        ThumpShow.Meta()
    )

    val visualizer = Visualizer(sheepModel, dmxUniverse).also {
        it.onNewMapper = {
            it.setMapperRunning(true)
            mapperScope.launch {
                Mapper(network, sheepModel, JsMapperDisplay(FakeDomContainer()), it.mediaDevices).apply {
                    this.addCloseListener { it.setMapperRunning(false) }
                    start()
                }
            }
        }

        it.onNewUi = {
            GlobalScope.launch {
                Ui(network, pinky.address, JsUiDisplay(FakeDomContainer()))
            }
        }
    }

    val pinky = Pinky(sheepModel, showMetas, network, dmxUniverse, display.forPinky())

    fun start() = doRunBlocking {
        sheepModel.load()

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

        doRunBlocking {
            delay(200000L)
        }
    }

    val pinkyScope = CoroutineScope(Dispatchers.Main)
    val brainScope = CoroutineScope(Dispatchers.Main)
    val mapperScope = CoroutineScope(Dispatchers.Main)
}
