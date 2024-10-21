package baaahs.sm.server

import baaahs.client.document.DataStore
import baaahs.io.Fs
import baaahs.migrator.DataMigrator
import baaahs.plugin.Plugins
import kotlinx.serialization.Serializable

@Serializable
data class PinkyConfig(
    val runningShowPath: String? = null,
    val runningScenePath: String? = null
)

class PinkyConfigStore(
    plugins: Plugins,
    dataDir: Fs.File
) {
    private val configFile = dataDir.resolve("config.json")
    private val store = DataStore(plugins, DataMigrator(PinkyConfig.serializer()))

    suspend fun load(): PinkyConfig? =
        store.load(configFile)

    suspend fun update(update: PinkyConfig.() -> PinkyConfig) {
        val oldConfig = load() ?: PinkyConfig()
        val newConfig = oldConfig.update()
        if (newConfig != oldConfig) {
            store.save(configFile, newConfig, allowOverwrite = true)
        }
    }
}