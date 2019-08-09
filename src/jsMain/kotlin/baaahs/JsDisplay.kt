package baaahs

import baaahs.net.Network
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Document
import org.w3c.dom.Element
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.get
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
        JsBrainDisplay(
            document.getElementById("brainsView")!!,
            document.getElementById("brainDetails")!!
        )

    override fun forVisualizer(): VisualizerDisplay = JsVisualizerDisplay()
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
//        packetLossRate = 0.05f
        packetLossRate = 0.0f
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
    override var onShowChange: (() -> Unit) = {}
    override var selectedShow: Show? = null
        set(value) {
            field = value
            val options = showListInput.options
            for (i in 0 until options.length) {
                if (options[i]?.textContent == value?.name) showListInput.selectedIndex = i
            }
        }

    override var showFrameMs: Int = 0
        set(value) {
            field = value
            showFramerate.textContent = "${1000 / value}fps"
            showElapsedMs.textContent = "${value}ms"
        }

    override var stats: Pinky.NetworkStats? = null
        set(value) {
            field = value
            statsSpan.textContent = value?.run { "$bytesSent bytes / $packetsSent packets sent" } ?: "?"
        }

    private val brainCountDiv: Element
    private val beat1: Element
    private val beat2: Element
    private val beat3: Element
    private val beat4: Element
    private val beats: List<Element>
    private var showList = emptyList<Show>()
    private val showListInput: HTMLSelectElement
    private var showFramerate: Element = document.getElementById("showFramerate")!!
    private var showElapsedMs: Element = document.getElementById("showElapsedMs")!!
    private var statsSpan: Element

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

        element.appendElement("b") { appendText("Renderer: ") }
        showListInput = element.appendElement("select") { className = "showsDiv" } as HTMLSelectElement
        showListInput.onchange = {
            selectedShow = showList.find { it.name == showListInput.selectedOptions[0]?.textContent }
            onShowChange.invoke()
        }

        element.appendElement("br") { }
        element.appendElement("b") { appendText("Data to Brains: ") }
        statsSpan = element.appendElement("span") {}
    }

    override fun listShows(shows: List<Show>) {
        showListInput.clear()
        showList = shows
        shows.forEach {
            showListInput.appendElement("option") { appendText(it.name) }
        }
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
}

class JsBrainDisplay(container: Element, detailsContainer: Element) : BrainDisplay {
    override var id: String? = null
    override var surface: Surface? = null
    override var onReset: suspend () -> Unit = {}

    private var myDiv = container.appendElement("div") {
        addClass("brain-box", "brain-offline")
        this.addEventListener("click", { GlobalScope.launch { onReset() } })
        this.addEventListener("mouseover", {
            detailsContainer.clear()
            detailsContainer.appendElement("hr") {}
            detailsContainer.appendElement("b") {
                appendText("Brain ${this@JsBrainDisplay.id}")
            }
            detailsContainer.appendElement("div") {
                appendText("Surface: ${surface?.describe()}")
            }
        })
    }

    override fun haveLink(link: Network.Link) {
        myDiv.classList.remove("brain-offline")
        myDiv.classList.add("brain-link")
    }
}

class JsVisualizerDisplay : VisualizerDisplay {
    private var visualizerFramerate: Element = document.getElementById("visualizerFramerate")!!
    private var visualizerElapsedMs: Element = document.getElementById("visualizerElapsedMs")!!

    override var renderMs: Int = 0
        set(value) {
            field = value
            visualizerFramerate.textContent = "${1000 / value}fps"
            visualizerElapsedMs.textContent = "${value}ms"
        }
}