package baaahs.show

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class Layouts(
    val panelNames: List<String> = emptyList(),
    val map: Map<String, Layout> = emptyMap()
)

@Serializable
data class Layout(
    val rootNode: JsonObject
)