package baaahs.ui

import kotlinx.browser.window
import react.RBuilder

interface JsView : View {
    fun RBuilder.render()
}

actual fun confirm(message: String): Boolean =
    window.confirm(message)