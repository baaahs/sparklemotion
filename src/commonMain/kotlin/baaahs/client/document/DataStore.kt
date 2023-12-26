package baaahs.client.document

import baaahs.io.Fs
import baaahs.migrator.DataMigrator
import baaahs.plugin.Plugins
import baaahs.scene.migration.SceneMigrator
import baaahs.show.migration.ShowMigrator

val Plugins.showStore get() = DataStore(this, ShowMigrator)
val Plugins.sceneStore get() = DataStore(this, SceneMigrator)

class DataStore<T : Any>(
    private val plugins: Plugins,
    private val migrator: DataMigrator<T>
) {
    suspend fun load(file: Fs.File): T? =
        file.read()?.let {
            plugins.json.decodeFromString(migrator, it)
        }

    suspend fun save(file: Fs.File, content: T, allowOverwrite: Boolean = false) =
        file.write(
            plugins.json.encodeToString(migrator, content),
            allowOverwrite
        )
}