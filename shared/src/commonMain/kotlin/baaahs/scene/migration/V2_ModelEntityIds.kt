package baaahs.scene.migration

import baaahs.camelize
import baaahs.migrator.DataMigrator
import baaahs.migrator.edit
import baaahs.migrator.replaceJsonObj
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

        return from.edit {
            replaceJsonObj("model") { model ->
                model.jsonObject.edit {
                    (this["entities"] as JsonArray?)?.forEach { obj ->
                        val objMap = (obj as JsonObject).toMap()
                        val suggestedId = objMap["title"]?.jsonPrimitive?.contentOrNull?.camelize()
                            .ifBlank { objMap["type"]?.jsonPrimitive?.contentOrNull?.camelize() }
                            .ifBlank { "entity" }!!
                        val id = entityIds.idFor(obj) { suggestedId }
                        entities[id] = obj
                    }
                    this.remove("entities")
                }
            }
            this["entities"] = JsonObject(entities)
        }
    }
}

fun String?.ifBlank(block: () -> String?): String? {
    return if (isNullOrBlank()) block() else this
}