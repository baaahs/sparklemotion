package baaahs.ui

import react.RBuilder

actual interface View {
    fun RBuilder.render()
}

actual interface Icon {
    fun getReactIcon(): materialui.Icon
}