package baaahs.app.settings

import baaahs.app.ui.AppMode
import kotlinx.serialization.Serializable

@Serializable
data class UiSettings(
    val darkMode: Boolean = false,
    val useSharedContexts: Boolean = true,
    val renderButtonPreviews: Boolean = false,
    val appMode: AppMode = AppMode.Show
)