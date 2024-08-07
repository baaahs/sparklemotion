package baaahs.migrator

import baaahs.util.Logger
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.*

open class DataMigrator<T : Any>(
    private val tSerializer: KSerializer<T>,
    private val migrations: List<Migration> = emptyList(),
    private val versionKey: String = "version"
) {
    init {
        val versionDupes = migrations.groupBy { it.toVersion }
            .filter { (_, matching) -> matching.size > 1 }
            .map { (version, _) -> version }
            .sortedBy { it }
        if (versionDupes.isNotEmpty()) {
            throw Error("Duplicate migrations for version(s): ${versionDupes.joinToString(", ")}")
        }
    }
    private val currentVersion = migrations.maxOfOrNull { it.toVersion } ?: 0

    inner class Migrate(
        fileName: String? = null
    ) : JsonTransformingSerializer<T>(tSerializer) {
        private val context = fileName?.let { "$it: " } ?: ""

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

            logger.debug { "${context}Migrating from v$fromVersion:\n$newJson" }

            migrations.forEach { migration ->
                if (fromVersion < migration.toVersion) {
                    logger.info {
                        "${context}Migrating from $fromVersion to ${migration.toVersion} (${migration::class.simpleName})."
                    }
                    newJson = migration.migrate(newJson)
                }
            }

            logger.debug { "${context}Migrated to v$currentVersion:\n$newJson" }

            return newJson.toJsonObj()
        }

        override fun transformSerialize(element: JsonElement): JsonElement {
            if (element !is JsonObject) return element
            return element.toMutableMap().apply {
                put(versionKey, JsonPrimitive(currentVersion))
            }.toJsonObj()
        }
    }

    abstract class Migration(val toVersion: Int) {
        abstract fun migrate(from: JsonObject): JsonObject
    }

    private val logger = Logger<DataMigrator<*>>()
}