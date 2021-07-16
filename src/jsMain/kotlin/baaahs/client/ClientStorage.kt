package baaahs.client

import baaahs.app.ui.settings.UiSettings
import baaahs.sim.BrowserSandboxFs
import kotlinx.serialization.json.Json

class ClientStorage(private val browserSandboxFs: BrowserSandboxFs) {
    private val file get() = browserSandboxFs.resolve("ui-settings.json")

    suspend fun loadSettings(): UiSettings? {
        return file.read()?.let { json.decodeFromString(UiSettings.serializer(), it) }
    }

    suspend fun saveSettings(uiSettings: UiSettings) {
        file.write(json.encodeToString(UiSettings.serializer(), uiSettings), allowOverwrite = true)
    }

    companion object {
        val json = Json { isLenient = true }
    }
}