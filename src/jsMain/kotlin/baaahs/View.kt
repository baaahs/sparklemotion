package baaahs

import org.w3c.dom.DOMTokenList
import org.w3c.dom.Element
import org.w3c.dom.ItemArrayLike
import kotlin.dom.appendElement
import kotlin.dom.appendText

var Element.disabled: Boolean
    get() = getAttribute("disabled") == "disabled"
    set(value) {
        if (value) {
            setAttribute("disabled", "disabled")
        } else {
            removeAttribute("disabled")
        }
    }

fun <T> ItemArrayLike<T>.forEach(action: (T) -> Unit) {
    for (i in 0 until length) {
        action(item(i)!!)
    }
}

fun DOMTokenList.clear() {
    while (length > 0) {
        remove(item(0)!!)
    }
}

open class Button<T>(val data: T, val element: Element) {
    lateinit var allButtons: List<Button<T>>
    var onSelect: ((T) -> Unit)? = null

    init {
        element.addEventListener("click", { select() })
    }

    fun select() {
        allButtons.forEach { it.element.classList.clear() }
        element.classList.add("selected")
        onSelect?.invoke(data)
    }
}

class ColorPickerView(element: Element, onSelect: (Color) -> Unit) {
    private val colorButtons: List<ColorButton>

    init {
        val colorsDiv = element.appendElement("div") {
            className = "colorsDiv"
            appendElement("b") { appendText("Colors: ") }
            appendElement("br") {}
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
        colorButtons.forEach {
            it.allButtons = colorButtons
            it.element.setAttribute("style", "background-color: #${it.data.toHexString()}")
            it.onSelect = { onSelect(it) }
        }
        colorButtons.random()!!.select()
    }

    private class ColorButton(color: Color, element: Element) : Button<Color>(color, element)
}
