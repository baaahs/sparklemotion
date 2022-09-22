package baaahs

import baaahs.app.ui.PatchEditorApp
import baaahs.client.WebClient
import baaahs.di.*
import baaahs.monitor.MonitorUi
import baaahs.net.BrowserNetwork
import baaahs.scene.SceneMonitor
import baaahs.sim.FakeNetwork
import baaahs.sim.HostedWebApp
import baaahs.sim.SimMappingManager
import baaahs.sim.ui.SimulatorAppProps
import baaahs.sim.ui.SimulatorAppView
import baaahs.sim.ui.WebClientWindowView
import baaahs.sm.brain.proto.Ports
import baaahs.ui.ErrorDisplay
import baaahs.util.*
import baaahs.util.JsPlatform.decodeQueryParams
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlinx.js.jso
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.w3c.dom.get
import react.createElement
import react.dom.client.createRoot
import three_ext.installCameraControls

val _init_ =
    document.getElementById("_patience_")?.let { patience ->
        patience.innerHTML = arrayOf(
            "Reticulating Splines…",
            "Grooming Llamas…"
        ).random()
    }

fun main(args: Array<String>) {
    window.asDynamic().LoggerConfig = LoggerConfig

    @Suppress("ConstantConditionIf", "SimplifyBooleanWithConstants")
    ConsoleFormatters.install()
    installCameraControls()

    val mode = document["sparklemotionMode"] as? String ?: "test"
    println("args = $args, mode = $mode")

    val queryParams = decodeQueryParams(document.location!!)

    globalLaunch {
        when (mode) {
            "Simulator" -> launchSimulator(queryParams)
            else -> launchUi(mode)
        }
    }
}

private fun launchUi(appName: String?) {
    tryCatchAndShowErrors {
        val pinkyAddress = JsPlatform.myAddress
        val network = BrowserNetwork(pinkyAddress, Ports.PINKY)

        val webAppInjector = koinApplication {
            logger(KoinLogger())

            modules(
                PluginsModule(Pluggables.plugins).getModule(),
                JsPlatformModule(network).getModule(),
                JsStandaloneWebClientModule(pinkyAddress).getModule()
            )
        }
        val koin = webAppInjector.koin

        val app = when (appName) {
            "Monitor" -> {
                koin.loadModules(listOf(JsMonitorWebClientModule().getModule()))
                koin.createScope<MonitorUi>().get<MonitorUi>()
            }

            "UI" -> {
                koin.loadModules(listOf(JsUiWebClientModule().getModule()))
                koin.createScope<WebClient>().get<WebClient>()
            }

            "PatchEditor" -> {
                koin.loadModules(listOf(JsUiWebClientModule().getModule()))
                koin.createScope<WebClient>().get<PatchEditorApp>()
            }

            else -> throw UnsupportedOperationException("unknown mode $appName")
        }

        app.launchApp()
    }
}

private fun launchSimulator(
    queryParams: Map<String, String>
) {
    val pixelDensity = queryParams.getOrElse("pixelDensity") { "0.2" }.toFloat()
    val pixelSpacing = queryParams.getOrElse("pixelSpacing") { "3" }.toFloat()

    val pinkyAddress = JsPlatform.myAddress
    val fakeNetwork = FakeNetwork()
    val bridgeNetwork = BrowserNetwork(pinkyAddress, Ports.PINKY)
    val pinkySettings = PinkySettings()
    val sceneMonitor = SceneMonitor()
    val simMappingManager = SimMappingManager()

    val injector = koinApplication {
        logger(KoinLogger())

        modules(
            PluginsModule(Pluggables.plugins).getModule(),
            JsSimPlatformModule(fakeNetwork).getModule(),
            JsSimulatorModule(
                sceneMonitor, fakeNetwork, bridgeNetwork,
                pinkyAddress, pixelDensity, pixelSpacing, simMappingManager
            ).getModule(),
            JsSimPinkyModule(sceneMonitor, pinkySettings, Dispatchers.Main, simMappingManager).getModule(),
            JsUiWebClientModule().getModule(),
            JsMonitorWebClientModule().getModule(),
        )
    }.koin

    val simulator = injector.get<SheepSimulator>(parameters = { parametersOf(injector) })

    val hostedWebApp = when (val app = queryParams["app"] ?: "UI") {
        "Monitor" -> simulator.createMonitorApp()
        "UI" -> simulator.createWebClientApp()
        else -> throw UnsupportedOperationException("unknown app $app")
    }
    hostedWebApp.onLaunch()

    val props = jso<SimulatorAppProps> {
        this.simulator = simulator.facade
        this.hostedWebApp = hostedWebApp
    }
    val simulatorEl = document.getElementById("app")!!

    GlobalScope.promise {
        createRoot(simulatorEl)
            .render(createElement(SimulatorAppView, props))

        simulator.start()
    }.catch {
        window.alert("Failed to launch simulator: $it")
        Logger("JsMain").error(it) { "Failed to launch simulator." }
        throw it
    }
}

private fun HostedWebApp.launchApp() {
    globalLaunch {
        onLaunch()

        val contentDiv = document.getElementById("content")!!
        createRoot(contentDiv)
            .render(createElement(WebClientWindowView, jso {
                this.hostedWebApp = this@launchApp
            }))
    }
}

private fun tryCatchAndShowErrors(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        val container = document.getElementById("content")
            ?: document.getElementById("app")!!
        createRoot(container)
            .render(
                createElement(ErrorDisplay, jso {
                    this.error = e.asDynamic()
                    this.componentStack = e.stackTraceToString().let {
                        if (it.contains("@webpack-internal:")) {
                            it.replace(Regex("webpack-internal:///.*/"), "")
                                .replace(".prototype.", "#")
                                .replace(Regex("([A-Z][A-Za-z]+)\$")) { it.groupValues.first() }
                                .replace("@", " @ ")
                        } else it
                    }
                    this.resetErrorBoundary = { window.location.reload() }

                })
            )
        throw e
    }
}
