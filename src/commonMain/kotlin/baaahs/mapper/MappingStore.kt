package baaahs.mapper

import baaahs.io.Fs
import baaahs.plugin.Plugins
import baaahs.scene.OpenScene
import baaahs.util.Clock
import baaahs.util.Logger
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.jsonObject

class MappingStore(
    private val dataDir: Fs.File,
    private val plugins: Plugins,
    private val clock: Clock
) {
    val json = Json(plugins.json) { isLenient = true }
    val mappingSessionsDir = dataDir.resolve("mapping-sessions")
    val imagesDir = mappingSessionsDir.resolve("images")

    companion object {
        private val logger = Logger<MappingStore>()

        fun formatDateTime(dateTime: Instant, timeZone: TimeZone): String {
            return dateTime.toLocalDateTime(timeZone).toString()
                .replace(Regex(".\\d+$"), "")
                .replace(Regex("[-:]"), "")
                .replace("T", "-")
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
        val name = "${formatDateTime(mappingSession.startedAt, clock.tz())}-v${mappingSession.version}.json"
        val file = mappingSessionsDir.resolve(name)
        file.write(json.encodeToString(MappingSession.serializer(), mappingSession))
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
        file.write(imageData, allowOverwrite = true)
    }

    suspend fun loadImage(name: String): ByteArray? {
        val file = imagesDir.resolve(name)
        return file.read()?.let { contents ->
            ByteArray(contents.length) { i ->
                contents[i].code.toByte()
            }
        }
    }

    suspend fun loadMappingData(scene: OpenScene): SessionMappingResults {
        return SessionMappingResults(scene, buildList {
            dataDir.resolve("mapping", scene.model.name).listFiles()
                .flatMap { it.listFiles() }
                .sortedBy { it.name }
                .filter { it.name.endsWith(".json") }
                .forEach { f ->
                    add(loadMappingSession(f))
                }
        })
    }

    suspend fun loadMappingSession(name: String): MappingSession {
        val file = mappingSessionsDir.resolve(name)
        return loadMappingSession(file)
    }

    suspend fun loadMappingSession(f: Fs.File): MappingSession {
        val mappingJson = f.read()
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
}