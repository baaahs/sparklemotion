package baaahs.client

import baaahs.app.settings.UiSettings
import baaahs.mapper.MapperData
import baaahs.sim.BrowserSandboxFs
import kotlinx.serialization.json.Json

class ClientStorage(private val browserSandboxFs: BrowserSandboxFs) {
    private val uiSettingsFile get() = browserSandboxFs.resolve("ui-settings.json")
    private val mapperDataFile get() = browserSandboxFs.resolve("mapper-data.json")

    suspend fun loadSettings(): UiSettings? =
        uiSettingsFile.read()?.let { json.decodeFromString(UiSettings.serializer(), it) }

    suspend fun saveSettings(uiSettings: UiSettings) {
        this.uiSettingsFile.write(json.encodeToString(UiSettings.serializer(), uiSettings), allowOverwrite = true)
    }

    suspend fun loadMapperData(): MapperData? =
        mapperDataFile.read()?.let { json.decodeFromString(MapperData.serializer(), it) }

    suspend fun saveMapperData(mapperData: MapperData) {
        mapperDataFile.write(json.encodeToString(MapperData.serializer(), mapperData), allowOverwrite = true)
    }

    companion object {
        val json = Json { isLenient = true }
    }
}