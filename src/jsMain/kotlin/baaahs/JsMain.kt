package baaahs

import baaahs.client.WebClient
import baaahs.di.*
import baaahs.mapper.JsMapperUi
import baaahs.mapper.MapperUi
import baaahs.model.Model
import baaahs.model.ObjModel
import baaahs.monitor.MonitorUi
import baaahs.net.BrowserNetwork
import baaahs.net.BrowserNetwork.BrowserAddress
import baaahs.proto.Ports
import baaahs.sim.HostedWebApp
import baaahs.sim.ui.SimulatorAppProps
import baaahs.sim.ui.SimulatorAppView
import baaahs.sim.ui.WebClientWindowView
import baaahs.ui.ErrorDisplay
import baaahs.util.ConsoleFormatters
import baaahs.util.KoinLogger
import baaahs.util.Logger
import decodeQueryParams
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.promise
import org.koin.core.parameter.parametersOf
import org.koin.dsl.koinApplication
import org.w3c.dom.get
import react.createElement
import react.dom.render
import three_ext.installCameraControls

fun main(args: Array<String>) {
    @Suppress("ConstantConditionIf", "SimplifyBooleanWithConstants")
    ConsoleFormatters.install()
    installCameraControls()

    val mode = document["sparklemotionMode"] as? String ?: "test"
    println("args = $args, mode = $mode")

    val queryParams = decodeQueryParams(document.location!!)
    val model = Pluggables.loadModel(queryParams["model"] ?: Pluggables.defaultModel)


    GlobalScope.launch {
        when (mode) {
            "Simulator" -> launchSimulator(model, queryParams)
            else -> launchUi(mode, model)
        }
    }
}

private fun launchUi(appName: String?, model: Model) {
    tryCatchAndShowErrors {
        val pinkyAddress = myBrowserAddress()
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
            "Mapper" -> {
                koin.loadModules(listOf(JsAdminWebClientModule(model).getModule()))
                (model as? ObjModel)?.load()
                koin.createScope<MapperUi>().get<JsMapperUi>()
            }

            "Monitor" -> {
                koin.loadModules(listOf(JsAdminWebClientModule(model).getModule()))
                koin.createScope<MonitorUi>().get<MonitorUi>()
            }

            "UI" -> {
                koin.loadModules(listOf(JsUiWebClientModule(model).getModule()))
                koin.createScope<WebClient>().get<WebClient>()
            }

            else -> throw UnsupportedOperationException("unknown mode $appName")
        }

        app.launchApp()
    }
}

private fun launchSimulator(
    model: Model,
    queryParams: Map<String, String>
) {
    val pixelDensity = queryParams.getOrElse("pixelDensity") { "0.2" }.toFloat()
    val pixelSpacing = queryParams.getOrElse("pixelSpacing") { "3" }.toFloat()

    val pinkyAddress = myBrowserAddress()
    val network = BrowserNetwork(pinkyAddress, Ports.PINKY)
    val pinkySettings = PinkySettings()

    val injector = koinApplication {
        logger(KoinLogger())

        modules(
            PluginsModule(Pluggables.plugins).getModule(),
            JsSimPlatformModule().getModule(),
            JsSimulatorModule(model, network, pinkyAddress, pixelDensity, pixelSpacing).getModule(),
            JsSimPinkyModule(model, pinkySettings).getModule(),
            JsUiWebClientModule(model).getModule(),
            JsAdminWebClientModule(model).getModule(),
        )
    }.koin

    val simulator = injector.get<SheepSimulator>(parameters = { parametersOf(injector) })

    val hostedWebApp = when (val app = queryParams["app"] ?: "UI") {
        "Mapper" -> simulator.createMapperApp()
        "Monitor" -> simulator.createMonitorApp()
        "UI" -> simulator.createWebClientApp()
        else -> throw UnsupportedOperationException("unknown app $app")
    }
    hostedWebApp.onLaunch()

    val props = jsObject<SimulatorAppProps> {
        this.simulator = simulator.facade
        this.hostedWebApp = hostedWebApp
    }
    val simulatorEl = document.getElementById("app")

    GlobalScope.promise {
        render(createElement(SimulatorAppView, props), simulatorEl)

        simulator.start()
    }.catch {
        window.alert("Failed to launch simulator: $it")
        Logger("JsMain").error(it) { "Failed to launch simulator." }
        throw it
    }
}

private fun HostedWebApp.launchApp() {
    GlobalScope.launch {
        onLaunch()

        val contentDiv = document.getElementById("content")
        render(createElement(WebClientWindowView, jsObject {
            this.hostedWebApp = this@launchApp
        }), contentDiv)
    }
}

private fun tryCatchAndShowErrors(block: () -> Unit) {
    try {
        block()
    } catch (e: Exception) {
        val container = document.getElementById("content") ?: document.getElementById("app")
        render(createElement(ErrorDisplay, jsObject {
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

        }), container)
        throw e
    }
}

private fun myBrowserAddress(): BrowserAddress {
    return with(window.location) { BrowserAddress(protocol, hostname, port) }
}
