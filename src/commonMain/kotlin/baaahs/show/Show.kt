package baaahs.show

import baaahs.SparkleMotion
import baaahs.app.ui.Editable
import baaahs.camelize
import baaahs.fixtures.DeviceType
import baaahs.fixtures.Fixture
import baaahs.getBang
import baaahs.plugin.Plugins
import baaahs.show.ButtonGroupControl.Direction
import baaahs.show.mutable.MutableShaderChannel
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.MutableShowVisitor
import baaahs.show.mutable.VisitationLog
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

@Serializable
data class Show(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap(),
    val layouts: Layouts = Layouts(),
    val shaders: Map<String, Shader> = emptyMap(),
    val shaderInstances: Map<String, ShaderInstance> = emptyMap(),
    val controls: Map<String, Control>  = emptyMap(),
    val dataSources: Map<String, DataSource> = emptyMap()
) : PatchHolder, Editable {
    fun toJson(json: Json): JsonElement {
        return json.encodeToJsonElement(serializer(), this)
    }

    fun getControl(id: String): Control = controls.getBang(id, "control")

    fun getDataSource(id: String): DataSource = dataSources.getBang(id, "data source")

    fun getShader(id: String): Shader = shaders.getBang(id, "shader")

    companion object {
        val EmptyShow = Show("Empty Show")

        fun fromJson(plugins: Plugins, s: String): Show {
            val json = Json { serializersModule = plugins.serialModule }
            return json.decodeFromString(serializer(), s)
        }
    }
}

@Serializable
data class Patch(
    val shaderInstanceIds: List<String>,
    val surfaces: Surfaces
) {
    init { if (SparkleMotion.EXTRA_ASSERTIONS) shaderInstanceIds.assertNoDuplicates() }
}

fun <T> List<T>.assertNoDuplicates(items: String = "items") {
    val duplicates = groupBy { it }.mapValues { (_, v) -> v.size }.filterValues { it > 1 }
    if (duplicates.isNotEmpty()) {
        throw error("duplicate $items: $duplicates")
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
    val name: String,
    /** Set of [DeviceType]s that this object matches. If the set is empty, every DeviceType matches. */
    @Contextual
    val deviceTypes: Set<DeviceType> = emptySet()
) {
    fun matches(fixture: Fixture): Boolean {
        return true
    }

    fun accept(visitor: MutableShowVisitor, log: VisitationLog = VisitationLog()) {
        if (log.surfaces.add(this)) visitor.visit(this)
    }

    companion object {
        val AllSurfaces = Surfaces("All Surfaces", emptySet())
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
    /**language=glsl*/
    val src: String
) {
    fun suggestId(): String = title.camelize()
}

@Serializable
data class ShaderInstance(
    val shaderId: String,
    val incomingLinks: Map<String, PortRef>,
    val shaderChannel: ShaderChannel = ShaderChannel.Main,
    val priority: Float = 0f
)

@Serializable(with = ShaderChannel.ShaderChannelSerializer::class)
data class ShaderChannel(val id: String) {
    fun toMutable(): MutableShaderChannel = MutableShaderChannel(id)

    companion object {
        val Main = ShaderChannel("main")
    }

    class ShaderChannelSerializer :
        KSerializer<ShaderChannel> {
        override val descriptor: SerialDescriptor
            get() = PrimitiveSerialDescriptor(
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
        editLayouts {
            copyFrom(
                Layouts(
                    listOf("Scenes", "Patches", "More Controls", "Preview", "Controls"),
                    mapOf("default" to SampleData.defaultLayout)
                )
            )
        }

        addButtonGroup(
            "Scenes", "Scenes", Direction.Horizontal
        ) {
            addButton("Scene 1") {
                addButtonGroup(
                    "Backdrops", "Backdrops", Direction.Vertical
                ) {
                    addButton("All Dark") {
                    }
                }
            }
        }
    }.getShow()
}
