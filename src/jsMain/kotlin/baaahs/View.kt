package baaahs

import org.w3c.dom.*
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

fun <T : HTMLElement> HTMLElement.first(className: String) : T = (getElementsByClassName(className)[0] as T?)!!
fun HTMLCanvasElement.context2d() = this.getContext("2d")!! as CanvasRenderingContext2D

open class Button<T>(val data: T, val element: Element) {
    lateinit var allButtons: List<Button<T>>
    var onSelect: ((T) -> Unit)? = null

    init {
        element.addEventListener("click", { onClick() })
    }

    fun setSelected(isSelected: Boolean) {
        element.classList.toggle("selected", isSelected)
    }

    fun onClick() {
        setSelected(true)
        allButtons.forEach { it.setSelected(false) }
        onSelect?.invoke(data)
    }
}

interface HostedWebApp {
    @JsName("render")
    fun render(parentNode: HTMLElement)

    @JsName("onClose")
    fun onClose()
}

interface DomContainer {
    fun createFrame(name: String, hostedWebApp: HostedWebApp): Frame

    interface Frame {
        @JsName("containerNode")
        val containerNode: Element

        @JsName("close")
        fun close()
    }
}

class FakeDomContainer : DomContainer {
    override fun createFrame(name: String, hostedWebApp: HostedWebApp): DomContainer.Frame =
        js("document.createFakeClientDevice")(name, hostedWebApp)
}