package baaahs

import baaahs.net.Network
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.Element
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.get
import kotlin.browser.document
import kotlin.dom.addClass
import kotlin.dom.appendElement
import kotlin.dom.appendText
import kotlin.dom.clear
import kotlin.math.roundToInt

class JsDisplay : Display {
    override fun forPinky(): PinkyDisplay =
        JsPinkyDisplay(document.getElementById("pinkyView")!!)

    override fun forBrain(): BrainDisplay =
        JsBrainDisplay(
            document.getElementById("brainsView")!!,
            document.getElementById("brainDetails")!!
        )

    override fun forVisualizer(): VisualizerDisplay = JsVisualizerDisplay()
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
            val framerate = 1000f / value

            // Probably means this is the first datapoint we've received.
            if (field == 0) {
                showAvgFramerate = framerate
                showAvgElapsedMs = value.toFloat()
            }

            field = value

            showFramerate.textContent = "${framerate.roundToInt()}fps"
            showElapsedMs.textContent = "${value}ms"

            showAvgFramerate = (showAvgFramerate * 99 + framerate) / 100
            showAvgFramerateEl.textContent = "${showAvgFramerate.roundToInt()}fps"

            showAvgElapsedMs = (showAvgElapsedMs * 99 + value) / 100
            showAvgElapsedMsEl.textContent = "${showAvgElapsedMs.roundToInt()}ms"
        }

    override var stats: Pinky.NetworkStats? = null
        set(value) {
            field = value
            statsSpan.textContent = value?.run { "$bytesSent bytes / $packetsSent packets per frame" } ?: "?"
        }

    private val brainCountDiv: Element
    private val beat1: Element
    private val beat2: Element
    private val beat3: Element
    private val beat4: Element
    private val beats: List<Element>
    private val bpmSpan: Element
    private val beatConfidenceElement: Element
    private var showList = emptyList<Show>()
    private val showListInput: HTMLSelectElement
    private var showFramerate: Element = document.getElementById("showFramerate")!!
    private var showElapsedMs: Element = document.getElementById("showElapsedMs")!!
    private var showAvgFramerateEl: Element = document.getElementById("showAvgFramerate")!!
    private var showAvgElapsedMsEl: Element = document.getElementById("showAvgElapsedMs")!!
    private var showAvgFramerate = 0f
    private var showAvgElapsedMs = 0f
    private var statsSpan: Element

    init {
        element.appendText("Current Show: ")
        showListInput = element.appendElement("select") { className = "showsDiv" } as HTMLSelectElement
        showListInput.onchange = {
            selectedShow = showList.find { it.name == showListInput.selectedOptions[0]?.textContent }
            onShowChange.invoke()
        }

        element.appendElement("br") { }
        element.appendText("Brains online: ")
        brainCountDiv = element.appendElement("span") {}

        val beatsDiv = element.appendElement("div") {
            id = "beatsDiv"
            appendElement("b") { appendText("Beats: ") }
        }
        beatConfidenceElement = beatsDiv.appendElement("span") {
            appendText("[confidence: ?]")
        }
        beatsDiv.appendElement("br") {}
        beat1 = beatsDiv.appendElement("div") { appendText("1") }
        beat2 = beatsDiv.appendElement("div") { appendText("2") }
        beat3 = beatsDiv.appendElement("div") { appendText("3") }
        beat4 = beatsDiv.appendElement("div") { appendText("4") }
        beats = listOf(beat1, beat2, beat3, beat4)

        bpmSpan = beatsDiv.appendElement("span") { appendText("…BPM") }
        bpmSpan.classList.add("bpmDisplay-beatOff")

        element.appendElement("br") { }
        element.appendElement("b") { appendText("Data to Brains:") }
        element.appendElement("br") { }
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
        set(value) {
            if (value < 0 || value > 3) return

            try {
                beats[field].classList.clear()
                beats[value].classList.add("selected")
                if (value % 2 == 1) {
                    bpmSpan.classList.add("bpmDisplay-beatOn")
                } else {
                    bpmSpan.classList.remove("bpmDisplay-beatOn")
                }
            } catch (e: Exception) {
                println("durrr error $e")
            }

            field = value
        }

    fun Double.format(digits: Int): String = this.asDynamic().toFixed(digits) as String
    fun Float.format(digits: Int): String = this.asDynamic().toFixed(digits) as String

    override var bpm: Float = 0.0f
        set(value) {
            bpmSpan.textContent = "${value.format(1)} BPM"
            field = value
        }

    override var beatConfidence: Float = 1.0f
        set(value) {
            beatConfidenceElement.textContent = "[confidence: ${value * 100}%]"
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