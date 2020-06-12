package baaahs.show

import baaahs.Surface
import baaahs.glshaders.Plugins
import baaahs.ports.DataSourceRef
import baaahs.ports.Link
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class Show(
    val title: String,
    val scenes: List<Scene>,
    val patchSets: List<PatchSet>,
    val eventBindings: List<EventBinding>,
    val dataSources: List<DataSource>,
    val layouts: Layouts,
    val controlLayout: Map<String, List<DataSourceRef>>,
    val shaderFragments: Map<String, String>
) {
    fun toJson(plugins: Plugins): JsonElement {
        return plugins.json.toJson(serializer(), this)
    }

    companion object {
        fun fromJson(plugins: Plugins, s: String): Show {
            return plugins.json.parse(serializer(), s)
        }
    }
}

@Serializable
data class Scene(
    val title: String,
    val patchSets: List<PatchSet>,
    val eventBindings: List<EventBinding>,
    val controlLayout: Map<String, List<DataSourceRef>>
)

@Serializable
data class PatchSet(
    val title: String,
    val patchMappings: List<PatchMapping>,
    val eventBindings: List<EventBinding>,
    val controlLayout: Map<String, List<DataSourceRef>>
)

@Serializable
data class PatchMapping(
    val links: List<Link>,
    val surfaces: Surfaces
) {
    fun matches(surface: Surface): Boolean {
        return true
    }
}

@Serializable
data class EventBinding(
    val inputType: String,
    val inputData: JsonElement,
    val target: DataSourceRef
)

@Serializable
data class Surfaces(
    val name: String
) {
    companion object {
        val AllSurfaces = Surfaces("All Surfaces")
    }
}

@Serializable
data class Layouts(
    val panelNames: List<String>,
    val map: Map<String, Layout>
)

@Serializable
data class Layout(
    val rootNode: JsonObject
)