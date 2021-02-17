package baaahs.show

import baaahs.show.migration.AllMigrations
import baaahs.show.migration.toJsonObj
import baaahs.util.Logger
import kotlinx.serialization.json.*

object ShowMigrator : JsonTransformingSerializer<Show>(Show.serializer()) {
    private const val currentVersion = 2
    private const val versionKey = "version"

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        val fromVersion = element[versionKey]?.jsonPrimitive?.int ?: 0
        if (fromVersion == currentVersion)
            return element.toMutableMap()
                .apply { remove(versionKey) }.toJsonObj()

        val newJson = element.toMutableMap()
        newJson.remove(versionKey)

        AllMigrations.forEach { migration ->
            if (fromVersion < migration.toVersion) {
                logger.info {
                    "Migrating show from $fromVersion to ${migration.toVersion} (${migration::class.simpleName})."
                }
                migration.apply(newJson)
            }
        }

        return newJson.toJsonObj()
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        return element.toMutableMap().apply {
            put(versionKey, JsonPrimitive(currentVersion))
        }.toJsonObj()
    }

    abstract class Migration(val toVersion: Int) {
        abstract fun apply(newJson: MutableMap<String, JsonElement>)
    }

    private val logger = Logger<ShowMigrator>()
}