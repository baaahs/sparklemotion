package baaahs

import baaahs.client.WebClient
import baaahs.di.*
import baaahs.io.Fs
import baaahs.mapper.JsMapperUi
import baaahs.mapper.MapperUi
import baaahs.model.Model
import baaahs.monitor.MonitorUi
import baaahs.sim.FakeNetwork
import baaahs.sim.FixturesSimulator
import baaahs.sim.Launcher
import baaahs.util.KoinLogger
import baaahs.util.LoggerConfig
import baaahs.visualizer.Visualizer
import decodeQueryParams
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.dsl.koinApplication

class SheepSimulator(val model: Model) {
    @Suppress("unused")
    val facade = Facade()

    private val queryParams = decodeQueryParams(document.location!!)
    val network = FakeNetwork()

    init {
        window.asDynamic().simulator = this
        window.asDynamic().LoggerConfig = LoggerConfig
//  TODO      GlslBase.plugins.add(SoundAnalysisPlugin(bridgeClient.soundAnalyzer))
    }

    val pixelDensity = queryParams.getOrElse("pixelDensity") { "0.2" }.toFloat()
    val pixelSpacing = queryParams.getOrElse("pixelSpacing") { "3" }.toFloat()

    val pinkyLink = network.link("pinky")
    val pinkySettings = PinkySettings()

    val injector = koinApplication {
        logger(KoinLogger())

        modules(
            JsSimPlatformModule(network, model).getModule(),
            JsSimulatorModule(window.location.hostname, pixelDensity, pixelSpacing).getModule(),
            JsSimPinkyModule(pinkyLink, pinkySettings).getModule(),
            JsWebClientModule(pinkyLink.myAddress).getModule(),
            JsAdminClientModule(pinkyLink.myAddress).getModule(),
            JsSimBeatLinkPluginModule().getModule()
        )
    }.koin

    private val pinkyScope = injector.createScope<Pinky>()
    private val pinky = pinkyScope.get<Pinky>()
    private val visualizer = injector.get<Visualizer>()
    private val fixturesSimulator = injector.get<FixturesSimulator>()

    init {
        GlobalScope.launch { cleanUpBrowserStorage() }
    }

    suspend fun start() {
        fixturesSimulator.generateMappingData()

        GlobalScope.launch {
            pinky.startAndRun()
        }

        val launcher = Launcher(document.getElementById("launcher")!!)
        launcher.add("Web UI") { createWebClientApp() }
        launcher.add("Mapper") { createMapperApp() }
        launcher.add("Monitor") { createMonitorApp() }

        pinky.awaitMappingResultsLoaded() // Otherwise controllers might report in before they can be mapped.
        fixturesSimulator.launchControllers()
        fixturesSimulator.addToVisualizer()

//        val users = storage.users.transaction { store -> store.getAll() }
//        println("users = ${users}")

        facade.notifyChanged()

        delay(200000L)
    }

    fun createWebClientApp(): WebClient = injector.createScope<WebClient>().get()
    fun createMapperApp(): JsMapperUi = injector.createScope<MapperUi>().get()
    fun createMonitorApp(): MonitorUi = injector.createScope<MonitorUi>().get()

    private suspend fun cleanUpBrowserStorage() {
        val fs = pinkyScope.get<Fs>()

        // [2021-03-13] Delete old 2019-era show files.
        fs.resolve("shaders").listFiles().forEach { file ->
            file.delete()
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val pinky: Pinky.Facade
            get() = this@SheepSimulator.pinky.facade
        val network: FakeNetwork.Facade
            get() = this@SheepSimulator.network.facade
        val visualizer: Visualizer.Facade
            get() = this@SheepSimulator.visualizer.facade
        val fixturesSimulator: FixturesSimulator.Facade
            get() = this@SheepSimulator.fixturesSimulator.facade
    }
}