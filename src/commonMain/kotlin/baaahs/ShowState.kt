package baaahs

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class ShowState(
    val controls: Map<String, Map<String, JsonElement>>
)