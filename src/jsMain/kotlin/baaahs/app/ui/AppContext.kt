package baaahs.app.ui

import baaahs.app.settings.UiSettings
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.editor.EditIntent
import baaahs.client.ClientStageManager
import baaahs.client.Notifier
import baaahs.client.SceneEditorClient
import baaahs.client.WebClient
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.Toolchain
import baaahs.plugin.Plugins
import baaahs.ui.Prompt
import baaahs.ui.ReactBeautifulDragNDrop
import baaahs.util.Clock
import react.createContext

val appContext = createContext<AppContext>()

/**
 * The application context.
 *
 * Object identities here are guaranteed to not change over the lifetime of the application.
 * No need to include them in React watch lists.
 */
external interface AppContext {
    var showPlayer: ClientStageManager
    var dragNDrop: ReactBeautifulDragNDrop
    var webClient: WebClient.Facade
    var plugins: Plugins
    var toolchain: Toolchain
    var uiSettings: UiSettings
    var allStyles: AllStyles
    var prompt: (prompt: Prompt) -> Unit
    var clock: Clock
    var showManager: ShowManager.Facade
    var sceneManager: SceneManager.Facade
    var fileDialog: FileDialog
    var notifier: Notifier.Facade

    var openEditor: (EditIntent) -> Unit
    var openSceneEditor: (EditIntent) -> Unit

    // Scene editing:
    var sceneEditorClient: SceneEditorClient.Facade
}
