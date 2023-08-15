package baaahs.sim

import baaahs.io.Fs
import baaahs.ui.Observable
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

class SimulatorSettingsManager(private val fs: Fs) : Observable() {
    private val simSettingsFile get() = fs.resolve("simulator-settings.json")
    var simSettings: SimSettings = SimSettings()
        private set

    suspend fun start() {
        simSettingsFile.read()?.let {
            simSettings = Json.decodeFromString(it)
            notifyChanged()
        }
    }
}

@Serializable
data class SimSettings(
    val pluginSettings: Map<String, JsonObject> = emptyMap()
) {
    fun <T> getConfig(id: String, serializer: KSerializer<T>): T? =
        pluginSettings[id]?.let { Json.decodeFromJsonElement(serializer, it) }
}