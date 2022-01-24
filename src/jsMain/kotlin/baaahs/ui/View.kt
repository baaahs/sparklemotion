package baaahs.ui

import kotlinx.browser.window
import react.RBuilder

actual interface View {
    fun RBuilder.render()
}

actual interface Icon {
    fun getReactIcon(): materialui.Icon
}

actual fun confirm(message: String): Boolean =
    window.confirm(message)