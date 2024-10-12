package baaahs.app.settings

import baaahs.app.ui.AppMode
import kotlinx.serialization.Serializable

@Serializable
data class UiSettings(
    val darkMode: Boolean = true,
    val useSharedContexts: Boolean = true,
    val renderButtonPreviews: Boolean = true,
    val appMode: AppMode = AppMode.Show,
    val developerMode: Boolean = false
)