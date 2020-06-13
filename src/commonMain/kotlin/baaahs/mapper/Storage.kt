package baaahs.mapper;

import baaahs.Logger
import baaahs.io.Fs
import baaahs.model.Model
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class Storage(val fs: Fs) {

    companion object {
        private val logger = Logger("Storage")
        val json = Json(JsonConfiguration.Stable.copy(isLenient = true))

        private val format = DateFormat("yyyy''MM''dd'-'HH''mm''ss")

        fun formatDateTime(dateTime: DateTime): String {
            return dateTime.format(format)
        }
    }

    fun listSessions(): List<Fs.File> {
        val mappingSessionsDir = fs.resolve("mapping-sessions")
        return fs.listFiles(mappingSessionsDir).filter { it.name.endsWith(".json") }
    }

    fun saveSession(mappingSession: MappingSession): Fs.File {
        val file =
            fs.resolve(
                "mapping-sessions",
                "${formatDateTime(mappingSession.startedAtDateTime)}-v${mappingSession.version}.json")
        fs.saveFile(file, json.stringify(MappingSession.serializer(), mappingSession))
        return file
    }

    fun saveImage(name: String, imageData: ByteArray) {
        val file = fs.resolve("mapping-sessions", "images", name)
        fs.saveFile(file, imageData)
    }

    fun loadMappingData(model: Model<*>): MappingResults {
        val sessions = arrayListOf<MappingSession>()
        val path = fs.resolve("mapping", model.name)
        fs.listFiles(path).forEach { dir ->
            fs.listFiles(dir)
                .sortedBy { it.name }
                .filter { it.name.endsWith(".json") }
                .forEach { f ->
                    val mappingJson = fs.loadFile(f)
                    val mappingSession = json.parse(MappingSession.serializer(), mappingJson!!)
                    mappingSession.surfaces.forEach { surface ->
                        logger.debug { "Found pixel mapping for ${surface.panelName} (${surface.brainId})" }
                    }
                    sessions.add(mappingSession)
                }
        }
        return SessionMappingResults(model, sessions)
    }
}
