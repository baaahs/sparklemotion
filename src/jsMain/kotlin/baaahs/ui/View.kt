package baaahs.ui

import kotlinx.browser.window
import mui.icons.material.SvgIconComponent
import react.RBuilder

interface JsView : View {
    fun RBuilder.render()
}
fun View.render(rBuilder: RBuilder) = with (this as JsView) {
    rBuilder.render()
}

actual interface Icon {
    fun getReactIcon(): SvgIconComponent
}

actual fun confirm(message: String): Boolean =
    window.confirm(message)