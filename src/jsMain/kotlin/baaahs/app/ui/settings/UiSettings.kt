package baaahs.app.ui.settings

import kotlinx.serialization.Serializable

@Serializable
data class UiSettings(
    val darkMode: Boolean = false,
    val useSharedContexts: Boolean = true,
    val renderButtonPreviews: Boolean = false,
)