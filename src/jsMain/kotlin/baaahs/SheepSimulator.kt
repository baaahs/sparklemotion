package baaahs

import baaahs.geom.Matrix4
import baaahs.geom.Vector3F
import baaahs.glsl.GlslRenderer
import baaahs.mapper.MappingSession
import baaahs.mapper.MappingSession.SurfaceData.PixelData
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.proto.Ports
import baaahs.shaders.GlslShader
import baaahs.shows.AllShows
import baaahs.shows.GlslShow
import baaahs.sim.*
import baaahs.visualizer.SurfaceGeometry
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizPixels
import decodeQueryParams
import info.laht.threekt.math.Vector3
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

@JsName("SheepSimulator")
class SheepSimulator {
    @Suppress("unused")
    val facade = Facade()

    private val queryParams = decodeQueryParams(document.location!!)
    val network = FakeNetwork()
    private val dmxUniverse = FakeDmxUniverse()
    private val model = selectModel()
    val visualizer = Visualizer(model)
    private val mapperFs = FakeFs()
    val fs = MergedFs(BrowserSandboxFs(), mapperFs)
    private val bridgeClient: BridgeClient = BridgeClient("${window.location.hostname}:${Ports.SIMULATOR_BRIDGE_TCP}")
    init {
//  TODO      GlslBase.plugins.add(SoundAnalysisPlugin(bridgeClient.soundAnalyzer))
    }
    val shows = AllShows.allShows

    init {
        shows.forEach { show ->
            if (show !is GlslShow) return@forEach
            try {
                fs.saveFile(fs.resolve("shaders", "${show.name}.glsl"), show.src)
            } catch (e: Exception) {
            }
        }
    }

    val glslContext = GlslShader.globalRenderContext
    val clock = JsClock()
    private val pinky = Pinky(
        model,
        shows,
        network,
        dmxUniverse,
        bridgeClient.beatSource,
        clock,
        fs,
        PermissiveFirmwareDaddy(),
        bridgeClient.soundAnalyzer,
        glslRenderer = GlslRenderer(glslContext, model.defaultUvTranslator)
    )
    private val brains: MutableList<Brain> = mutableListOf()

    private fun selectModel(): Model<*> =
        Pluggables.loadModel(queryParams["model"] ?: Pluggables.defaultModel)

    fun getPubSub(): PubSub.Client =
        PubSub.Client(network.link(), pinky.address, Ports.PINKY_UI_TCP).apply {
            install(gadgetModule)
        }

    fun start() = doRunBlocking {
        val simSurfaces = prepareSurfaces()

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

        simSurfaces.forEach { simSurface ->
            val vizPanel = visualizer.addSurface(simSurface.surfaceGeometry)
            vizPanel.vizPixels = VizPixels(vizPanel, simSurface.pixelPositions)

            val brain = Brain(simSurface.brainId.uuid, network, vizPanel.vizPixels ?: NullPixels)
            brains.add(brain)

            brainScope.launch { randomDelay(1000); brain.run() }
        }

        model.movingHeads.forEach { movingHead ->
            visualizer.addMovingHead(movingHead, dmxUniverse)
        }

        queryParams["show"]?.let { showName ->
            shows.find { it.name == showName }?.let { show -> pinky.switchToShow(show) }
        }

//        val users = storage.users.transaction { store -> store.getAll() }
//        println("users = ${users}")

        facade.notifyChanged()

        doRunBlocking {
            delay(200000L)
        }
    }

    private fun prepareSurfaces(): List<SimSurface> {
        val pixelDensity = queryParams.getOrElse("pixelDensity") { "0.2" }.toFloat()
        val pixelSpacing = queryParams.getOrElse("pixelSpacing") { "3" }.toFloat()
        val pixelArranger = SwirlyPixelArranger(pixelDensity, pixelSpacing)
        var totalPixels = 0

        val simSurfaces = model.allSurfaces.sortedBy(Model.Surface::name).mapIndexed { index, surface ->
            //            if (panel.name != "17L") return@forEachIndexed

            val surfaceGeometry = SurfaceGeometry(surface)
            val pixelPositions = pixelArranger.arrangePixels(surfaceGeometry)
            totalPixels += pixelPositions.size
            SimSurface(surface, surfaceGeometry, pixelPositions, BrainId("brain//$index"))
        }
        document.getElementById("visualizerPixelCount").asDynamic().innerText = totalPixels.toString()

        val mappingSessionPath = Storage(mapperFs).saveSession(
            MappingSession(clock.now(), simSurfaces.map { simSurface ->
                MappingSession.SurfaceData(simSurface.brainId.uuid, simSurface.surface.name,
                    simSurface.pixelPositions.map {
                        PixelData(Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat()), null, null)
                    }, null, null, null
                )
            }, Matrix4(emptyArray()), null, notes = "Simulated pixels")
        )
        mapperFs.renameFile(
            mappingSessionPath,
            fs.resolve("mapping", model.name, mappingSessionPath.name))
        return simSurfaces
    }

    class SimSurface(
        val surface: Model.Surface,
        val surfaceGeometry: SurfaceGeometry,
        val pixelPositions: Array<Vector3>,
        val brainId: BrainId
    )

    @JsName("switchToShow")
    fun switchToShow(show: Show) {
        pinky.switchToShow(show)
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

    inner class Facade : baaahs.ui.Facade() {
        val pinky: Pinky.Facade
            get() = this@SheepSimulator.pinky.facade
        val network: FakeNetwork.Facade
            get() = this@SheepSimulator.network.facade
        val visualizer: Visualizer.Facade
            get() = this@SheepSimulator.visualizer.facade
        val brains: List<Brain.Facade>
            get() = this@SheepSimulator.brains.map { it.facade }
    }
}

class JsClock : Clock {
    override fun now(): Time = Date.now() / 1000.0
}
