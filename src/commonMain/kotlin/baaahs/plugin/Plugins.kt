package baaahs.plugin

import baaahs.Gadget
import baaahs.app.ui.editor.PortLinkOption
import baaahs.fixtures.DeviceType
import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.LinkException
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.*
import baaahs.show.*
import baaahs.show.mutable.MutableDataSourcePort
import baaahs.util.Clock
import baaahs.util.Time
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
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
    val resourceName: String
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
                if (pluginId.isEmpty()) {
                    PluginRef(Plugins.default, resourceName)
                } else if (!pluginId.contains(".")) {
                    PluginRef("baaahs.$pluginId", resourceName)
                } else {
                    PluginRef(pluginId, resourceName)
                }
            } else {
                PluginRef(Plugins.default, identString)
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

    private val dataSourceBuilders = DataSourceBuilders()

    val deviceTypes = DeviceTypes()

    val shaderDialects = ShaderDialects()
    val shaderTypes = ShaderTypes()

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
        include(contentTypes.serialModule)
        include(controlSerialModule)
        include(dataSourceBuilders.serialModule)
        include(deviceTypes.serialModule)
    }

    val json = Json { serializersModule = this@Plugins.serialModule }

    fun <T: Plugin > findPlugin(pluginKlass: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return plugins.find { it::class == pluginKlass } as T
    }

    inline fun <reified T : Plugin> findPlugin(): T = findPlugin(T::class)

    fun resolveContentType(name: String): ContentType? {
        return contentTypes.byId[name]
    }

    fun resolveContentType(glslType: GlslType): ContentType {
        return if (glslType is GlslType.Struct)
            contentTypes.all.firstOrNull() { it.glslType == glslType }
                ?: ContentType.unknown(glslType)
        else ContentType.unknown(glslType)
    }

    fun suggestContentTypes(inputPort: InputPort): Set<ContentType> {
        val glslType = inputPort.type
        return contentTypes.matchingType(glslType)
    }

    fun resolveDataSource(inputPort: InputPort): DataSource {
        val pluginRef = inputPort.pluginRef ?: error("no plugin specified")
        val builder = dataSourceBuilders.byPluginRef[pluginRef]
            ?: throw LinkException("unknown plugin resource $pluginRef", inputPort.glslArgSite?.lineNumber)
        return builder.build(inputPort)
    }

    fun suggestDataSources(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType> = emptySet()
    ): List<PortLinkOption> {
        val suggestions = (setOfNotNull(inputPort.contentType) + suggestedContentTypes).flatMap { contentType ->
            val dataSourceCandidates =
                dataSourceBuilders.buildForContentType(contentType, inputPort) +
                        deviceTypes.buildForContentType(contentType, inputPort)

            dataSourceCandidates.map { dataSource ->
                PortLinkOption(
                    MutableDataSourcePort(dataSource),
                    wasPurposeBuilt = dataSource.appearsToBePurposeBuiltFor(inputPort),
                    isPluginSuggestion = true,
                    isExactContentType = dataSource.contentType == inputPort.contentType
                            // TODO: This is dodgy. Trying to get Slider to win over channel ref.
                            || inputPort.contentType.isUnknown()
                )
            }
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

    inner class ContentTypes {
        val all = plugins.flatMap { it.contentTypes }.toSet()
        internal val byId = all.associateBy { it.id }
        private val byGlslType = all.filter { it.suggest }.groupBy({ it.glslType }, { it })

        val serialModule = SerializersModule {
            contextual(ContentType::class, object : KSerializer<ContentType> {
                override val descriptor: SerialDescriptor
                    get() = String.serializer().descriptor

                override fun deserialize(decoder: Decoder): ContentType {
                    val id = decoder.decodeString()
                    return byId[id] ?: error("Unknown content type \"$id\"")
                }

                override fun serialize(encoder: Encoder, value: ContentType) {
                    encoder.encodeString(value.id)
                }
            })
        }

        fun matchingType(glslType: GlslType): Set<ContentType> {
            val exactMatches = byGlslType[glslType] ?: emptyList()
            return exactMatches.toSet()
        }
    }

    inner class DataSourceBuilders {
        private val withPlugin = plugins.flatMap { plugin -> plugin.dataSourceBuilders.map { plugin to it } }

        val all = withPlugin.map { it.second }

        val byPluginRef = withPlugin.associate { (plugin, builder) ->
            PluginRef(plugin.packageName, builder.resourceName) to builder
        }

        val byContentType = all.groupBy { builder -> builder.contentType }

        val serialModule = SerializersModule {
            registerSerializers {
                dataSourceBuilders.map { it.serializerRegistrar } +
                        deviceTypes.flatMap { it.dataSourceBuilders.map { builder -> builder.serializerRegistrar } }
            }
        }

        fun buildForContentType(
            contentType: ContentType?,
            inputPort: InputPort
        ) = (
                dataSourceBuilders.byContentType[contentType]
                    ?.map { it.build(inputPort) }
                    ?: emptyList()
                )
    }

    inner class DeviceTypes {
        val all = plugins.flatMap { it.deviceTypes }

        val serialModule = SerializersModule {
            val serializer = DeviceType.Serializer(all.associateBy { it.id })

            contextual(DeviceType::class, serializer)
            all.forEach { deviceType ->
                @Suppress("UNCHECKED_CAST")
                contextual(deviceType::class as KClass<DeviceType>, serializer)
            }
        }

        fun buildForContentType(contentType: ContentType, inputPort: InputPort): List<DataSource> {
            return all.flatMap { deviceType ->
                deviceType.dataSourceBuilders.filter { dataSource -> dataSource.contentType == contentType }
            }.map { it.build(inputPort) }
        }
    }

    inner class ShaderDialects {
        val all = plugins.flatMap { it.shaderDialects }
    }

    inner class ShaderTypes {
        val all = plugins.flatMap { it.shaderTypes }
    }
}