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
    fun decode(content: String, fileName: String? = null) =
        plugins.json.decodeFromString(migrator.Migrate(fileName), content)

    fun decode(content: JsonElement, fileName: String? = null) =
        plugins.json.decodeFromJsonElement(migrator.Migrate(fileName), content)

    suspend fun load(file: Fs.File): T? =
        file.read()?.let { decode(it, file.toString()) }

    fun encode(content: T) =
        plugins.json.encodeToString(migrator.Migrate(), content)

    fun encodeToJsonElement(content: T) =
        plugins.json.encodeToJsonElement(migrator.Migrate(), content)

    suspend fun save(file: Fs.File, content: T, allowOverwrite: Boolean = false) =
        file.write(
            encode(content),
            allowOverwrite
        )
}