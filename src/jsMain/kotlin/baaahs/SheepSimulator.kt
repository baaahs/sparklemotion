package baaahs

import baaahs.proto.Ports
import baaahs.shows.AllShows
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeMediaDevices
import baaahs.sim.FakeNetwork
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizPanel
import decodeQueryParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.browser.document

class SheepSimulator {
    private val display = JsDisplay()
    private val network = FakeNetwork(display = display.forNetwork())
    private val dmxUniverse = FakeDmxUniverse()
    private val sheepModel = SheepModel().apply { load() }
    private val shows = AllShows.allShows
    private val visualizer = Visualizer(sheepModel)
    private val pinky = Pinky(sheepModel, shows, network, dmxUniverse, display.forPinky())

    fun start() = doRunBlocking {
        val queryParams = decodeQueryParams(document.location!!)

        pinkyScope.launch { pinky.run() }

        val launcher = Launcher(document.getElementById("launcher")!!)
        launcher.add("Web UI") {
            val webUiClientLink = network.link()
            val pubSub = PubSub.Client(webUiClientLink, pinky.address, Ports.PINKY_UI_TCP).apply {
                install(gadgetModule)
            }
            document.asDynamic().createUiApp(pubSub)
        }.also { delay(1000); it.click() }

        launcher.add("Mapper") {
            val mapperDisplay = JsMapperDisplay(visualizer)

            val mapper = Mapper(network, sheepModel, mapperDisplay, FakeMediaDevices(visualizer))
            mapperScope.launch { mapper.start() }

            mapperDisplay
        }

        val pixelDensity = queryParams.getOrElse("pixelDensity") { "0.2" }.toFloat()
        val pixelSpacing = queryParams.getOrElse("pixelSpacing") { "2" }.toFloat()
        val pixelArranger = SwirlyPixelArranger(pixelDensity, pixelSpacing)
        var totalPixels = 0

        sheepModel.panels.sortedBy(SheepModel.Panel::name).forEachIndexed { index, panel ->
            //            if (panel.name != "17L") return@forEachIndexed

            val vizPanel = visualizer.addPanel(panel)
            val pixelPositions = pixelArranger.arrangePixels(vizPanel)
            vizPanel.vizPixels = VizPanel.VizPixels(pixelPositions)

            totalPixels += pixelPositions.size
            document.getElementById("visualizerPixelCount").asDynamic().innerText = totalPixels.toString()

            // This part is cheating... TODO: don't cheat!
            val pixelLocations = vizPanel.getPixelLocations()!!
            pinky.providePixelMapping(panel, pixelLocations)

            val brain = Brain("brain//$index", network, display.forBrain(), vizPanel.vizPixels ?: NullPixels)
            pinky.providePanelMapping(BrainId(brain.id), panel)
            brainScope.launch { randomDelay(1000); brain.run() }
        }

        sheepModel.eyes.forEach { eye ->
            visualizer.addMovingHead(eye, dmxUniverse)
        }

//        val users = storage.users.transaction { store -> store.getAll() }
//        println("users = ${users}")

        doRunBlocking {
            delay(200000L)
        }
    }

    object NullPixels : Pixels {
        override val size = 0

        override fun get(i: Int): Color = Color.BLACK
        override fun set(i: Int, color: Color) {}
        override fun set(colors: Array<Color>) {}
    }

    private val pinkyScope = CoroutineScope(Dispatchers.Main)
    private val brainScope = CoroutineScope(Dispatchers.Main)
    private val mapperScope = CoroutineScope(Dispatchers.Main)
}
