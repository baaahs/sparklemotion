package baaahs.mapper.migration

import baaahs.migrator.DataMigrator
import kotlinx.datetime.Instant
import kotlinx.serialization.json.*

object V1_EpochToIso8601 : DataMigrator.Migration(1) {
    override fun migrate(from: JsonObject): JsonObject =
        from.toMutableMap().apply {
            this["startedAt"]?.let { this["startedAt"] = it.fromMillisToIso8601() }
            this["savedAt"]?.let { this["savedAt"] = it.fromMillisToIso8601() }
        }.let { JsonObject(it) }

    private fun JsonElement.fromMillisToIso8601() =
        JsonPrimitive(
            Instant.fromEpochMilliseconds(
                jsonPrimitive.double.toLong()
            ).toString()
        )
}
