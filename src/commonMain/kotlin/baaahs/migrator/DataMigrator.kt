package baaahs.migrator

import baaahs.util.Logger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

abstract class DataMigrator<T : Any>(
    tSerializer: KSerializer<T>,
    private val migrations: List<Migration>
) : JsonTransformingSerializer<T>(tSerializer) {
    init {
        val versionDupes = migrations.groupBy { it.toVersion }
            .filter { (_, matching) -> matching.size > 1 }
            .map { (version, _) -> version }
            .sortedBy { it }
        if (versionDupes.isNotEmpty()) {
            throw Error("Duplicate migrations for version(s): ${versionDupes.joinToString(", ")}")
        }
    }
    private val currentVersion = migrations.maxOf { it.toVersion }
    private val versionKey = "version"

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

        migrations.forEach { migration ->
            if (fromVersion < migration.toVersion) {
                logger.info {
                    "Migrating from $fromVersion to ${migration.toVersion} (${migration::class.simpleName})."
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

    private val logger = Logger<DataMigrator<*>>()
}