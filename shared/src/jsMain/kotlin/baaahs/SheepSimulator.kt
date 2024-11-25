package baaahs

import baaahs.app.settings.UiSettings
import baaahs.client.WebClient
import baaahs.controller.ControllersManager
import baaahs.monitor.MonitorUi
import baaahs.sim.*
import baaahs.sim.FixturesSimulator
import baaahs.sim.ui.LaunchItem
import baaahs.sm.brain.proto.Pixels
import baaahs.sm.brain.sim.BrainSimulatorManager
import baaahs.util.coroutineExceptionHandler
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.koin.core.Koin
import org.koin.core.parameter.parametersOf
import react.buildElement

class SheepSimulator(
    private val network: FakeNetwork,
    private val visualizer: Visualizer,
    @Suppress("unused") val localFs: BrowserSandboxFs, // For JS console access.
    private val getKoin: () -> Koin
) {
    val facade = Facade()

    private lateinit var pinky: Pinky
    private lateinit var fixturesSimulator: FixturesSimulator
    private lateinit var brainSimulatorManager: BrainSimulatorManager

    init {
        window.asDynamic().simulator = this
    }

    suspend fun start() = coroutineScope {
        val pinkyScope = getKoin().createScope<Pinky>()
        launch(coroutineExceptionHandler) { cleanUpBrowserStorage() }

        pinky = pinkyScope.get()
        val controllersManager = pinkyScope.get<ControllersManager>()
        fixturesSimulator = pinkyScope.get(parameters = { parametersOf(controllersManager) })
        brainSimulatorManager = pinkyScope.get<BrainSimulatorManager>()

        launch(coroutineExceptionHandler) { pinky.startAndRun() }

        pinky.awaitMappingResultsLoaded() // Otherwise controllers might report in before they can be mapped.
        facade.notifyChanged()
    }

    fun createWebClientApp(): WebClient = getKoin().createScope<WebClient>().get<WebClient>()
        .also {
            it.facade.additionalDrawerItems.add(
                buildElement {
                    showSimConsoleSwitch {
                        attrs.simulator = this@SheepSimulator.facade
                        attrs.mainWebApp = it
                    }
                }
            )
            it.facade.notifyChanged()
        }
    fun createMonitorApp(): MonitorUi = getKoin().createScope<MonitorUi>().get()

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

    fun newAnonymousBrain() {
        brainSimulatorManager.createBrain(null, Pixels.NullPixels)
            .start()
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
        val brainSimulatorManager: BrainSimulatorManager.Facade
            get() = this@SheepSimulator.brainSimulatorManager.facade

        val launchItems: List<LaunchItem> =
            listOf(
                launchItem("Web UI") { createWebClientApp() },
                launchItem("Monitor") { createMonitorApp() },
            )

        val uiSettings: UiSettings
            get() = getKoin().get()

        fun newBrain() =
            this@SheepSimulator.newAnonymousBrain()
    }
}