@file:ContextualSerialization(
    Control::class,
    DataSource::class,
    Shader::class,
    ShaderInstance::class
)

package baaahs.show

import baaahs.ShowState
import baaahs.Surface
import baaahs.camelize
import baaahs.plugin.Plugins
import baaahs.show.mutable.*
import baaahs.util.CanonicalizeReferables
import baaahs.util.CanonicalizeReferables.*
import baaahs.util.ReferableWithIdImpl
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class Show(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<Control>> = emptyMap(),
    val scenes: List<Scene> = emptyList(),
    val layouts: Layouts = Layouts() //,
//    val shaders: Map<String, Shader> = emptyMap(),
//    val shaderInstances: Map<String, ShaderInstance> = emptyMap(),
//    val controls: Map<String, Control>  = emptyMap(),
//    val dataSources: Map<String, DataSource> = emptyMap()
) : PatchHolder {
    fun toJson(plugins: Plugins): JsonElement {
        return plugins.json.toJson(canonicalizingSerializer, this)
    }

    fun defaultShowState() = ShowState.forShow(this)

    companion object {
        val canonicalizingSerializer = CanonicalizeReferables(
            serializer(), "Canonicalize Show", listOf(
                ReferenceType("shaders", Shader::class, Shader.serializer()),
                ReferenceType("shaderInstances", ShaderInstance::class, ShaderInstance.serializer()),
                ReferenceType("controls", Control::class, Control.serialModule),
                ReferenceType("dataSources", DataSource::class, DataSource.serialModule)
            )
        )

        fun fromJson(plugins: Plugins, s: String): Show {
            val json = Json(JsonConfiguration.Stable)
            return json.parse(canonicalizingSerializer, s)
        }
    }
}

@Serializable
data class Scene(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<Control>> = emptyMap(),
    val patchSets: List<PatchSet> = emptyList()
) : PatchHolder

@Serializable
data class PatchSet(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<Control>> = emptyMap()
) : PatchHolder

@Serializable
data class Patch(
    val shaderInstances: List<ShaderInstance>,
    val surfaces: Surfaces
)

@Serializable
data class EventBinding(
    val inputType: String,
    val inputData: JsonElement,
    val target: DataSourceSourcePort
)

@Serializable
data class Surfaces(
    val name: String
) {
    fun matches(surface: Surface): Boolean {
        return true
    }

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

@Serializable
data class Shader(
    val title: String,
    val type: ShaderType,
    /**language=glsl*/
    val src: String
) : ReferableWithIdImpl() {
    override fun suggestId(): String = title.camelize()
}

@Serializable
data class ShaderInstance(
    val shader: Shader,
    val incomingLinks: Map<String, SourcePort>,
    val shaderChannel: ShaderChannel = ShaderChannel.Main,
    val priority: Float = 0f
) : ReferableWithIdImpl() {

    fun findDataSourceRefs(): List<DataSourceSourcePort> {
        return incomingLinks.values.filterIsInstance<DataSourceSourcePort>()
    }

    companion object {
        val defaultOrder = compareBy<ShaderInstance>(
            { it.shader.type.priority },
            { it.shader.title }
        )
    }
}

@Serializable(with = ShaderChannel.ShaderChannelSerializer::class)
data class ShaderChannel(val id: String) {
    companion object {
        val Main = ShaderChannel("main")
    }

    class ShaderChannelSerializer :
        KSerializer<ShaderChannel> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveDescriptor(
                "id",
                PrimitiveKind.STRING
            )

        override fun deserialize(decoder: Decoder): ShaderChannel {
            return ShaderChannel(decoder.decodeString())
        }

        override fun serialize(encoder: Encoder, value: ShaderChannel) {
            encoder.encodeString(value.id)
        }
    }
}

fun buildEmptyShow(): Show {
    return MutableShow("Untitled").apply {
        addScene("Scene 1") {
            addPatchSet("All Dark") {
            }
        }
        addControl("Scenes", MutableButtonGroupControl("Scenes"))
        addControl("Patches", MutableButtonGroupControl("Patches"))

        editLayouts {
            copyFrom(
                Layouts(
                    listOf("Scenes", "Patches", "More Controls", "Preview", "Controls"),
                    mapOf("default" to SampleData.defaultLayout)
                )
            )
        }
    }.getShow()
}
