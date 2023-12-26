package baaahs.mapper

import baaahs.io.Fs
import baaahs.plugin.Plugins
import baaahs.scene.OpenScene
import baaahs.util.Logger
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

class Storage(private val fs: Fs, val plugins: Plugins) {
    val dataDir = fs.resolve(".")
    val json = Json(plugins.json) { isLenient = true }
    val mappingSessionsDir = fs.resolve("mapping-sessions")
    val imagesDir = mappingSessionsDir.resolve("images")

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
        fs.saveFile(file, imageData, allowOverwrite = true)
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
        fs.listFiles(path)
            .flatMap { fs.listFiles(it) }
            .sortedBy { it.name }
            .filter { it.name.endsWith(".json") }
            .forEach { f ->
                sessions.add(loadMappingSession(f))
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

    private suspend fun <T> loadJson(file: Fs.File, serializer: KSerializer<T>): T? {
        return fs.loadFile(file)?.let { plugins.json.decodeFromString(serializer, it) }
    }

    fun resolve(path: String): Fs.File = fs.resolve(path)
}
