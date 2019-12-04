package baaahs.mapper;

import baaahs.Logger
import baaahs.Model
import baaahs.io.Fs
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class Storage(val fs: Fs) {

    companion object {
        private val logger = Logger("Storage")
        val json = Json(JsonConfiguration.Stable.copy(strictMode = false))

        private val format = DateFormat("yyyy''MM''dd'-'HH''mm''ss")

        fun formatDateTime(dateTime: DateTime): String {
            return dateTime.format(format)
        }
    }

    fun listSessions(): List<String> {
        return fs.listFiles("mapping-sessions").filter { it.endsWith(".json") }
    }

    fun saveSession(mappingSession: MappingSession) {
        fs.createFile(
            "mapping-sessions/${formatDateTime(mappingSession.startedAtDateTime)}-v${mappingSession.version}.json",
            json.stringify(MappingSession.serializer(), mappingSession)
        )
    }

    fun saveImage(name: String, imageData: ByteArray) {
        fs.createFile("mapping-sessions/images/$name", imageData)
    }

    fun loadMappingData(model: Model<*>): MappingResults {
        val sessions = arrayListOf<MappingSession>()
        val path = "mapping/${model.name}"
        fs.listFiles(path).forEach { dir ->
            fs.listFiles("$path/$dir").sorted().filter { it.endsWith(".json") }.forEach { f ->
                val mappingJson = fs.loadFile("$path/$dir/$f")
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
