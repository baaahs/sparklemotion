package baaahs.sim.ui

import baaahs.app.ui.AppIndexProps
import baaahs.app.ui.appIndex
import baaahs.client.ClientShowResources
import baaahs.glsl.GlslBase
import baaahs.jsx.sim.store
import baaahs.sim.FakeFs
import baaahs.ui.ErrorDisplay
import baaahs.ui.SaveAsFs
import external.ErrorBoundary
import react.functionalComponent
import react.useContext
import react.useMemo

/** Changes here should also be applied to [baaahs.WebUi]. */
val AppWindow = functionalComponent<AppIndexProps> { props_DO_NOT_USE ->
    val contextState = useContext(store).state
    val pubSub = useMemo({ contextState.simulator.getPubSub() }, arrayOf(contextState.simulator))
    val plugins = contextState.simulator.plugins
    val glslContext = useMemo({ GlslBase.manager.createContext() }, arrayOf())
    val clientShowResources = useMemo({
        ClientShowResources(plugins, glslContext, pubSub)
    }, arrayOf())
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
            this.showResources = clientShowResources
        }
    }
}
