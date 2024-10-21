package baaahs.scene.migration

import baaahs.migrator.*
import kotlinx.serialization.json.*

/**
 * Grid directions were backwards.
 */
@Suppress("ClassName")
object V1_GridDirectionBackwards : DataMigrator.Migration(1) {
    override fun migrate(from: JsonObject): JsonObject {
        return from.edit {
            replaceJsonObj("model") { model ->
                model.jsonObject.edit {
                    mapObjsInArray("entities") { entity ->
                        entity.apply {
                            if (this.type == "Grid") {
                                // Grid's direction was backwards
                                val wrongDirection = this["direction"]?.jsonPrimitive?.contentOrNull
                                    ?: "ColumnsThenRows"
                                when(wrongDirection) {
                                    "RowsThenColumns" -> this["direction"] = JsonPrimitive("ColumnsThenRows")
                                    "ColumnsThenRows" -> this.remove("direction")
                                    else -> error("Unknown direction \"$wrongDirection\".")
                                }

                                // Grid's zigzag might as well default to true.
                                val oldZigZag = this.remove("zigZag")?.jsonPrimitive?.booleanOrNull ?: false
                                if (!oldZigZag) this["zigZag"] = JsonPrimitive(false)
                            }
                        }
                    }
                }
            }
        }
    }
}