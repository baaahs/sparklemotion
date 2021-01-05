package baaahs.sim.ui

import baaahs.app.ui.AppIndexProps
import baaahs.jsx.sim.store
import baaahs.ui.ErrorDisplay
import baaahs.ui.xComponent
import external.ErrorBoundary
import react.useContext

val WebClientWindow = xComponent<AppIndexProps>("WebClientWindow") { _ ->
    val contextState = useContext(store).state
    val simulator = contextState.simulator
    val webClient = memo { simulator.createWebClient() }

    ErrorBoundary {
        attrs.FallbackComponent = ErrorDisplay

        child(webClient.render())
    }
}
