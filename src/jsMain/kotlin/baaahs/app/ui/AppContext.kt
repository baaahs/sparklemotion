package baaahs.app.ui

import baaahs.ShowPlayer
import baaahs.client.WebClient
import baaahs.glshaders.AutoWirer
import baaahs.glshaders.Plugins
import react.createContext

val appContext = createContext<AppContext>()

external interface AppContext {
    var showPlayer: ShowPlayer
    var dragNDrop: DragNDrop
    var webClient: WebClient.Facade
    var plugins: Plugins
    var autoWirer: AutoWirer
}
