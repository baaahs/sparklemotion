package baaahs.sim.ui

import baaahs.app.ui.AppIndexProps
import baaahs.app.ui.appIndex
import baaahs.jsx.sim.store
import baaahs.sim.FakeFs
import baaahs.ui.ErrorDisplay
import baaahs.ui.SaveAsFs
import external.ErrorBoundary
import react.functionalComponent
import react.useContext
import react.useMemo

val AppWindow = functionalComponent<AppIndexProps> {
    val contextState = useContext(store).state
    val pubSub = useMemo({ contextState.simulator.getPubSub() }, arrayOf(contextState.simulator))
    val saveAsFilesystems = listOf(
        SaveAsFs("Shader Library", contextState.simulator.fs),
        SaveAsFs("Show", FakeFs())
    )

    ErrorBoundary {
        attrs.FallbackComponent = ErrorDisplay

        appIndex {
            this.id = "Simulator Window"
            this.pubSub = pubSub
            this.filesystems = saveAsFilesystems
        }
    }
}
