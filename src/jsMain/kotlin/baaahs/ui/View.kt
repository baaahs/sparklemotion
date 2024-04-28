package baaahs.ui

import kotlinx.browser.window
import mui.icons.material.SvgIconComponent
import react.RBuilder

actual interface View {
    fun RBuilder.render()
}

actual interface Icon {
    fun getReactIcon(): SvgIconComponent
}

actual fun confirm(message: String): Boolean =
    window.confirm(message)