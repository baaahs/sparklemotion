package baaahs

import web.canvas.RenderingContextId
import web.dom.DOMTokenList
import web.dom.Element
import web.html.HTMLCanvasElement
import web.html.HTMLElement

val document get() = web.dom.document
val window get() = web.window.window

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

fun HTMLCanvasElement.context2d() = this.getContext(RenderingContextId.canvas)!!
