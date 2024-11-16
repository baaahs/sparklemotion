package baaahs.scene.migration

import baaahs.camelize
import baaahs.migrator.*
import baaahs.model.Model
import baaahs.util.UniqueIds
import kotlinx.serialization.json.*

/**
 * Grid directions were backwards.
 */
@Suppress("ClassName")
object V2_ModelEntityIds : DataMigrator.Migration(2) {
    override fun migrate(from: JsonObject): JsonObject {
        val entities = UniqueIds<Any>()

        return from.edit {
            replaceJsonObj("model") { model ->
                model.jsonObject.edit {
                    (this["entities"] as JsonArray?)?.associate { obj ->
                        val objMap = (obj as JsonObject).toMap()
                        val id = objMap["title"]?.jsonPrimitive?.contentOrNull?.camelize()
                            .ifBlank { objMap["type"]?.jsonPrimitive?.contentOrNull?.camelize() }
                            .ifBlank { "entity" }!!
                        entities.idFor(obj) { id } to obj
                    }?.also { this["entities"] = JsonObject(it) }
                }
            }
        }
    }
}

fun String?.ifBlank(block: () -> String?): String? {
    return if (isNullOrBlank()) block() else this
}