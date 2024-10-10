package baaahs.sim.ui

import baaahs.sim.HostedWebApp
import baaahs.ui.ErrorDisplay
import baaahs.ui.xComponent
import external.ErrorBoundary
import react.Props
import react.RBuilder
import react.RHandler

val WebClientWindowView = xComponent<WebClientWindowProps>("WebClientWindow") { props ->
    ErrorBoundary {
        attrs.FallbackComponent = ErrorDisplay

        child(props.hostedWebApp.render())
    }
}

external interface WebClientWindowProps : Props {
    var hostedWebApp: HostedWebApp
}

fun RBuilder.webClientWindow(handler: RHandler<WebClientWindowProps>) =
    child(WebClientWindowView, handler = handler)