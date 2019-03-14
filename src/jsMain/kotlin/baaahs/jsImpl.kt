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
import kotlin.js.Date

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

    override fun forPinky(): PinkyDisplay =
        JsPinkyDisplay(document.getElementById("pinkyView")!!)

    override fun forBrain(): BrainDisplay =
        JsBrainDisplay(document.getElementById("brainsView")!!)

    override fun forMapper(): MapperDisplay =
        JsMapperDisplay(document.getElementById("mapperView")!!)
}

class JsNetworkDisplay(document: Document) : NetworkDisplay {
    private val packetsReceivedSpan = document.getElementById("networkPacketsReceived")!!
    private val packetsDroppedSpan = document.getElementById("networkPacketsDropped")!!

    private var packetsReceived = 0
    private var packetsDropped = 0

    override fun receivedPacket() {
        packetsReceivedSpan.clear()
        packetsReceivedSpan.appendText(packetsReceived++.toString())
    }

    override fun droppedPacket() {
        packetsDroppedSpan.clear()
        packetsDroppedSpan.appendText(packetsDropped++.toString())
    }
}

class JsPinkyDisplay(element: Element) : PinkyDisplay {
    override var color: Color? = null

    private val consoleDiv: Element
    private val beat1: Element
    private val beat2: Element
    private val beat3: Element
    private val beat4: Element
    private val beats: List<Element>
    private val colorButtons: List<ColorButton>
    private val brainCountDiv: Element

    init {
        element.appendText("Brains online: ")
        brainCountDiv = element.appendElement("span") {}

        val beatsDiv = element.appendElement("div") {
            id = "beatsDiv"
            appendElement("b") { appendText("Beats: ") }
        }
        beat1 = beatsDiv.appendElement("span") { appendText("1") }
        beat2 = beatsDiv.appendElement("span") { appendText("2") }
        beat3 = beatsDiv.appendElement("span") { appendText("3") }
        beat4 = beatsDiv.appendElement("span") { appendText("4") }
        beats = listOf(beat1, beat2, beat3, beat4)

        val colorsDiv = element.appendElement("div") {
            id = "colorsDiv"
            appendElement("b") { appendText("Colors: ") }
        }
        colorButtons = listOf(
            ColorButton(Color.WHITE, colorsDiv.appendElement("span") { }),
            ColorButton(Color.RED, colorsDiv.appendElement("span") {}),
            ColorButton(Color.ORANGE, colorsDiv.appendElement("span") {}),
            ColorButton(Color.YELLOW, colorsDiv.appendElement("span") {}),
            ColorButton(Color.GREEN, colorsDiv.appendElement("span") {}),
            ColorButton(Color.BLUE, colorsDiv.appendElement("span") {}),
            ColorButton(Color.PURPLE, colorsDiv.appendElement("span") {})
        )
        colorButtons.forEach {it.allButtons = colorButtons; it.onSelect = { this.color = it } }
        colorButtons.random()!!.select()

        consoleDiv = element.appendElement("div") {}
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

    private class ColorButton(val color: Color, val button: Element) {
        lateinit var allButtons: List<ColorButton>
        var onSelect: ((Color) -> Unit)? = null

        init {
            button.setAttribute("style", "background-color: #${color.toHexString()}")
            button.addEventListener("click", { select() })
        }

        fun select() {
            allButtons.forEach { it.button.classList.clear() }
            button.classList.add("selected")
            onSelect?.invoke(color)
        }
    }
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

class JsMapperDisplay(val element: Element) : MapperDisplay {
    private var startButton = element.ownerDocument!!.getElementById("mapperStartButton")!!
    private var stopButton = element.ownerDocument!!.getElementById("mapperStopButton")!!

    override var onStart: (() -> Unit)? = null
    override var onStop: (() -> Unit)? = null

    init {
        updateButtons(false)
        startButton.addEventListener("click", {
            updateButtons(true)
            onStart?.invoke()
        })
        stopButton.addEventListener("click", {
            updateButtons(false)
            onStop?.invoke()
        })
    }

    private fun updateButtons(isRunning: Boolean) {
        startButton.disabled = isRunning
        stopButton.disabled = !isRunning
    }
}

private var Element.disabled: Boolean
    get() = getAttribute("disabled") == "disabled"
    set(value) {
        if (value) {
            setAttribute("disabled", "disabled")
        } else {
            removeAttribute("disabled")
        }
    }

private fun DOMTokenList.clear() {
    while (length > 0) {
        remove(item(0)!!)
    }
}

fun <T> ItemArrayLike<T>.forEach(action: (T) -> Unit) {
    for (i in 0 until length) {
        action(item(i)!!)
    }
}

actual fun getTimeMillis(): Long = Date().getTime().toLong()