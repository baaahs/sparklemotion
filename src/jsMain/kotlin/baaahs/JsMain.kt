package baaahs

import baaahs.browser.RealMediaDevices
import baaahs.jsx.MosiacUI
import baaahs.net.BrowserNetwork
import baaahs.net.BrowserNetwork.BrowserAddress
import kotlinext.js.jsObject
import org.w3c.dom.get
import react.createElement
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    val mode = document["sparklemotionMode"] ?: "test"
    println("args = $args, mode = $mode")

    val pinkyAddress = BrowserAddress(websocketsUrl())
    val network = BrowserNetwork(pinkyAddress, baaahs.proto.Ports.PINKY)
    val contentDiv = document.getElementById("content")

    when (mode) {
        "Simulator" -> {
            // Run the simulator from within the new MosiacUI
            val props = jsObject<MosiacUI.Props> {
            }
            val simulatorEl = document.getElementById("app")
            render(createElement(MosiacUI::class.js, props), simulatorEl)
            SheepSimulator().start()
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
