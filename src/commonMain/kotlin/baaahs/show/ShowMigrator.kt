package baaahs.show

import baaahs.show.migration.AllMigrations
import baaahs.show.migration.toJsonObj
import baaahs.util.Logger
import kotlinx.serialization.json.*

object ShowMigrator : JsonTransformingSerializer<Show>(Show.serializer()) {
    private val allMigrations = AllMigrations
    private val currentVersion = allMigrations.maxOf { it.toVersion }
    private const val versionKey = "version"

    override fun transformDeserialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        val fromVersion = element[versionKey]?.jsonPrimitive?.int ?: 0
        if (fromVersion == currentVersion)
            return element.toMutableMap()
                .apply { remove(versionKey) }.toJsonObj()

        var newJson = element
            .toMutableMap().apply {
                remove(versionKey)
            }.toJsonObj()

        logger.debug { "Migrating from v$fromVersion:\n$newJson" }

        allMigrations.forEach { migration ->
            if (fromVersion < migration.toVersion) {
                logger.info {
                    "Migrating show from $fromVersion to ${migration.toVersion} (${migration::class.simpleName})."
                }
                newJson = migration.migrate(newJson)
            }
        }

        logger.debug { "Migrated to v$currentVersion:\n$newJson" }

        return newJson.toJsonObj()
    }

    override fun transformSerialize(element: JsonElement): JsonElement {
        if (element !is JsonObject) return element
        return element.toMutableMap().apply {
            put(versionKey, JsonPrimitive(currentVersion))
        }.toJsonObj()
    }

    abstract class Migration(val toVersion: Int) {
        abstract fun migrate(from: JsonObject): JsonObject
    }

    private val logger = Logger<ShowMigrator>()
}