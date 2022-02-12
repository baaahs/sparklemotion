package baaahs.show.migration

import baaahs.show.DataMigrator
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Suppress("ClassName")
object V2_RemoveShaderType : DataMigrator.Migration(2) {
    override fun migrate(from: JsonObject): JsonObject {
        return from.toMutableMap().apply {
            mapObjsInDict("shaders") { _, shader ->
                shader.remove("type")?.jsonPrimitive?.contentOrNull
            }
        }.toJsonObj()
    }
}