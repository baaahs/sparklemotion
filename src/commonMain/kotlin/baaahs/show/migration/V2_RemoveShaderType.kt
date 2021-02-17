package baaahs.show.migration

import baaahs.show.ShowMigrator
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Suppress("ClassName")
object V2_RemoveShaderType : ShowMigrator.Migration(2) {
    override fun apply(newJson: MutableMap<String, JsonElement>) {
        newJson.mapObjsInDict("shaders") { _, shader ->
            shader.remove("type")?.jsonPrimitive?.contentOrNull
        }
    }
}