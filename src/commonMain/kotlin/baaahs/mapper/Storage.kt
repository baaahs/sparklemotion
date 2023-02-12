package baaahs.mapper

import baaahs.PinkyConfig
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.resourcesFs
import baaahs.libraries.ShaderLibraryIndexFile
import baaahs.plugin.Plugins
import baaahs.scene.OpenScene
import baaahs.scene.Scene
import baaahs.show.SceneMigrator
import baaahs.show.Show
import baaahs.show.ShowMigrator
import baaahs.sim.MergedFs
import baaahs.util.Logger
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

class Storage(val fs: Fs, val plugins: Plugins) {
    val json = Json(plugins.json) { isLenient = true }
    val fsSerializer = FsServerSideSerializer()
    val mappingSessionsDir = fs.resolve("mapping-sessions")
    val imagesDir = mappingSessionsDir.resolve("images")

    private val configFile = fs.resolve("config.json")

    companion object {
        private val logger = Logger("Storage")

        private val format = DateFormat("yyyy''MM''dd'-'HH''mm''ss")

        fun formatDateTime(dateTime: DateTime): String {
            return dateTime.format(format)
        }
    }

    suspend fun listSessions(): List<Fs.File> {
        return buildList {
            mappingSessionsDir.recurse {
                if (it.name.endsWith(".json")) add(it)
            }
        }
    }

    private suspend fun Fs.File.recurse(block: (Fs.File) -> Unit) {
        fs.listFiles(this).forEach {
            if (fs.isDirectory(it)) it.recurse(block) else block(it)
        }
    }

    suspend fun saveSession(mappingSession: MappingSession): Fs.File {
        val name = "${formatDateTime(mappingSession.startedAtDateTime)}-v${mappingSession.version}.json"
        val file = mappingSessionsDir.resolve(name)
        fs.saveFile(file, json.encodeToString(MappingSession.serializer(), mappingSession))
        return file
    }

    suspend fun listImages(sessionName: String?): List<Fs.File> {
        val startingDir = if (sessionName == null) imagesDir else imagesDir.resolve(sessionName)
        return buildList {
            startingDir.recurse {
                if (it.name.endsWith(".webp")) add(it)
            }
        }
    }

    suspend fun saveImage(name: String, imageData: ByteArray) {
        val file = imagesDir.resolve(name)
        fs.saveFile(file, imageData)
    }

    suspend fun loadImage(name: String): ByteArray? {
        val file = imagesDir.resolve(name)
        return fs.loadFile(file)?.let { contents ->
            ByteArray(contents.length) { i ->
                contents[i].code.toByte()
            }
        }
    }

    suspend fun loadMappingData(scene: OpenScene): SessionMappingResults {
        val sessions = arrayListOf<MappingSession>()
        val path = fs.resolve("mapping", scene.model.name)
        fs.listFiles(path).forEach { dir ->
            fs.listFiles(dir)
                .sortedBy { it.name }
                .filter { it.name.endsWith(".json") }
                .forEach { f ->
                    sessions.add(loadMappingSession(f))
                }
        }
        return SessionMappingResults(scene, sessions)
    }

    suspend fun loadMappingSession(name: String): MappingSession {
        val file = mappingSessionsDir.resolve(name)
        return loadMappingSession(file)
    }

    suspend fun loadMappingSession(f: Fs.File): MappingSession {
        val mappingJson = fs.loadFile(f)
            ?: error("No such file \"${f.fullPath}\".")
        val mappingSession = try {
            val json = plugins.json.parseToJsonElement(mappingJson).jsonObject.let {
                // Janky data migration.
                buildJsonObject {
                    it.entries.forEach { (k, v) ->
                        put(k, if (k == "cameraMatrix") {
                            if (v is JsonObject) {
                                v["elements"]!!
                            } else v
                        } else v)
                    }
                }
            }
            plugins.json.decodeFromJsonElement(MappingSession.serializer(), json)
        } catch (e: Exception) {
            throw Exception("Error loading \"${f.fullPath}\": ${e.message}.", e)
        }
        mappingSession.surfaces.forEach { surface ->
            logger.debug { "Found pixel mapping for ${surface.entityName} (${surface.controllerId.name()})" }
        }
        return mappingSession
    }

    suspend fun loadConfig(): PinkyConfig? {
        return loadJson(configFile, PinkyConfig.serializer())
    }

    suspend fun updateConfig(update: PinkyConfig.() -> PinkyConfig) {
        val oldConfig = loadConfig() ?: PinkyConfig(null, null)
        val newConfig = oldConfig.update()
        if (newConfig != oldConfig) {
            configFile.write(json.encodeToString(PinkyConfig.serializer(), newConfig), true)
        }
    }

    private suspend fun <T> loadJson(file: Fs.File, serializer: KSerializer<T>): T? {
        return fs.loadFile(file)?.let { plugins.json.decodeFromString(serializer, it) }
    }

    suspend fun loadScene(file: Fs.File): Scene? {
        return loadJson(file, SceneMigrator)
    }

    suspend fun saveScene(file: Fs.File, scene: Scene) {
        file.write(plugins.json.encodeToString(SceneMigrator, scene), true)
    }

    suspend fun loadShow(file: Fs.File): Show? {
        return loadJson(file, ShowMigrator)
    }

    suspend fun saveShow(file: Fs.File, show: Show) {
        file.write(plugins.json.encodeToString(ShowMigrator, show), true)
    }

    suspend fun listShaderLibraries(): List<Fs.File> {
        return MergedFs(fs, resourcesFs)
            .resolve("shader-libraries").listFiles()
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
