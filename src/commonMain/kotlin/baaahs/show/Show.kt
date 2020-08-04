package baaahs.show

import baaahs.ShowState
import baaahs.Surface
import baaahs.camelize
import baaahs.glshaders.Plugins
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerialModuleCollector
import kotlin.reflect.KClass

interface Patchy {
    val title: String
    val patches: List<Patch>
    val eventBindings: List<EventBinding>
    val controlLayout: Map<String, List<ControlRef>>
}

@Serializable
data class Show(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<ControlRef>> = emptyMap(),
    val scenes: List<Scene> = emptyList(),
    val layouts: Layouts = Layouts(),
    val shaders: Map<String, Shader> = emptyMap(),
    val shaderInstances: Map<String, ShaderInstance> = emptyMap(),
    val dataSources: Map<String, DataSource> = emptyMap()
) : Patchy {
    fun toJson(plugins: Plugins): JsonElement {
        return plugins.json.toJson(serializer(), this)
    }

    fun defaultShowState() = ShowState.forShow(this)

    companion object {
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
    override val controlLayout: Map<String, List<ControlRef>> = emptyMap(),
    val patchSets: List<PatchSet> = emptyList()
) : Patchy

@Serializable
data class PatchSet(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<ControlRef>> = emptyMap()
) : Patchy

@Serializable
data class Patch(
    val shaderInstanceIds: List<String>,
    val surfaces: Surfaces
) {
    companion object {
        fun from(editor: PatchEditor, showBuilder: ShowBuilder): Patch {
            return Patch(
                editor.shaderInstances.map { showBuilder.idFor(it.build(showBuilder)) },
                editor.surfaces
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
    /**language=glsl*/
    val src: String
) {
    val title: String
        get() {
            val newlineIndex = src.indexOf("\n")
            return if (newlineIndex < 3 || src.length < 3) {
                "shader"
            } else {
                src.substring(3, newlineIndex)
            }
        }

    fun suggestId(): String = title.camelize()
}

@Serializable
data class ShaderInstance(
    val shaderId: String,
    val incomingLinks: Map<String, PortRef>,
    val shaderChannel: ShaderChannel?
) {
    fun findDataSourceRefs(): List<DataSourceRef> {
        return incomingLinks.values.filterIsInstance<DataSourceRef>()
    }

    companion object {
        fun from(editor: ShaderInstanceEditor, showBuilder: ShowBuilder): ShaderInstance {
            return editor.build(showBuilder)
        }
    }
}

fun buildEmptyShow(): Show {
    return ShowEditor("Untitled").apply {
        addScene("Scene 1") {
            addPatchSet("All Dark") {
            }
        }
        addControl("Scenes", SpecialControl("baaahs.Core:Scenes"))
        addControl("Patches", SpecialControl("baaahs.Core:Patches"))

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
