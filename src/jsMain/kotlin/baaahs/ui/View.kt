package baaahs.ui

import kotlinx.browser.window
import react.RBuilder

actual interface View {
    fun RBuilder.render()
}

actual fun confirm(message: String): Boolean =
    window.confirm(message)