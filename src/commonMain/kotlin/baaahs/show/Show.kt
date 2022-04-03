@file:UseContextualSerialization(DataSource::class)

package baaahs.show

import baaahs.app.ui.editor.Editable
import baaahs.camelize
import baaahs.device.FixtureType
import baaahs.fixtures.Fixture
import baaahs.getBang
import baaahs.plugin.Plugins
import baaahs.show.mutable.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.UseContextualSerialization
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

@Serializable
data class Show(
    override val title: String,
    override val patchIds: List<String> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap(),
    val layouts: Layouts = Layouts(),
    val shaders: Map<String, Shader> = emptyMap(),
    val patches: Map<String, Patch> = emptyMap(),
    val controls: Map<String, Control>  = emptyMap(),
    val dataSources: Map<String, DataSource> = emptyMap()
) : PatchHolder, Editable {
    init { validatePatchHolder() }

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
    val shaderId: String,
    val incomingLinks: Map<String, PortRef>,
    val shaderChannel: ShaderChannel = ShaderChannel.Main,
    val priority: Float = 0f

    // TODO: Fixture matcher (previously called "Surfaces") will eventually go here.
)

fun <T> List<T>.assertNoDuplicates(items: String = "items") {
    val duplicates = groupBy { it }.mapValues { (_, v) -> v.size }.filterValues { it > 1 }
    if (duplicates.isNotEmpty()) {
        error("duplicate $items: $duplicates")
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
    /** Set of [FixtureType]s that this object matches. If the set is empty, every FixtureType matches. */
    @Contextual
    val fixtureTypes: Set<FixtureType> = emptySet()
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
data class Shader(
    val title: String,
    /**language=glsl*/
    val src: String
) {
    fun suggestId(): String = title.camelize()
}

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
            editLayout("default") {
                editTab("Main") {
                    columns.add(MutableLayoutDimen.decode("1fr"))
                    rows.add(MutableLayoutDimen.decode("1fr"))
                    areas.add(findOrCreatePanel("Controls"))
                }
            }
        }
    }.getShow()
}
