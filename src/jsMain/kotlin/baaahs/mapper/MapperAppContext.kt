package baaahs.mapper

import baaahs.admin.AdminClient
import baaahs.app.ui.AllStyles
import baaahs.plugin.Plugins
import baaahs.util.Clock
import react.createContext

val mapperAppContext = createContext<MapperAppContext>()

external interface MapperAppContext {
    var adminClient: AdminClient.Facade
    var plugins: Plugins
    var allStyles: AllStyles
    var clock: Clock
}