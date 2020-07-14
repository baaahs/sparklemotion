package baaahs.app.ui

import baaahs.ShowResources
import react.createContext

val appContext = createContext<AppContext>()

external interface AppContext {
    var showResources: ShowResources
}
