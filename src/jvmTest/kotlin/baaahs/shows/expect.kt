package baaahs.shows

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonElementSerializer
import java.nio.file.Files
import java.nio.file.Paths

actual fun expectJson(
    expected: JsonElement,
    block: () -> JsonElement
) {
    val json = Json(JsonConfiguration.Stable.copy(prettyPrint = true))
    fun JsonElement.toStr() = json.stringify(JsonElementSerializer, this)
    kotlin.test.expect(expected.toStr()) { block().toStr() }
}