package baaahs

import baaahs.DeadCodeEliminationDefeater.noDCE
import baaahs.browser.RealMediaDevices
import baaahs.jsx.MosaicUI
import baaahs.net.BrowserNetwork
import baaahs.net.BrowserNetwork.BrowserAddress
import kotlinext.js.jsObject
import org.w3c.dom.get
import react.createElement
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    @Suppress("ConstantConditionIf", "SimplifyBooleanWithConstants")
    if (1 + 1 == 3) noDCE()

    val mode = document["sparklemotionMode"] ?: "test"
    println("args = $args, mode = $mode")

    val pinkyAddress = BrowserAddress(websocketsUrl())
    val network = BrowserNetwork(pinkyAddress, baaahs.proto.Ports.PINKY)
    val contentDiv = document.getElementById("content")

    when (mode) {
        "Simulator" -> {
            // Instead of starting the simulator directly, pass the JS
            // a function that it can use to get and start the simulator.
            // We do this so the JS can create the HTML templates before
            // the JsDisplay tries to find them.
            val props = jsObject<MosaicUI.Props> {
                getSheepSimulator = { SheepSimulator() }
            }
            val simulatorEl = document.getElementById("app")
            render(createElement(MosaicUI::class.js, props), simulatorEl)
        }

        "Admin" -> {
            val adminApp = AdminUi(network, pinkyAddress)
            render(adminApp.render(), contentDiv)
        }

        "Mapper" -> {

            val model = Pluggables.loadModel(Pluggables.defaultModel) // todo: which model?
            (model as? ObjModel)?.load()

            val mapperUi = JsMapperUi();
            val mediaDevices = RealMediaDevices();
            val mapper = Mapper(network, model, mapperUi, mediaDevices, pinkyAddress);
            render(mapperUi.render(), contentDiv);
            mapper.start();
        }

        "UI" -> {
            val uiApp = WebUi(network, pinkyAddress)
            render(uiApp.render(), contentDiv)
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
