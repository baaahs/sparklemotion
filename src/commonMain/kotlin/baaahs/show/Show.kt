package baaahs.show

import baaahs.Surface
import baaahs.camelize
import baaahs.glshaders.Plugins
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.json.JsonElement
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
    val dataSources: Map<String, DataSource> = emptyMap()
) : Patchy {
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
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<ControlRef>> = emptyMap(),
    val patchSets: List<PatchSet> = emptyList()
) : Patchy {
    fun findShaderPortRefs(): Set<ShaderPortRef> = patchSets.flatMap { it.findShaderPortRefs() }.toSet()
}

@Serializable
data class PatchSet(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<ControlRef>> = emptyMap()
) : Patchy {
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
    val rootNode: LayoutNode
) {
    fun getPanelNames(): List<String> {
        val panelNames = hashSetOf<String>()
        rootNode.accept(object : Visitor<LayoutNode> {
            override fun visit(t: LayoutNode) {
                if (t is LayoutNode.Panel) {
                    panelNames.add(t.title)
                }
            }
        })
        return panelNames.toList().sorted()
    }
}

@Serializable
sealed class LayoutNode {
    abstract val size: String

    abstract fun accept(visitor: Visitor<LayoutNode>)

    interface Container {
        val items: List<LayoutNode>
    }

    @Serializable @SerialName("columns")
    data class Columns(
        override val items: List<LayoutNode>,
        override val size: String
    ) : LayoutNode(), Container {
        constructor(size: String, vararg items: LayoutNode) : this(items.toList(), size)

        override fun accept(visitor: Visitor<LayoutNode>) {
            visitor.visit(this)
            items.forEach { it.accept(visitor) }
        }
    }

    @Serializable @SerialName("rows")
    data class Rows(
        override val items: List<LayoutNode>,
        override val size: String
    ) : LayoutNode(), Container {
        constructor(size: String, vararg items: LayoutNode) : this(items.toList(), size)

        override fun accept(visitor: Visitor<LayoutNode>) {
            visitor.visit(this)
            items.forEach { it.accept(visitor) }
        }
    }

    @Serializable @SerialName("panel")
    data class Panel(
        val title: String,
        override val size: String,
        val flow: Flow = Flow.horizontalFromLeft
    ) : LayoutNode() {
        override fun accept(visitor: Visitor<LayoutNode>) {
            visitor.visit(this)
        }
    }

    enum class Flow {
        horizontalFromLeft,
        horizontalFromRight,
        verticalFromTop,
        verticalFromBottom
    }
}

interface Visitor<T> {
    fun visit(t: T)
}

@Serializable
data class Link(
    val from: PortRef,
    val to: PortRef
)

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