package baaahs

import baaahs.client.WebClient
import baaahs.di.*
import baaahs.geom.Matrix4
import baaahs.geom.Vector3F
import baaahs.io.Fs
import baaahs.io.ResourcesFs
import baaahs.mapper.MappingSession
import baaahs.mapper.MappingSession.SurfaceData.PixelData
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.net.FragmentingUdpLink
import baaahs.plugin.Plugins
import baaahs.proto.Ports
import baaahs.sim.*
import baaahs.util.Clock
import baaahs.util.KoinLogger
import baaahs.util.LoggerConfig
import baaahs.visualizer.SurfaceGeometry
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import baaahs.visualizer.VizPixels
import decodeQueryParams
import kotlinx.coroutines.*
import org.koin.core.logger.Level
import org.koin.core.logger.PrintLogger
import org.koin.core.qualifier.named
import org.koin.dsl.koinApplication
import three.js.Vector3

class SheepSimulator(val model: Model) {
    @Suppress("unused")
    val facade = Facade()

    private val queryParams = decodeQueryParams(document.location!!)
    val network = FakeNetwork()

    private val bridgeClient: BridgeClient = BridgeClient("${window.location.hostname}:${Ports.SIMULATOR_BRIDGE_TCP}")
    init {
        window.asDynamic().simulator = this
        window.asDynamic().LoggerConfig = LoggerConfig
//  TODO      GlslBase.plugins.add(SoundAnalysisPlugin(bridgeClient.soundAnalyzer))
    }

    init {
        GlobalScope.launch { cleanUpBrowserStorage() }
    }

    val pinkyLink = FragmentingUdpLink(network.link("pinky"))

    val injector = koinApplication {
        logger(KoinLogger())

        modules(
            JsSimPlatformModule(network, model).getModule(),
            JsSimulatorModule().getModule(),
            JsSimPinkyModule(pinkyLink).getModule(),
            JsWebClientModule(pinkyLink.myAddress).getModule(),
            JsMapperClientModule(pinkyLink.myAddress).getModule(),
            JsBeatLinkPluginModule(bridgeClient.beatSource).getModule()
        )
    }.koin

    val pinky = injector.createScope<Pinky>().get<Pinky>()
    val plugins = injector.get<Plugins>()
    val visualizer = injector.get<Visualizer>()
    val clock = injector.get<Clock>()

    private val brains: MutableList<Brain> = mutableListOf()


    fun start() = doRunBlocking {
        val simSurfaces = prepareSurfaces()

        GlobalScope.launch {
            pinky.startAndRun()
        }

        val launcher = Launcher(document.getElementById("launcher")!!)
        launcher.add("Web UI") { createWebClientApp() }
        launcher.add("Mapper") { createMapperApp() }
        launcher.add("Admin UI") { createAdminUiApp() }

        simSurfaces.forEach { simSurface ->
            val vizPanel = visualizer.addSurface(simSurface.surfaceGeometry)
            vizPanel.vizPixels = VizPixels(vizPanel, simSurface.pixelPositions)

            val brain = Brain(simSurface.brainId.uuid, network, vizPanel.vizPixels ?: NullPixels, clock)
            brains.add(brain)

            brainScope.launch { randomDelay(1000); brain.run() }
        }
        pinky.pixelCount = simSurfaces.sumBy { it.pixelPositions.size }

        val dmxUniverse = injector.get<FakeDmxUniverse>()
        model.movingHeads.forEach { movingHead ->
            visualizer.addMovingHead(movingHead, dmxUniverse)
        }

//        val users = storage.users.transaction { store -> store.getAll() }
//        println("users = ${users}")

        facade.notifyChanged()

        doRunBlocking {
            delay(200000L)
        }
    }

    fun createWebClientApp(): WebClient {
        return injector.createScope<WebClient>().get()
    }

    fun createMapperApp(): JsMapperUi {
        return injector.createScope<MapperUi>().get()
    }

    fun createAdminUiApp() = AdminUi(network, pinky.address, model, clock)

    private fun prepareSurfaces(): List<SimSurface> {
        val pixelDensity = queryParams.getOrElse("pixelDensity") { "0.2" }.toFloat()
        val pixelSpacing = queryParams.getOrElse("pixelSpacing") { "3" }.toFloat()
        val pixelArranger = SwirlyPixelArranger(pixelDensity, pixelSpacing)
        var totalPixels = 0

        val simSurfaces = model.allSurfaces.sortedBy(Model.Surface::name).mapIndexed { index, surface ->
            //            if (panel.name != "17L") return@forEachIndexed

            val surfaceGeometry = SurfaceGeometry(surface)
            val pixelPositions = pixelArranger.arrangePixels(surfaceGeometry, surface.expectedPixelCount)
            totalPixels += pixelPositions.size
            SimSurface(surface, surfaceGeometry, pixelPositions, BrainId("brain//$index"))
        }

        doRunBlocking {
            val mapperFs = injector.get<Fs>(named(JsSimulatorModule.Qualifier.MapperFs)) as FakeFs
            val fs = injector.get<Fs>()

            val mappingSessionPath = Storage(mapperFs, plugins).saveSession(
                MappingSession(clock.now(), simSurfaces.map { simSurface ->
                    MappingSession.SurfaceData(
                        simSurface.brainId.uuid, simSurface.surface.name,
                        simSurface.pixelPositions.map {
                            PixelData(Vector3F(it.x.toFloat(), it.y.toFloat(), it.z.toFloat()), null, null)
                        }, null, null, null
                    )
                }, Matrix4(doubleArrayOf()), null, notes = "Simulated pixels")
            )
            mapperFs.renameFile(
                mappingSessionPath,
                fs.resolve("mapping", model.name, "simulated", mappingSessionPath.name))
        }
        return simSurfaces
    }

    private suspend fun cleanUpBrowserStorage() {
        val fs = injector.get<Fs>()

        // [2021-03-13] Delete old 2019-era show files.
        fs.resolve("shaders").listFiles().forEach { file ->
            file.delete()
        }
    }

    class SimSurface(
        val surface: Model.Surface,
        val surfaceGeometry: SurfaceGeometry,
        val pixelPositions: Array<Vector3>,
        val brainId: BrainId
    )

    object NullPixels : Pixels {
        override val size = 0

        override fun get(i: Int): Color = Color.BLACK
        override fun set(i: Int, color: Color) {}
        override fun set(colors: Array<Color>) {}
    }

    private val brainScope = CoroutineScope(Dispatchers.Main)

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