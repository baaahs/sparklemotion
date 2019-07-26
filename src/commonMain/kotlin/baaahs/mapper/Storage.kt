package baaahs.mapper;

import baaahs.io.Fs
import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTime
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration

class Storage(val fs: Fs) {

    companion object {
        val json = Json(JsonConfiguration.Stable)

        private val format = DateFormat("yyyy''MM''dd'-'HH''mm''ss")

        fun formatDateTime(dateTime: DateTime): String {
            return dateTime.format(format)
        }
    }

    fun saveSession(mappingSession: MappingSession) {
        fs.createFile(
            "mapping-sessions/${formatDateTime(mappingSession.startedAtDateTime)}.json",
            json.stringify(MappingSession.serializer(), mappingSession)
        )
    }

    fun saveImage(name: String, imageData: ByteArray) {
        fs.createFile("mapping-sessions/images/$name", imageData)
    }
}
