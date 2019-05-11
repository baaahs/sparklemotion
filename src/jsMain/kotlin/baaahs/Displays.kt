package baaahs

import baaahs.net.Network
import org.w3c.dom.Document
import org.w3c.dom.Element
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.appendElement
import kotlin.dom.appendText
import kotlin.dom.clear

class JsDisplay : Display {
    override fun forNetwork(): NetworkDisplay = JsNetworkDisplay(document)

    override fun forPinky(): PinkyDisplay =
        JsPinkyDisplay(document.getElementById("pinkyView")!!)

    override fun forBrain(): BrainDisplay =
        JsBrainDisplay(document.getElementById("brainsView")!!)
}

class JsNetworkDisplay(document: Document) : NetworkDisplay {
    private val packetLossRateSpan = document.getElementById("networkPacketLossRate")!!.apply {
        addEventListener("click", {
            packetLossRate = kotlin.browser.window.prompt(
                "Packet loss rate (%):", "${(packetLossRate * 100).toInt()}"
            )!!.toFloat() / 100
        })
    }

    override var packetLossRate: Float = 0.05f
        set(value) {
            packetLossRateSpan.textContent = "${(value * 100).toInt()}%"
            field = value
        }

    init {
        packetLossRate = 0.05f
    }


    private val packetsReceivedSpan = document.getElementById("networkPacketsReceived")!!
    private val packetsDroppedSpan = document.getElementById("networkPacketsDropped")!!

    private var packetsReceived = 0
    private var packetsDropped = 0

    override fun receivedPacket() {
        packetsReceivedSpan.textContent = packetsReceived++.toString()
    }

    override fun droppedPacket() {
        packetsDroppedSpan.textContent = packetsDropped++.toString()
    }
}

class JsPinkyDisplay(element: Element) : PinkyDisplay {
    override var selectedShow: Show.MetaData? = null

    private val brainCountDiv: Element
    private val beat1: Element
    private val beat2: Element
    private val beat3: Element
    private val beat4: Element
    private val beats: List<Element>
    private val showListDiv: Element
    private val showButtons: MutableList<ShowButton>

    init {
        element.appendText("Brains online: ")
        brainCountDiv = element.appendElement("span") {}

        val beatsDiv = element.appendElement("div") {
            id = "beatsDiv"
            appendElement("b") { appendText("Beats: ") }
            appendElement("br") {}
        }
        beat1 = beatsDiv.appendElement("span") { appendText("1") }
        beat2 = beatsDiv.appendElement("span") { appendText("2") }
        beat3 = beatsDiv.appendElement("span") { appendText("3") }
        beat4 = beatsDiv.appendElement("span") { appendText("4") }
        beats = listOf(beat1, beat2, beat3, beat4)

        showListDiv = element.appendElement("div") { className = "showsDiv" }
        showButtons = mutableListOf()
    }

    override fun listShows(showMetas: List<Show.MetaData>) {
        showListDiv.clear()
        showListDiv.appendElement("b") { appendText("Shows: ") }
        showListDiv.appendElement("br") {}

        showButtons.clear()
        showMetas.forEach { showMeta ->
            val element = showListDiv.appendElement("span") { appendText(showMeta.name) }
            val showButton = ShowButton(showMeta, element)
            showButton.onSelect = { this.selectedShow = it }
            showButton.allButtons = showButtons
            showButtons.add(showButton)
        }
//        showButtons[0].select()
    }

    override var brainCount: Int = 0
        set(value) {
            brainCountDiv.clear()
            brainCountDiv.appendText(value.toString())
            field = value
        }

    override var beat: Int = 0
        set (value) {
            beats[field].classList.clear()
            beats[value].classList.add("selected")

            field = value
        }

    private class ShowButton(showMeta: Show.MetaData, element: Element) : Button<Show.MetaData>(showMeta, element)
}

class JsBrainDisplay(element: Element) : BrainDisplay {
    private var myDiv = element.appendElement("div") { addClass("brain-offline") }

    override fun haveLink(link: Network.Link) {
        clearClasses()
        myDiv.classList.add("brain-link")
    }

    private fun clearClasses() {
        myDiv.classList.clear()
    }

}
