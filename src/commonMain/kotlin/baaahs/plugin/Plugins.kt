package baaahs.plugin

import baaahs.Gadget
import baaahs.app.ui.editor.PortLinkOption
import baaahs.fixtures.DeviceType
import baaahs.getBang
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.show.*
import baaahs.show.mutable.MutableDataSourcePort
import baaahs.util.Clock
import baaahs.util.Time
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlin.reflect.KClass

@Serializable
data class PluginRef(
    /** A unique ID for plugin. Should be lowercase alphanums and dots, like a package name. */
    val pluginId: String,
    /** A unique ID for a resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String,
    val pluginIdNotSpecified: Boolean = false
) {
    fun toRef() = "$pluginId:$resourceName"

    companion object {
        fun hasPackage(identString: String): Boolean {
            return identString.contains(":")
        }

        fun from(identString: String): PluginRef {
            val result = Regex("(([\\w.]+):)?(\\w+)").matchEntire(identString)
            return if (result != null) {
                val (_, pluginId, resourceName) = result.destructured
                PluginRef(pluginId, resourceName)
            } else {
                PluginRef(Plugins.default, identString, pluginIdNotSpecified = true)
            }
        }
    }
}

class Plugins private constructor(
    val pluginContext: PluginContext,
    private val plugins: List<Plugin>
) {
    constructor(
        pluginBuilders: List<PluginBuilder>,
        pluginContext: PluginContext
    ) : this(pluginContext, pluginBuilders.map { it.build(pluginContext) })

    constructor(
        vararg pluginBuilders: PluginBuilder,
        pluginContext: PluginContext
    ) : this(pluginContext, pluginBuilders.map { it.build(pluginContext) })

    private val byPackage: Map<String, Plugin> = plugins.associateBy { it.packageName }

    val addControlMenuItems: List<AddControlMenuItem> = plugins.flatMap { it.addControlMenuItems }

    private val contentTypes = ContentTypes()

    private val controlSerialModule = SerializersModule {
        registerSerializers { controlSerializers }
    }

    private val dataSourceSerialModule = SerializersModule {
        registerSerializers { dataSourceSerializers }
    }

    private val dataSourceBuilders = DataSourceBuilders()

    private inline fun <reified T : Any> SerializersModuleBuilder.registerSerializers(
        getSerializersFn: Plugin.() -> List<SerializerRegistrar<out T>>
    ) {
        polymorphic(T::class) {
            plugins.forEach { plugin ->
                plugin.getSerializersFn().forEach { classSerializer ->
                    with(classSerializer) { register(this@polymorphic) }
                }
            }
        }
    }

    val serialModule = SerializersModule {
        include(Gadget.serialModule)
        include(controlSerialModule)
        include(dataSourceSerialModule)
        include(DeviceType.serialModule)
    }

    val json = Json { serializersModule = this@Plugins.serialModule }

    fun <T: Plugin > findPlugin(pluginKlass: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return plugins.find { it::class == pluginKlass } as T
    }

    inline fun <reified T : Plugin> findPlugin(): T = findPlugin(T::class)

    private fun findPlugin(pluginRef: PluginRef): Plugin {
        return byPackage[pluginRef.pluginId]
            ?: error("unknown plugin \"${pluginRef.pluginId}\"")
    }

    fun resolveContentType(name: String): ContentType? {
        return contentTypes.byId[name]
    }

    fun suggestContentTypes(inputPort: InputPort): Set<ContentType> {
        val glslType = inputPort.type
        val isStream = inputPort.glslVar?.isVarying ?: false
        return (contentTypes.byGlslType[glslType to isStream] ?: emptyList()).toSet()
    }

    fun resolveDataSource(inputPort: InputPort): DataSource {
        val pluginRef = inputPort.pluginRef ?: error("no plugin specified")
        val builder = dataSourceBuilders.byPluginRef[pluginRef] ?: error("unknown plugin resource $pluginRef")
        return builder.build(inputPort)
    }

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType> = emptySet()
    ): List<PortLinkOption> {
        val suggestions = (setOf(inputPort.contentType) + suggestedContentTypes).flatMap { contentType ->
            dataSourceBuilders.byContentType[contentType]?.map {
                it.build(inputPort)
                    .let { dataSource ->
                        PortLinkOption(
                            MutableDataSourcePort(dataSource),
                            wasPurposeBuilt = dataSource.appearsToBePurposeBuiltFor(inputPort),
                            isPluginSuggestion = true,
                            isExactContentType = dataSource.getContentType() == inputPort.contentType
                        )
                    }
            } ?: emptyList()
        }

        return if (suggestions.isNotEmpty()) {
            suggestions
        } else {
            dataSourceBuilders.all.map {
                it.suggestDataSources(inputPort, suggestedContentTypes)
            }.flatten()
        }
    }

    private fun getPlugin(packageName: String): Plugin {
        return byPackage[packageName]
            ?: error("no such plugin \"$packageName\"")
    }

    fun decodeDataSource(pluginId: String, pluginData: JsonObject): DataSource {
        TODO("not implemented")
    }

    operator fun plus(pluginBuilder: PluginBuilder): Plugins {
        return Plugins(pluginContext, plugins + pluginBuilder.build(pluginContext))
    }

    fun find(packageName: String): Plugin {
        return byPackage.getBang(packageName, "package")
    }

    companion object {
        fun safe(pluginContext: PluginContext): Plugins =
            Plugins(listOf(CorePlugin), pluginContext)

        val default = CorePlugin.id

        /** Don't use me except from [baaahs.show.SampleData] and [baaahs.glsl.GuruMeditationError]. */
        internal val dummyContext = PluginContext(ZeroClock())

        private class ZeroClock : Clock {
            override fun now(): Time = 0.0
        }
    }

    inner class DataSourceBuilders {
        private val withPlugin = plugins.flatMap { plugin -> plugin.dataSourceBuilders.map { plugin to it } }

        val all = withPlugin.map { it.second }

        val byPluginRef = withPlugin.associate { (plugin, builder) ->
            PluginRef(plugin.packageName, builder.resourceName) to builder
        }

        val byContentType = all.groupBy { builder -> builder.contentType }
    }

    inner class ContentTypes {
        val all = plugins.flatMap { it.contentTypes }.toSet()
        val byId = all.associateBy { it.id }
        val byGlslType = all.filter { it.suggest }.groupBy({ it.glslType to it.isStream }, { it })
    }
}