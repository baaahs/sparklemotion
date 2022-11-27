package baaahs

import canvas.CanvasRenderingContext2D
import dom.DOMTokenList
import dom.Element
import dom.html.*
import kotlinx.js.get

val document get() = browser.document
val window get() = browser.window

var Element.disabled: Boolean
    get() = getAttribute("disabled") == "disabled"
    set(value) {
        if (value) {
            setAttribute("disabled", "disabled")
        } else {
            removeAttribute("disabled")
        }
    }

fun DOMTokenList.clear() {
    while (length > 0) {
        remove(item(0)!!)
    }
}

@Suppress("UNCHECKED_CAST")
fun <T : HTMLElement> HTMLElement.first(className: String) : T =
    (getElementsByClassName(className)[0] as T?)!!

fun HTMLCanvasElement.context2d() = this.getContext(RenderingContextId.canvas)!! as CanvasRenderingContext2D
