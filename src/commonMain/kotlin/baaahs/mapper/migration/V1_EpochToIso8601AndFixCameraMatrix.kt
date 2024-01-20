package baaahs.mapper.migration

import baaahs.migrator.DataMigrator
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*

object V1_EpochToIso8601AndFixCameraMatrix : DataMigrator.Migration(1) {
    override fun migrate(from: JsonObject): JsonObject =
        from.toMutableMap().apply {
            replace("startedAt") { it.fromMillisToIso8601() }
            replace("savedAt") { it.fromMillisToIso8601() }

            replace("cameraMatrix") {
                if (it is JsonObject) it["elements"]!! else it
            }
        }.let { JsonObject(it) }

    private fun MutableMap<String, JsonElement>.replace(
        key: String,
        block: (JsonElement) -> JsonElement
    ) {
        this[key]?.let { this[key] = block(it) }
    }

    private fun JsonElement.fromMillisToIso8601() =
        JsonPrimitive(
            Instant.fromEpochMilliseconds(
                jsonPrimitive.double.toLong()
            ).toString()
        )
}
