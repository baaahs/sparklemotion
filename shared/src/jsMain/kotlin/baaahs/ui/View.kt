package baaahs.ui

import kotlinx.browser.window
import react.RBuilder

interface JsView : View {
    fun RBuilder.render()
}
fun View.render(rBuilder: RBuilder) = with (this as JsView) {
    rBuilder.render()
}

actual fun confirm(message: String): Boolean =
    window.confirm(message)