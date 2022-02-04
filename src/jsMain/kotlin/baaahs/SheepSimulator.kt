package baaahs

import baaahs.client.WebClient
import baaahs.io.Fs
import baaahs.mapper.JsMapperUi
import baaahs.mapper.MapperUi
import baaahs.monitor.MonitorUi
import baaahs.sim.FakeNetwork
import baaahs.sim.FixturesSimulator
import baaahs.sim.HostedWebApp
import baaahs.sim.Launcher
import baaahs.sim.ui.LaunchItem
import baaahs.util.LoggerConfig
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

class SheepSimulator(
    private val network: FakeNetwork,
    private val visualizer: Visualizer,
    private val getKoin: () -> Koin
) {
    val facade = Facade()

    private lateinit var pinky: Pinky
    private lateinit var fixturesSimulator: FixturesSimulator

    init {
        window.asDynamic().simulator = this
        window.asDynamic().LoggerConfig = LoggerConfig
    }

    suspend fun start() = coroutineScope {
        val pinkyScope = getKoin().createScope<Pinky>()
        launch { cleanUpBrowserStorage(pinkyScope.get<Fs>()) }

        pinky = pinkyScope.get()
        fixturesSimulator = pinkyScope.get(parameters = { parametersOf(pinky.plugins) })

        launch { pinky.startAndRun() }

        pinky.awaitMappingResultsLoaded() // Otherwise controllers might report in before they can be mapped.
        facade.notifyChanged()
    }

    fun createWebClientApp(): WebClient = getKoin().createScope<WebClient>().get()
    fun createMapperApp(): JsMapperUi = getKoin().createScope<MapperUi>().get()
    fun createMonitorApp(): MonitorUi = getKoin().createScope<MonitorUi>().get()

    private suspend fun cleanUpBrowserStorage(fs: Fs) {
        // [2021-03-13] Delete old 2019-era show files.
        fs.resolve("shaders").listFiles().forEach { file ->
            file.delete()
        }
    }

    private fun launchItem(title: String, block: () -> HostedWebApp) =
        LaunchItem(title) { Launcher.launch(title, block) }

    inner class Facade : baaahs.ui.Facade() {
        val pinky: Pinky.Facade
            get() = this@SheepSimulator.pinky.facade
        val network: FakeNetwork.Facade
            get() = this@SheepSimulator.network.facade
        val visualizer: Visualizer.Facade
            get() = this@SheepSimulator.visualizer.facade
        val fixturesSimulator: FixturesSimulator.Facade
            get() = this@SheepSimulator.fixturesSimulator.facade
        val launchItems: List<LaunchItem> =
            listOf(
                launchItem("Web UI") { createWebClientApp() },
                launchItem("Mapper") { createMapperApp() },
                launchItem("Monitor") { createMonitorApp() }
            )
    }
}