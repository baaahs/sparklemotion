package baaahs

import baaahs.mapper.MapperAppView
import baaahs.mapper.MapperAppViewProps
import baaahs.DeadCodeEliminationDefeater.noDCE
import baaahs.browser.RealMediaDevices
import baaahs.client.WebClient
import baaahs.jsx.sim.MosaicApp
import baaahs.model.ObjModel
import baaahs.net.BrowserNetwork
import baaahs.net.BrowserNetwork.BrowserAddress
import baaahs.sim.HostedWebApp
import baaahs.sim.ui.WebClientWindow
import baaahs.sim.ui.WebClientWindowProps
import baaahs.util.ConsoleFormatters
import baaahs.util.JsClock
import decodeQueryParams
import kotlinext.js.jsObject
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
    val network = BrowserNetwork(pinkyAddress, baaahs.proto.Ports.PINKY)
    val contentDiv = document.getElementById("content")

    val queryParams = decodeQueryParams(document.location!!)
    val model = Pluggables.loadModel(queryParams["model"] ?: Pluggables.defaultModel)

    fun HostedWebApp.launch() {
        onLaunch()
        render(createElement(WebClientWindow, jsObject<WebClientWindowProps> {
            this.hostedWebApp = this@launch
        }), contentDiv)
    }

    when (mode) {
        "Simulator" -> {
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
            render(createElement(MosaicApp::class.js, props), simulatorEl)
        }

        "Admin" -> {
            val adminApp = AdminUi(network, pinkyAddress, model)
            adminApp.launch()
        }

        "Mapper" -> {
            (model as? ObjModel)?.load()

            val mapperUi = JsMapperUi();
            val mediaDevices = RealMediaDevices()
            // Yuck, side effects.
            Mapper(network, model, mapperUi, mediaDevices, pinkyAddress, JsClock)

            mapperUi.launch()
        }

        "UI" -> {
            val uiApp = WebClient(network, pinkyAddress)
            uiApp.launch()
        }

        "test" -> {}

        else -> throw UnsupportedOperationException("unknown mode $mode")
    }
}

private fun websocketsUrl(): String {
    val l = window.location
    val proto = if (l.protocol === "https:") "wss:" else "ws:"
    return "$proto//${l.host}/"
}
