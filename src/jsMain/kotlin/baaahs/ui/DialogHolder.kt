package baaahs.ui

import react.RBuilder

fun DialogHolder.showDialog(block: RBuilder.() -> Unit) {
    showDialog(object : JsView {
        override fun RBuilder.render() {
            block()
        }
    })
}