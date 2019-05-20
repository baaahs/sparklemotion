package baaahs

import baaahs.shows.AllShows
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeNetwork
import kotlinx.coroutines.*

class SheepSimulator {
    var display = getDisplay()
    var network = FakeNetwork(display = display.forNetwork())

    var dmxUniverse = FakeDmxUniverse()
    var sheepModel = SheepModel().apply { load() }

    val showMetas = AllShows.allShows

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
                Ui(network, pinky.address)
            }
        }
    }

    val pinky = Pinky(sheepModel, showMetas, network, dmxUniverse, display.forPinky())

    fun start() = doRunBlocking {
        pinkyScope.launch { pinky.run() }

        doRunBlocking {
            delay(200000L)
        }
    }

    @JsName("visualize")
    fun visualize(threeVisualizer: dynamic) {
        sheepModel.panels.forEach { panel ->
            val jsPanel = threeVisualizer.addPanel(panel)
            val brain = Brain(network, display.forBrain(), JsPixels(jsPanel), panel)
            brainScope.launch { randomDelay(1000); brain.run() }
        }

        sheepModel.eyes.forEach { eye -> MovingHeadView(eye, dmxUniverse, threeVisualizer)
//            Config.DMX_DEVICES[eye.name]
        }
    }

    class MovingHeadView(movingHead: SheepModel.MovingHead, dmxUniverse: FakeDmxUniverse, val threeVisualizer: dynamic) {
        val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
        val device = Shenzarpy(dmxUniverse.reader(baseChannel, 16) { receivedDmxFrame() })
        val movingHeadJs = threeVisualizer.addMovingHead(movingHead)

        private fun receivedDmxFrame() {
            val colorWheelV = device.colorWheel
            val wheelColor = Shenzarpy.WheelColor.get(colorWheelV)
            threeVisualizer.adjustMovingHead(movingHeadJs, wheelColor.color, device.dimmer, device.pan, device.tilt)
        }
    }

    fun launchNewWebUi() = Ui(network, pinky.address)

    val pinkyScope = CoroutineScope(Dispatchers.Main)
    val brainScope = CoroutineScope(Dispatchers.Main)
    val mapperScope = CoroutineScope(Dispatchers.Main)
}
