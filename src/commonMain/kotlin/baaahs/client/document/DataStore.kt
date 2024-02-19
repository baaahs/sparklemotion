package baaahs.client.document

import baaahs.io.Fs
import baaahs.mapper.migration.MappingSessionMigrator
import baaahs.migrator.DataMigrator
import baaahs.plugin.Plugins
import baaahs.scene.migration.SceneMigrator
import baaahs.show.migration.ShowMigrator
import kotlinx.serialization.json.JsonElement

val Plugins.mappingSessionStore get() = DataStore(this, MappingSessionMigrator)
val Plugins.showStore get() = DataStore(this, ShowMigrator)
val Plugins.sceneStore get() = DataStore(this, SceneMigrator)

class DataStore<T : Any>(
    private val plugins: Plugins,
    private val migrator: DataMigrator<T>
) {
    fun decode(content: String) =
        plugins.json.decodeFromString(migrator, content)

    fun decode(content: JsonElement) =
        plugins.json.decodeFromJsonElement(migrator, content)

    suspend fun load(file: Fs.File): T? =
        file.read()?.let { decode(it) }

    fun encode(content: T) =
        plugins.json.encodeToString(migrator, content)

    fun encodeToJsonElement(content: T) =
        plugins.json.encodeToJsonElement(migrator, content)

    suspend fun save(file: Fs.File, content: T, allowOverwrite: Boolean = false) =
        file.write(
            encode(content),
            allowOverwrite
        )
}