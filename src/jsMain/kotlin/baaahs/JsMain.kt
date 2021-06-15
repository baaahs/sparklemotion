package baaahs

import baaahs.DeadCodeEliminationDefeater.noDCE
import baaahs.client.WebClient
import baaahs.di.JsBeatLinkPluginModule
import baaahs.di.JsMapperClientModule
import baaahs.di.JsPlatformModule
import baaahs.di.JsWebClientModule
import baaahs.jsx.sim.MosaicApp
import baaahs.model.Model
import baaahs.model.ObjModel
import baaahs.net.BrowserNetwork
import baaahs.net.BrowserNetwork.BrowserAddress
import baaahs.plugin.beatlink.BeatSource
import baaahs.proto.Ports
import baaahs.sim.HostedWebApp
import baaahs.sim.ui.WebClientWindow
import baaahs.sim.ui.WebClientWindowProps
import baaahs.ui.ErrorDisplay
import baaahs.ui.ErrorDisplayProps
import baaahs.util.ConsoleFormatters
import baaahs.util.KoinLogger
import decodeQueryParams
import kotlinext.js.jsObject
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.dsl.koinApplication
import org.w3c.dom.get
import react.createElement
import react.dom.render
import three_ext.installCameraControls

fun main(args: Array<String>) {
    @Suppress("ConstantConditionIf", "SimplifyBooleanWithConstants")
    if (1 + 1 == 3) noDCE()
    ConsoleFormatters.install()
    installCameraControls()

    val mode = document["sparklemotionMode"] ?: "test"
    println("args = $args, mode = $mode")

    val pinkyAddress = BrowserAddress(websocketsUrl())
    val network = BrowserNetwork(pinkyAddress, Ports.PINKY)

    val queryParams = decodeQueryParams(document.location!!)
    val model = Pluggables.loadModel(queryParams["model"] ?: Pluggables.defaultModel)


    GlobalScope.launch {
        launchUi(mode, model, queryParams, network, pinkyAddress)
    }
}

private fun launchUi(
    mode: String?,
    model: Model,
    queryParams: Map<String, String>,
    network: BrowserNetwork,
    pinkyAddress: BrowserAddress
) {
    fun HostedWebApp.launch() {
        GlobalScope.launch {
            onLaunch()

            val contentDiv = document.getElementById("content")
            render(createElement(WebClientWindow, jsObject<WebClientWindowProps> {
                this.hostedWebApp = this@launch
            }), contentDiv)
        }
    }


    try {
        if (mode == "Simulator") {
            val simulator = SheepSimulator(model)

            val hostedWebApp = when (val app = queryParams["app"] ?: "UI") {
                "Admin" -> simulator.createAdminUiApp()
                "Mapper" -> simulator.createMapperApp()
                "UI" -> simulator.createWebClientApp()
                else -> throw UnsupportedOperationException("unknown app $app")
            }
            hostedWebApp.onLaunch()

            val props = jsObject<MosaicApp.Props> {
                this.simulator = simulator
                this.hostedWebApp = hostedWebApp
            }
            val simulatorEl = document.getElementById("app")

            GlobalScope.launch {
                render(createElement(MosaicApp::class.js, props), simulatorEl)
            }
        } else {
            val webAppInjector = koinApplication {
                logger(KoinLogger())

                modules(
                    JsPlatformModule(network, model).getModule(),
                    JsBeatLinkPluginModule(BeatSource.None).getModule(),
    //            JsSoundAnalysisPluginModule(args).getModule()
                )
            }
            val koin = webAppInjector.koin

            when (mode) {
                "Admin" -> {
                    koin.loadModules(listOf(JsMapperClientModule(pinkyAddress).getModule()))
//                    val adminApp = AdminUi(koin.get(), pinkyAddress, model)
                    val adminApp = koin.createScope<AdminUi>().get<AdminUi>()
                    adminApp.launch()
                }

                "Mapper" -> {
                    koin.loadModules(listOf(JsMapperClientModule(pinkyAddress).getModule()))

                    (model as? ObjModel)?.load()

//                    val mapperUi = JsMapperUi();
//                    val mediaDevices = RealMediaDevices()
//                    // Yuck, side effects.
//                    Mapper(koin.get(), model, mapperUi, mediaDevices, pinkyAddress, JsClock)

                    val mapperUi = koin.createScope<MapperUi>().get<JsMapperUi>()
                    mapperUi.launch()
                }

                "UI" -> {
                    koin.loadModules(listOf(JsWebClientModule(pinkyAddress).getModule()))

                    val uiApp = koin.createScope<WebClient>().get<WebClient>()
                    uiApp.launch()
                }

                "test" -> {
                }

                else -> throw UnsupportedOperationException("unknown mode $mode")
            }
        }
    } catch (e: Exception) {
        val container = document.getElementById("content") ?: document.getElementById("app")
        render(createElement(ErrorDisplay, jsObject<ErrorDisplayProps> {
            this.error = e.asDynamic()
            this.componentStack = e.stackTraceToString()
            this.resetErrorBoundary = { window.location.reload() }

        }), container)
        throw e
    }
}

private fun websocketsUrl(): String {
    val l = window.location
    val proto = if (l.protocol === "https:") "wss:" else "ws:"
    return "$proto//${l.host}/"
}
