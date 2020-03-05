package baaahs

import baaahs.browser.RealMediaDevices
import baaahs.net.BrowserNetwork
import baaahs.net.BrowserNetwork.BrowserAddress
import org.w3c.dom.get
import react.dom.render
import kotlin.browser.document
import kotlin.browser.window

fun main(args: Array<String>) {
    val mode = document["sparklemotionMode"] ?: "test"
    println("args = $args, mode = $mode")

    val pinkyAddress = BrowserAddress(websocketsUrl())
    val network = BrowserNetwork(pinkyAddress, baaahs.proto.Ports.PINKY)

    when (mode) {
        "Simulator" -> SheepSimulator().start()
        "Mapper" -> {

            val model = Pluggables.loadModel(Pluggables.defaultModel) // todo: which model?
            (model as? ObjModel)?.load()

            val mapperUi = JsMapperUi();
            val mediaDevices = RealMediaDevices();
            val mapper = Mapper(network, model, mapperUi, mediaDevices, pinkyAddress);
            render(mapperUi.render(), document.body);
            mapper.start();
        }

        "UI" -> {
            val uiApp = WebUi(network, pinkyAddress)
            render(uiApp.render(), document.body)
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
