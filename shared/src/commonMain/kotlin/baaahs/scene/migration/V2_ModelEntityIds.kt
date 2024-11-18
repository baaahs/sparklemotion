package baaahs.scene.migration

import baaahs.camelize
import baaahs.migrator.DataMigrator
import baaahs.migrator.edit
import baaahs.migrator.replaceJsonObj
import baaahs.migrator.toJsonObj
import baaahs.model.EntityId
import baaahs.util.UniqueIds
import kotlinx.serialization.json.*

/**
 * Grid directions were backwards.
 */
@Suppress("ClassName")
object V2_ModelEntityIds : DataMigrator.Migration(2) {
    override fun migrate(from: JsonObject): JsonObject {
        val entityIds = UniqueIds<Any>()
        val entities = mutableMapOf<EntityId, JsonObject>()
        val entityIdsByName = mutableMapOf<String, EntityId>()

        return from.edit {
            // Migrate entities to dictionary in the scene.
            replaceJsonObj("model") { model ->
                model.jsonObject.edit {
                    (this["entities"] as JsonArray?)?.forEach { obj ->
                        val objMap = (obj as JsonObject).toMap()
                        val title = objMap["title"]?.jsonPrimitive?.contentOrNull
                        val suggestedId = title?.camelize()
                            .ifBlank { objMap["type"]?.jsonPrimitive?.contentOrNull?.camelize() }
                            .ifBlank { "entity" }!!
                        val id = entityIds.idFor(obj) { suggestedId }
                        entities[id] = obj
                        title?.let { entityIdsByName[it] = id }
                    }
                    this.remove("entities")
                }
            }
            this["entities"] = JsonObject(entities)

            // Migrate controller fixtures to use entity ids rather than entity names.
            replaceJsonObj("controllers") { controllers ->
                controllers.mapValues { (_, controllerData) ->
                    controllerData.jsonObject.edit {
                        val newFixtures = this["fixtures"]?.jsonArray?.map { fixture ->
                            fixture.jsonObject.edit {
                                val entityName = this["entityId"]?.jsonPrimitive?.contentOrNull
                                if (entityName != null) {
                                    val entityId = entityIdsByName[entityName]
                                        ?: error("No entity with name \"$entityName\" found.")
                                    this["entityId"] = JsonPrimitive(entityId)
                                }
                            }
                        }
                        if (newFixtures != null)
                            this["fixtures"] = JsonArray(newFixtures)
                    }
                }.toJsonObj()
            }
        }
    }
}

fun String?.ifBlank(block: () -> String?): String? {
    return if (isNullOrBlank()) block() else this
}