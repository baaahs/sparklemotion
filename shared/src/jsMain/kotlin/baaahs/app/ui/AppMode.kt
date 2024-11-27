package baaahs.app.ui

import baaahs.client.document.DocumentManager
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager

enum class AppMode {
    Show {
        override val otherOne: AppMode get() = Scene
        override fun getDocumentManager(appContext: AppContext): ShowManager.Facade =
            appContext.showManager
    },
    Scene {
        override val otherOne: AppMode get() = Show
        override fun getDocumentManager(appContext: AppContext): SceneManager.Facade =
            appContext.sceneManager
    };

    abstract val otherOne: AppMode
    abstract fun getDocumentManager(appContext: AppContext): DocumentManager<*, *, *>.Facade
}