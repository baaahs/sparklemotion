package baaahs

import baaahs.geom.Vector3F
import baaahs.glsl.GlslBase
import baaahs.proto.Ports
import baaahs.shaders.SoundAnalysisPlugin
import baaahs.shows.AllShows
import baaahs.sim.*
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizSurface
import decodeQueryParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLInputElement
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

@JsName("SheepSimulator")
class SheepSimulator {
    private val queryParams = decodeQueryParams(document.location!!)
    private val display = JsDisplay()
    private val network = FakeNetwork(display = display.forNetwork())
    private val dmxUniverse = FakeDmxUniverse()
    private val model = selectModel()
    val visualizer = Visualizer(
        model,
        display.forVisualizer(),
        document.getElementById("sheepView")!! as HTMLDivElement,
        document.getElementById("selectionInfo")!! as HTMLDivElement
    )
    private val fs = FakeFs()
    private val bridgeClient: BridgeClient = BridgeClient("${window.location.hostname}:${Ports.SIMULATOR_BRIDGE_TCP}")
    private val pinkyDisplay = display.forPinky()
    init {
        GlslBase.plugins.add(SoundAnalysisPlugin(bridgeClient.soundAnalyzer))
    }
    public val shows = AllShows.allShows
    private val pinky = Pinky(
        model, shows, network, dmxUniverse, bridgeClient.beatSource, JsClock(), fs,
        PermissiveFirmwareDaddy(), pinkyDisplay, bridgeClient.soundAnalyzer,
        prerenderPixels = true
    )

    private fun selectModel(): Model<*> =
        Pluggables.loadModel(queryParams["model"] ?: Pluggables.defaultModel)

    fun getPubSub(): PubSub.Client =
        PubSub.Client(network.link(), pinky.address, Ports.PINKY_UI_TCP).apply {
            install(gadgetModule)
        }

    fun start() = doRunBlocking {
        pinkyScope.launch { pinky.run() }

        val launcher = Launcher(document.getElementById("launcher")!!)
        launcher.add("Web UI") {
            WebUi(network, pinky.address)
        }.also { delay(1000); it.click() }

        launcher.add("Mapper") {
            val mapperUi = JsMapperUi(visualizer)
            val mediaDevices = FakeMediaDevices(visualizer)
            val mapper = Mapper(network, model, mapperUi, mediaDevices, pinky.address)
            mapperScope.launch { mapper.start() }

            mapperUi
        }

        launcher.add("Admin UI") {
            AdminUi(network, pinky.address)
        }

        val pixelDensity = queryParams.getOrElse("pixelDensity") { "0.2" }.toFloat()
        val pixelSpacing = queryParams.getOrElse("pixelSpacing") { "3" }.toFloat()
        val pixelArranger = SwirlyPixelArranger(pixelDensity, pixelSpacing)
        var totalPixels = 0

        model.allSurfaces.sortedBy(Model.Surface::name).forEachIndexed { index, surface ->
            //            if (panel.name != "17L") return@forEachIndexed

            val vizPanel = visualizer.addSurface(surface)
            val pixelPositions = pixelArranger.arrangePixels(vizPanel)
            vizPanel.vizPixels = VizSurface.VizPixels(vizPanel, pixelPositions)

            totalPixels += pixelPositions.size
            document.getElementById("visualizerPixelCount").asDynamic().innerText = totalPixels.toString()

            // This part is cheating... TODO: don't cheat!
            val pixelLocations = vizPanel.getPixelLocationsInModelSpace()!!.map {
                Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat())
            }
            pinky.providePixelMapping_CHEAT(surface, pixelLocations)

            val brain = Brain("brain//$index", network, display.forBrain(), vizPanel.vizPixels ?: NullPixels)
            pinky.providePanelMapping_CHEAT(BrainId(brain.id), surface)
            brainScope.launch { randomDelay(1000); brain.run() }
        }

        model.movingHeads.forEach { movingHead ->
            visualizer.addMovingHead(movingHead, dmxUniverse)
        }

        val showName = queryParams.get("show")
        showName?.let {
            val show = shows.find { it.name == showName }
            show?.let {
                pinky.switchToShow(show)
                pinkyDisplay.selectedShow = show
            }
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

class JsClock : Clock {
    override fun now(): Time = Date.now() / 1000.0
}
