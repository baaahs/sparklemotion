package baaahs

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.w3c.dom.DOMTokenList
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.ItemArrayLike
import org.w3c.xhr.XMLHttpRequest
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.appendElement
import kotlin.dom.appendText
import kotlin.dom.clear

actual fun doRunBlocking(block: suspend () -> Unit): dynamic = GlobalScope.promise { block() }

actual fun getResource(name: String): String {
    val xhr = XMLHttpRequest()
    xhr.open("GET", name, false)
    xhr.send()

    if (xhr.status.equals(200)) {
        return xhr.responseText
    }

    throw Exception("failed to load resource ${name}: ${xhr.status} ${xhr.responseText}")
}

actual fun getDisplay(): Display = JsDisplay()

class JsDisplay : Display {
    override fun forNetwork(): NetworkDisplay = JsNetworkDisplay(document)

    override fun forCentral(): CentralDisplay =
        JsCentralDisplay(document.getElementById("centralView")!!)

    override fun forController(): ControllerDisplay =
        JsControllerDisplay(document.getElementById("controllersView")!!)

    override fun forMapper(): MapperDisplay =
        JsMapperDisplay(document.getElementById("mapperView")!!)
}

class JsNetworkDisplay(document: Document) : NetworkDisplay {
    private val packetsSentSpan = document.getElementById("networkPacketsSent")!!
    private val packetsDroppedSpan = document.getElementById("networkPacketsDropped")!!

    private var packetsSent = 0
    private var packetsDropped = 0

    override fun sentPacket() {
        packetsSentSpan.clear()
        packetsSentSpan.appendText(packetsSent++.toString())
    }

    override fun droppedPacket() {
        packetsDroppedSpan.clear()
        packetsDroppedSpan.appendText(packetsDropped++.toString())
    }
}

class JsCentralDisplay(element: Element) : CentralDisplay {
    private val consoleDiv: Element
    private var controllerCountDiv: Element

    init {
        element.appendElement("div") {
            appendText("Central")
        }

        element.appendText("Controllers online: ")
        controllerCountDiv = element.appendElement("span") {}
        consoleDiv = element.appendElement("div") {}
    }

    override var controllerCount: Int = 0
        set(value) {
            controllerCountDiv.clear()
            controllerCountDiv.appendText(value.toString())
            field = value
        }
}

class JsControllerDisplay(element: Element) : ControllerDisplay {
    private var myDiv: Element

    init {
        myDiv = element.appendElement("div") { addClass("controller-offline") }
    }

    override fun haveLink(link: Network.Link) {
        clearClasses()
        myDiv.classList.add("controller-link")
    }

    private fun clearClasses() {
        myDiv.classList.clear()
    }

}

class JsMapperDisplay(val element: Element) : MapperDisplay

private fun DOMTokenList.clear() {
    while (length > 0) {
        remove(item(0)!!)
    }
}

fun <T> ItemArrayLike<T>.forEach(action: (T) -> Unit) {
    for (i in 0..length) {
        action(item(i)!!)
    }
}