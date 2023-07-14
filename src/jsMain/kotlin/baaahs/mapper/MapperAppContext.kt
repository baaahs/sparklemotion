package baaahs.mapper

import baaahs.app.ui.AllStyles
import baaahs.client.SceneEditorClient
import baaahs.plugin.Plugins
import baaahs.ui.KeyboardShortcutHandler
import baaahs.util.Clock
import js.core.jso
import react.createContext

val mapperAppContext = createContext<MapperAppContext>(jso {})

external interface MapperAppContext {
    var sceneEditorClient: SceneEditorClient.Facade
    var plugins: Plugins
    var allStyles: AllStyles
    var keyboard: KeyboardShortcutHandler
    var clock: Clock
}