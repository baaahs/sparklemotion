package baaahs.migrator

import kotlinx.serialization.json.*

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

fun MutableMap<String, JsonElement>.mapObjsInArray(
    key: String,
    callback: (item: MutableMap<String, JsonElement>) -> Unit
) {
    (this[key] as JsonArray?)?.map { obj ->
        val objMap = (obj as JsonObject).toMutableMap()
        callback(objMap)
        objMap.toJsonObj()
    }?.also { this[key] = JsonArray(it) }
}

fun JsonObject.edit(block: MutableMap<String, JsonElement>.() -> Unit) =
    JsonObject(toMutableMap().apply(block))

fun Map<String, JsonElement>.toJsonObj(): JsonObject = buildJsonObject {
    forEach { (k, v) -> put(k, v) }
}

fun MutableMap<String, JsonElement>.replaceJsonObj(name: String, block: (JsonObject) -> JsonElement) {
    val origEl = this[name] ?: buildJsonObject { }
    if (origEl !is JsonObject) error("\"$name\" entry is a ${origEl::class.simpleName}, not an object.")
    this[name] = block(origEl)
}

val JsonElement.type get(): String? =
    this.jsonObject["type"]?.jsonPrimitive?.contentOrNull

val JsonObject.type get(): String? =
    this["type"]?.jsonPrimitive?.contentOrNull

val MutableMap<String, JsonElement>.type get(): String? =
    this["type"]?.jsonPrimitive?.contentOrNull

fun MutableMap<String, JsonElement>.replaceMapValues(
    name: String,
    block: (key: String, value: JsonElement) -> JsonElement
) {
    replaceJsonObj(name) { map ->
        map.edit {
            keys.forEach { key ->
                replaceJsonObj(key) { value -> block(key, value) }
            }
        }
    }
}

fun <T: Any> MutableMap<String, T>.rename(from: String, to: String) {
    remove(from)?.let { put(to, it) }
}