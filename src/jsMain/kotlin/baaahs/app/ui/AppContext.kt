package baaahs.app.ui

import baaahs.Clock
import baaahs.ShowPlayer
import baaahs.client.WebClient
import baaahs.gl.patch.AutoWirer
import baaahs.plugin.Plugins
import baaahs.ui.Prompt
import baaahs.ui.ReactBeautifulDragNDrop
import react.createContext

val appContext = createContext<AppContext>()

/**
 * The application context.
 *
 * Object identities here are guaranteed to not change over the lifetime of the application.
 * No need to include them in React watch lists.
 */
external interface AppContext {
    var showPlayer: ShowPlayer
    var dragNDrop: ReactBeautifulDragNDrop
    var webClient: WebClient.Facade
    var plugins: Plugins
    var autoWirer: AutoWirer
    var allStyles: AllStyles
    var prompt: (prompt: Prompt) -> Unit
    var clock: Clock

    var openEditor: (EditIntent) -> Unit
}
