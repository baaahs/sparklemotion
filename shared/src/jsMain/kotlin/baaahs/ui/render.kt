package baaahs.ui

import react.RBuilder

fun View.render(rBuilder: RBuilder) = with (this as JsView) {
    rBuilder.render()
}
