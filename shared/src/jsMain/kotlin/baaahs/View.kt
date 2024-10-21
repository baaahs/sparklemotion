package baaahs

import web.canvas.CanvasRenderingContext2D
import web.dom.DOMTokenList
import web.dom.Element
import web.gl.WebGL2RenderingContext
import web.gl.WebGLRenderingContext
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

fun HTMLCanvasElement.get2DContext() = this.getContext(CanvasRenderingContext2D.ID)!!
fun HTMLCanvasElement.getWebGLContext() = this.getContext(WebGLRenderingContext.ID)
fun HTMLCanvasElement.getWebGL2Context() = this.getContext(WebGL2RenderingContext.ID)