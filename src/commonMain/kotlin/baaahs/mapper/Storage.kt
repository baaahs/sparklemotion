package baaahs.mapper;

import baaahs.Logger
import baaahs.PinkyConfig
import baaahs.glshaders.Plugins
import baaahs.io.Fs
import baaahs.model.Model
import baaahs.show.Show
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class Storage(val fs: Fs, val plugins: Plugins) {

    companion object {
        private val logger = Logger("Storage")
        val json = Json(JsonConfiguration.Stable.copy(isLenient = true))

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
                "${formatDateTime(mappingSession.startedAtDateTime)}-v${mappingSession.version}.json")
        fs.saveFile(file, json.stringify(MappingSession.serializer(), mappingSession))
        return file
    }

    suspend fun saveImage(name: String, imageData: ByteArray) {
        val file = fs.resolve("mapping-sessions", "images", name)
        fs.saveFile(file, imageData)
    }

    suspend fun loadMappingData(model: Model<*>): MappingResults {
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

    suspend fun loadConfig(): PinkyConfig? {
        return loadJson(fs.resolve("config.json"), PinkyConfig.serializer())
    }

    private suspend fun <T> loadJson(configFile: Fs.File, serializer: KSerializer<T>): T? {
        return fs.loadFile(configFile)?.let { plugins.json.parse(serializer, it) }
    }

    suspend fun loadShow(file: Fs.File): Show? {
        return loadJson(file, Show.serializer())
    }

    suspend fun saveShow(file: Fs.File, show: Show) {
        file.write(plugins.json.stringify(Show.serializer(), show), true)
    }

    fun resolve(path: String): Fs.File = fs.resolve(path)
}
