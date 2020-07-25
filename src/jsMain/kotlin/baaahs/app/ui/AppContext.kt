package baaahs.app.ui

import baaahs.ShowResources
import baaahs.client.WebClient
import react.createContext

val appContext = createContext<AppContext>()

external interface AppContext {
    var showResources: ShowResources
    var dragNDrop: DragNDrop
    var webClient: WebClient.Facade
}
