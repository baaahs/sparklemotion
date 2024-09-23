package baaahs

import baaahs.app.settings.UiSettings
import baaahs.client.WebClient
import baaahs.controller.ControllersManager
import baaahs.midi.MIDIUi
import baaahs.monitor.MonitorUi
import baaahs.plugin.SimulatorPlugins
import baaahs.sim.*
import baaahs.sim.ui.LaunchItem
import baaahs.util.coroutineExceptionHandler
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf

class SheepSimulator(
    private val network: FakeNetwork,
    private val visualizer: Visualizer,
    @Suppress("unused") val localFs: BrowserSandboxFs, // For JS console access.
    private val getKoin: () -> Koin
) {
    val facade = Facade()

    private lateinit var pinky: Pinky
    private lateinit var fixturesSimulator: FixturesSimulator

    init {
        window.asDynamic().simulator = this
    }

    suspend fun start() = coroutineScope {
        val pinkyScope = getKoin().createScope<Pinky>()
        launch(coroutineExceptionHandler) { cleanUpBrowserStorage() }

        pinky = pinkyScope.get()
        val controllersManager = pinkyScope.get<ControllersManager>()
        fixturesSimulator = pinkyScope.get(parameters = { parametersOf(controllersManager) })

        launch(coroutineExceptionHandler) { pinky.startAndRun() }

        pinky.awaitMappingResultsLoaded() // Otherwise controllers might report in before they can be mapped.
        facade.notifyChanged()
    }

    fun createWebClientApp(): WebClient = getKoin().createScope<WebClient>().get()
    fun createMonitorApp(): MonitorUi = getKoin().createScope<MonitorUi>().get()
    fun createMIDIApp(): MIDIUi = getKoin().createScope<MIDIUi>().get()

    private suspend fun cleanUpBrowserStorage() {
        val fs = BrowserSandboxFs("BrowserSandboxFs")

        // [2021-03-13] Delete old 2019-era show files.
        fs.resolve("shaders").listFiles().forEach { file ->
            file.delete()
        }

        // [2022-02-10] Move show and scene files in / to /data.
        fs.resolve().listFiles().forEach { file ->
            if (
                file.name.endsWith(".sparkle")
                || file.name.endsWith(".scene")
                || file.name == "config.json"
            ) {
                file.renameTo(file.parent!!.resolve("data").resolve(file.name))
            }
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
                launchItem("Monitor") { createMonitorApp() },
                launchItem("MIDIUi") { createMIDIApp() }
            )
        val uiSettings: UiSettings
            get() = getKoin().get()
    }
}