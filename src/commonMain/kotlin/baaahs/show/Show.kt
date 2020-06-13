package baaahs.show

import baaahs.Surface
import baaahs.glshaders.Plugins
import baaahs.ports.DataSourceRef
import baaahs.ports.Link
import baaahs.ports.ShaderPortRef
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class Show(
    val title: String,
    val scenes: List<Scene> = emptyList(),
    val eventBindings: List<EventBinding> = emptyList(),
    val dataSources: List<DataSource> = emptyList(),
    val layouts: Layouts = Layouts(),
    val controlLayout: Map<String, List<DataSourceRef>> = emptyMap(),
    val shaderFragments: Map<String, String> = emptyMap()
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
    val patchSets: List<PatchSet> = emptyList(),
    val eventBindings: List<EventBinding> = emptyList(),
    val controlLayout: Map<String, List<DataSourceRef>> = emptyMap()
)

@Serializable
data class PatchSet(
    val title: String,
    val patchMappings: List<PatchMapping> = emptyList(),
    val eventBindings: List<EventBinding> = emptyList(),
    val controlLayout: Map<String, List<DataSourceRef>> = emptyMap()
)

@Serializable
data class PatchMapping(
    val links: List<Link>,
    val surfaces: Surfaces
) {
    fun getShaderIds(): List<String> {
        return (links.mapNotNull { (_, to) ->
            if (to is ShaderPortRef) to.shaderId else null
        } + links.mapNotNull { (from, _) ->
            if (from is ShaderPortRef) from.shaderId else null
        }).distinct()
    }

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
    val panelNames: List<String> = emptyList(),
    val map: Map<String, Layout> = emptyMap()
)

@Serializable
data class Layout(
    val rootNode: JsonObject
)