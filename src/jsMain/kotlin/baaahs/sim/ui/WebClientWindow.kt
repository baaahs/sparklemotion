package baaahs.sim.ui

import baaahs.sim.HostedWebApp
import baaahs.ui.ErrorDisplay
import baaahs.ui.xComponent
import external.ErrorBoundary
import react.RProps

val WebClientWindow = xComponent<WebClientWindowProps>("WebClientWindow") { props ->
    ErrorBoundary {
        attrs.FallbackComponent = ErrorDisplay

        child(props.hostedWebApp.render())
    }
}

external interface WebClientWindowProps : RProps {
    var hostedWebApp: HostedWebApp
}

object Views {
    val webClientWindow = WebClientWindow
}