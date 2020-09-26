package baaahs.ui

import react.RBuilder

actual interface Renderer {
    fun RBuilder.render()
}

actual interface Icon {
    fun getReactIcon(): materialui.Icon
}