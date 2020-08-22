package baaahs.sim.ui

import baaahs.app.ui.AppIndexProps
import baaahs.client.WebClient
import baaahs.jsx.sim.store
import baaahs.ui.ErrorDisplay
import baaahs.ui.xComponent
import external.ErrorBoundary
import react.useContext

val WebClientWindow = xComponent<AppIndexProps>("WebClientWindow") { props_DO_NOT_USE ->
    val contextState = useContext(store).state
    val simulator = contextState.simulator
    val webClient = memo {
        with(simulator) {
            WebClient(network, pinkyAddress, plugins)
        }
    }

    ErrorBoundary {
        attrs.FallbackComponent = ErrorDisplay

        child(webClient.render())
    }
}
