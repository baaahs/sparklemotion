package baaahs.sim.ui

import baaahs.sim.HostedWebApp
import baaahs.ui.ErrorDisplay
import baaahs.ui.xComponent
import external.ErrorBoundary
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

val WebClientWindowView = xComponent<WebClientWindowProps>("WebClientWindow") { props ->
    ErrorBoundary {
        attrs.FallbackComponent = ErrorDisplay

        child(props.hostedWebApp.render())
    }
}

external interface WebClientWindowProps : RProps {
    var hostedWebApp: HostedWebApp
}

fun RBuilder.webClientWindow(handler: RHandler<WebClientWindowProps>) =
    child(WebClientWindowView, handler = handler)

object Views {
    val webClientWindow = WebClientWindowView
}