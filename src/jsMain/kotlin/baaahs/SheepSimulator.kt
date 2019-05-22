package baaahs

import baaahs.proto.Ports
import baaahs.shows.AllShows
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeMediaDevices
import baaahs.sim.FakeNetwork
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.*
import kotlin.browser.document

class SheepSimulator {
    private val display = JsDisplay()
    private val network = FakeNetwork(display = display.forNetwork())
    private val dmxUniverse = FakeDmxUniverse()
    private val sheepModel = SheepModel().apply { load() }
    private val showMetas = AllShows.allShows
    private val visualizer = Visualizer(sheepModel)
    private val pinky = Pinky(sheepModel, showMetas, network, dmxUniverse, display.forPinky())

    fun start() = doRunBlocking {
        pinkyScope.launch { pinky.run() }

        val launcher = Launcher(document.getElementById("launcher")!!)
        launcher.add("Web UI") {
            val webUiClientLink = network.link()
            val pubSub = PubSub.Client(webUiClientLink, pinky.address, Ports.PINKY_UI_TCP).apply {
                install(gadgetModule)
            }
            document.asDynamic().createUiApp(pubSub)
        }

        launcher.add("Mapper") {
            visualizer.rotate = false
            val mapperDisplay = JsMapperDisplay()

            val mapper = Mapper(network, sheepModel, mapperDisplay, FakeMediaDevices(visualizer))
            mapperScope.launch { mapper.start() }

            mapperDisplay
        }

        sheepModel.panels.forEach { panel ->
            val jsPanel = visualizer.addPanel(panel)
            val brain = Brain(network, display.forBrain(), jsPanel.vizPixels ?: NullPixels, panel)
            brainScope.launch { randomDelay(1000); brain.run() }
        }

        sheepModel.eyes.forEach { eye ->
            visualizer.addMovingHead(eye, dmxUniverse)
        }

        doRunBlocking {
            delay(200000L)
        }
    }

    object NullPixels : Pixels {
        override val count = 0
        override fun set(colors: Array<Color>) {}
    }

    private val pinkyScope = CoroutineScope(Dispatchers.Main)
    private val brainScope = CoroutineScope(Dispatchers.Main)
    private val mapperScope = CoroutineScope(Dispatchers.Main)
}
