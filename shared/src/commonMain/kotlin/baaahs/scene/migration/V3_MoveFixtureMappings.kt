package baaahs.scene.migration

import baaahs.migrator.DataMigrator
import baaahs.migrator.edit
import baaahs.migrator.editEachJsonObj
import baaahs.migrator.editJsonObj
import baaahs.migrator.toJsonObj
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.jsonArray

/**
 * Grid directions were backwards.
 */
@Suppress("ClassName")
object V3_MoveFixtureMappings : DataMigrator.Migration(3) {
    override fun migrate(from: JsonObject): JsonObject {
        val fixtureMappingsByControllerId = mutableMapOf<String, MutableList<JsonElement>>()

        return from.edit {
            // Migrate fixture mappings from within controllers to top-level map.
            editJsonObj("controllers") { controllers ->
                controllers.editEachJsonObj { controllerId, controller ->
                    val fixtureMappings = controller.remove("fixtures")?.jsonArray
                    if (fixtureMappings != null && fixtureMappings.isNotEmpty()) {
                        val controllerFixtureMappings =
                            fixtureMappingsByControllerId.getOrPut(controllerId) { mutableListOf() }
                        fixtureMappings.forEach { controllerFixtureMappings.add(it) }
                    }

                    val defaultFixtureConfig = controller.remove("defaultFixtureConfig")
                    if (defaultFixtureConfig != null) {
                        controller["defaultFixtureOptions"] = defaultFixtureConfig
                    }
                }
            }

            this["fixtureMappings"] = fixtureMappingsByControllerId
                .mapValues { (k, v) -> JsonArray(v) }
                .toJsonObj()
        }
    }
}