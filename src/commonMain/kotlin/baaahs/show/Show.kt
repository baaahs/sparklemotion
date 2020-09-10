package baaahs.show

import baaahs.Surface
import baaahs.camelize
import baaahs.getBang
import baaahs.plugin.Plugins
import baaahs.show.ButtonGroupControl.Direction
import baaahs.show.mutable.*
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerialModuleCollector
import kotlin.reflect.KClass

interface ShowContext {
    fun getControl(id: String): Control
    fun getDataSource(id: String): DataSource
    fun getShader(id: String): Shader
}

@Serializable
data class Show(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap(),
    val scenes: List<Scene> = emptyList(),
    val layouts: Layouts = Layouts(),
    val shaders: Map<String, Shader> = emptyMap(),
    val shaderInstances: Map<String, ShaderInstance> = emptyMap(),
    val controls: Map<String, Control>  = emptyMap(),
    val dataSources: Map<String, DataSource> = emptyMap()
) : PatchHolder, ShowContext {
    fun toJson(plugins: Plugins): JsonElement {
        return plugins.json.toJson(serializer(), this)
    }

    override fun getControl(id: String): Control = controls.getBang(id, "control")

    override fun getDataSource(id: String): DataSource = dataSources.getBang(id, "data source")

    override fun getShader(id: String): Shader = shaders.getBang(id, "shader")

    companion object {
        val EmptyShow = Show("Empty Show")

        fun fromJson(plugins: Plugins, s: String): Show {
            val json = Json(JsonConfiguration.Stable, context = ShowSerialModule(plugins.serialModule))
            return json.parse(serializer(), s)
        }
    }
}

class ShowSerialModule(private val delegate: SerialModule) : SerialModule by delegate {
    override fun dumpTo(collector: SerialModuleCollector) {
        println("dumpTo($collector)")
        delegate.dumpTo(collector)
    }

    override fun <T : Any> getContextual(kclass: KClass<T>): KSerializer<T>? {
        println("getContextual($kclass)")
        return delegate.getContextual(kclass)
    }
}

@Serializable
data class Scene(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap(),
    val patchSets: List<PatchSet> = emptyList()
) : PatchHolder

@Serializable
data class PatchSet(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap()
) : PatchHolder

@Serializable
data class Patch(
    val shaderInstanceIds: List<String>,
    val surfaces: Surfaces
) {
    companion object {
        fun from(mutablePatch: MutablePatch, showBuilder: ShowBuilder): Patch {
            return Patch(
                mutablePatch.mutableShaderInstances.map { showBuilder.idFor(it.build(showBuilder)) },
                mutablePatch.surfaces
            )
        }
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
) {
    fun suggestId(): String = title.camelize()
}

@Serializable
data class ShaderInstance(
    val shaderId: String,
    val incomingLinks: Map<String, PortRef>,
    val shaderChannel: ShaderChannel = ShaderChannel.Main,
    val priority: Float = 0f
) {

    fun findDataSourceRefs(): List<DataSourceRef> {
        return incomingLinks.values.filterIsInstance<DataSourceRef>()
    }

    companion object {
        fun from(mutableShaderInstance: MutableShaderInstance, showBuilder: ShowBuilder): ShaderInstance {
            return mutableShaderInstance.build(showBuilder)
        }
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
