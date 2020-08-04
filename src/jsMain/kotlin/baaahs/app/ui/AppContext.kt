package baaahs.app.ui

import baaahs.ShowPlayer
import baaahs.client.WebClient
import react.createContext

val appContext = createContext<AppContext>()

external interface AppContext {
    var showPlayer: ShowPlayer
    var dragNDrop: DragNDrop
    var webClient: WebClient.Facade
}
