package baaahs.app.ui

import baaahs.client.WebClient
import baaahs.scene.Scene
import baaahs.show.Show

interface AppState {
    class FullScreenMessage(
        val title: String,
        val message: String,
        val isInProgress: Boolean = true
    ) : AppState

    object ShowView : AppState
    object SceneView : AppState

    companion object {
        fun getState(webClient: WebClient.Facade, show: Show?, scene: Scene?): AppState {
            val uiSettings = webClient.uiSettings
            val appMode = uiSettings.appMode

            return if (!webClient.isConnected) {
                FullScreenMessage("Connecting…", "Attempting to connect to Sparkle Motion.")
            } else if (!webClient.serverIsOnline) {
                FullScreenMessage("Connecting…", "Sparkle Motion is initializing.")
            } else if (appMode == AppMode.Show) {
                if (!webClient.showManagerIsReady) {
                    FullScreenMessage("Connecting…", "Show is initializing.")
                } else if (show == null) {
                    FullScreenMessage("No open show.", "Maybe you'd like to open one?", isInProgress = false)
                } else if (webClient.isMapping) {
                    FullScreenMessage("Mapper Running…", "Please wait.")
                } else ShowView
            } else if (appMode == AppMode.Scene) {
                if (scene == null) {
                    FullScreenMessage("No open scene.", "Maybe you'd like to open one?", isInProgress = false)
                } else SceneView
            } else error("Unknown app mode: $appMode")
        }
    }
}