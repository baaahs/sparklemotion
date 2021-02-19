package baaahs.show.migration

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject

fun MutableMap<String, JsonElement>.mapObjsInDict(
    key: String,
    callback: (id: String, item: MutableMap<String, JsonElement>) -> Unit
) {
    (this[key] as JsonObject?)?.mapValues { (id, obj) ->
        val objMap = (obj as JsonObject).toMutableMap()
        callback(id, objMap)
        objMap.toJsonObj()
    }?.also { this[key] = it.toJsonObj() }
}

fun Map<String, JsonElement>.toJsonObj(): JsonObject = buildJsonObject {
    forEach { (k, v) -> put(k, v) }
}

fun MutableMap<String, JsonElement>.replaceJsonObj(name: String, block: (JsonObject) -> JsonElement) {
    this[name] = block((this[name] ?: buildJsonObject { }) as JsonObject)
}
