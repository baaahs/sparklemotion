package baaahs.mapper

import baaahs.app.ui.AllStyles
import baaahs.plugin.Plugins
import baaahs.util.Clock
import react.createContext

val mapperAppContext = createContext<MapperAppContext>()

external interface MapperAppContext {
    var plugins: Plugins
    var allStyles: AllStyles
    var clock: Clock
}