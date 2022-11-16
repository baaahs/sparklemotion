package baaahs.app.ui

import baaahs.app.settings.UiSettings
import baaahs.app.ui.dialog.FileDialog
import baaahs.app.ui.editor.EditIntent
import baaahs.app.ui.layout.GridLayoutContext
import baaahs.client.ClientStageManager
import baaahs.client.Notifier
import baaahs.client.SceneEditorClient
import baaahs.client.WebClient
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.Toolchain
import baaahs.libraries.ShaderLibraries
import baaahs.plugin.Plugins
import baaahs.scene.SceneProvider
import baaahs.ui.KeyboardShortcutHandler
import baaahs.ui.Prompt
import baaahs.util.Clock
import js.core.jso
import react.createContext

val appContext = createContext<AppContext>(jso {})
val toolchainContext = createContext<Toolchain>(jso {})

/**
 * The application context.
 *
 * Object identities here are guaranteed to not change over the lifetime of the application.
 * No need to include them in React watch lists.
 */
external interface AppContext {
    var showPlayer: ClientStageManager
    var webClient: WebClient.Facade
    var plugins: Plugins
    var uiSettings: UiSettings
    var allStyles: AllStyles
    var prompt: (prompt: Prompt) -> Unit
    var keyboard: KeyboardShortcutHandler
    var clock: Clock
    var showManager: ShowManager.Facade
    var sceneManager: SceneManager.Facade
    var sceneProvider: SceneProvider
    var shaderLibraries: ShaderLibraries
    var fileDialog: FileDialog
    var notifier: Notifier.Facade
    var gridLayoutContext: GridLayoutContext

    var openEditor: (EditIntent) -> Unit
    var openSceneEditor: (EditIntent) -> Unit

    // Scene editing:
    var sceneEditorClient: SceneEditorClient.Facade
}
