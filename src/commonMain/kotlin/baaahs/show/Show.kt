package baaahs.show

import baaahs.Surface
import baaahs.camelize
import baaahs.getBang
import baaahs.glshaders.Plugins
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerialModule
import kotlinx.serialization.modules.SerialModuleCollector
import kotlin.reflect.KClass

interface Controllables {
    val eventBindings: List<EventBinding>
    val controlLayout: Map<String, List<DataSourceRef>>
}

@Serializable
data class Show(
    val title: String,
    val scenes: List<Scene> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    val layouts: Layouts = Layouts(),
    override val controlLayout: Map<String, List<DataSourceRef>> = emptyMap(),
    val shaders: Map<String, Shader> = emptyMap(),
    val dataSources: Map<String, DataSource> = emptyMap()
) : Controllables {
    fun toJson(plugins: Plugins): JsonElement {
        return plugins.json.toJson(serializer(), this)
    }

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
    val title: String,
    val patchSets: List<PatchSet> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<DataSourceRef>> = emptyMap()
) : Controllables {
    fun findShaderPortRefs(): Set<ShaderPortRef> = patchSets.flatMap { it.findShaderPortRefs() }.toSet()
}

@Serializable
data class PatchSet(
    val title: String,
    val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<DataSourceRef>> = emptyMap()
) : Controllables {
    fun findDataSourceRefs(): Set<DataSourceRef> = patches.flatMap { it.findDataSourceRefs() }.toSet()
    fun findShaderPortRefs(): Set<ShaderPortRef> = patches.flatMap { it.findShaderPortRefs() }.toSet()
}

@Serializable
data class Patch(
    val links: List<Link>,
    val surfaces: Surfaces
) {
    fun findDataSourceRefs(): Set<DataSourceRef> {
        return links.map { it.from }.filterIsInstance<DataSourceRef>().toSet()
    }

    fun findShaderPortRefs(): Set<ShaderPortRef> {
        val fromShaders = links.map { it.from }.filterIsInstance<ShaderPortRef>()
        val toShaders = links.map { it.to }.filterIsInstance<ShaderPortRef>()
        return (fromShaders + toShaders).toSet()
    }

    fun findShaderRefs(): Set<String> {
        return findShaderPortRefs().map { it.shaderId }.toSet()
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
data class Link(
    val from: PortRef,
    val to: PortRef
)

@Serializable
sealed class PortRef {
    infix fun linkTo(other: PortRef): Link = Link(this, other)

    abstract fun dereference(showEditor: ShowEditor): LinkEditor.Port
}

interface ShaderPortRef {
    val shaderId: String
}

@Serializable @SerialName("datasource")
data class DataSourceRef(val dataSourceId: String) : PortRef() {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        showEditor.dataSources.getBang(dataSourceId, "datasource")
}

@Serializable @SerialName("shader-in")
data class ShaderInPortRef(override val shaderId: String, val portId: String) : PortRef(), ShaderPortRef {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        showEditor.shaders.getBang(shaderId, "shader").inputPort(portId)
}

@Serializable @SerialName("shader-out")
data class ShaderOutPortRef(override val shaderId: String, val portId: String) : PortRef(), ShaderPortRef {
    fun isReturnValue() = portId == ReturnValue

    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        showEditor.shaders.getBang(shaderId, "shader").outputPort(portId)

    companion object {
        const val ReturnValue = "_"
    }
}

@Serializable @SerialName("output")
data class OutputPortRef(val portId: String) : PortRef() {
    override fun dereference(showEditor: ShowEditor): LinkEditor.Port =
        OutputPortEditor(portId)
}

@Serializable
data class Shader(
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
    fun edit(): ShaderEditor = ShaderEditor(this)
}