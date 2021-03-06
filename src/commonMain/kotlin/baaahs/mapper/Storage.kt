package baaahs.mapper

import baaahs.PinkyConfig
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.libraries.ShaderLibraryIndexFile
import baaahs.model.Model
import baaahs.plugin.Plugins
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.util.Logger
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class Storage(val fs: Fs, val plugins: Plugins) {
    val fsSerializer = FsServerSideSerializer()

    private val configFile = fs.resolve("config.json")

    companion object {
        private val logger = Logger("Storage")
        val json = Json { isLenient = true }

        private val format = DateFormat("yyyy''MM''dd'-'HH''mm''ss")

        fun formatDateTime(dateTime: DateTime): String {
            return dateTime.format(format)
        }
    }

    suspend fun listSessions(): List<Fs.File> {
        val mappingSessionsDir = fs.resolve("mapping-sessions")
        return fs.listFiles(mappingSessionsDir).filter { it.name.endsWith(".json") }
    }

    suspend fun saveSession(mappingSession: MappingSession): Fs.File {
        val file =
            fs.resolve(
                "mapping-sessions",
                "${formatDateTime(mappingSession.startedAtDateTime)}-v${mappingSession.version}.json"
            )
        fs.saveFile(file, json.encodeToString(MappingSession.serializer(), mappingSession))
        return file
    }

    suspend fun saveImage(name: String, imageData: ByteArray) {
        val file = fs.resolve("mapping-sessions", "images", name)
        fs.saveFile(file, imageData)
    }

    suspend fun loadMappingData(model: Model): MappingResults {
        val sessions = arrayListOf<MappingSession>()
        val path = fs.resolve("mapping", model.name)
        fs.listFiles(path).forEach { dir ->
            fs.listFiles(dir)
                .sortedBy { it.name }
                .filter { it.name.endsWith(".json") }
                .forEach { f ->
                    sessions.add(loadMappingSession(f))
                }
        }
        return SessionMappingResults(model, sessions)
    }

    suspend fun loadMappingSession(name: String): MappingSession {
        val file = fs.resolve("mapping-sessions", name)
        return loadMappingSession(file)
    }

    suspend fun loadMappingSession(f: Fs.File): MappingSession {
        val mappingJson = fs.loadFile(f)
        val mappingSession = json.decodeFromString(MappingSession.serializer(), mappingJson!!)
        mappingSession.surfaces.forEach { surface ->
            logger.debug { "Found pixel mapping for ${surface.entityName} (${surface.controllerId.shortName()})" }
        }
        return mappingSession
    }

    suspend fun loadConfig(): PinkyConfig? {
        return loadJson(configFile, PinkyConfig.serializer())
    }

    suspend fun updateConfig(update: PinkyConfig.() -> PinkyConfig) {
        val oldConfig = loadConfig() ?: PinkyConfig(null)
        val newConfig = oldConfig.update()
        configFile.write(json.encodeToString(PinkyConfig.serializer(), newConfig), true)
    }

    private suspend fun <T> loadJson(configFile: Fs.File, serializer: KSerializer<T>): T? {
        return fs.loadFile(configFile)?.let { plugins.json.decodeFromString(serializer, it) }
    }

    suspend fun loadShow(file: Fs.File): Show? {
        return loadJson(file, ShowMigrator)
    }

    suspend fun saveShow(file: Fs.File, show: Show) {
        file.write(plugins.json.encodeToString(ShowMigrator, show), true)
    }

    suspend fun listShaderLibraries(): List<Fs.File> {
        return resolve("shader-libraries").listFiles()
            .also { println("shader libraries: $it") }
            .filter { it.libraryIndexFile().exists() }
            .also { println("shader libraries with index: $it") }
    }

    suspend fun loadShaderLibraryIndexFile(libDir: Fs.File): ShaderLibraryIndexFile {
        return json.decodeFromString(
            ShaderLibraryIndexFile.serializer(),
            libDir.libraryIndexFile().read()!!
        )
    }

    private fun Fs.File.libraryIndexFile() = resolve("_libraryIndex.json", isDirectory = false)

    fun resolve(path: String): Fs.File = fs.resolve(path)
}
