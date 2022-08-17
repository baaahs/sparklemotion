package baaahs.ui.diagnostics

import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val FeedsView = xComponent<FeedsProps>("Feeds") { props ->

    div {
    }
}

external interface FeedsProps : Props {
}

fun RBuilder.feeds(handler: RHandler<FeedsProps>) =
    child(FeedsView, handler = handler)