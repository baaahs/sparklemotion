package baaahs.plugin

import baaahs.Gadget
import baaahs.PubSub
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.PortLinkOption
import baaahs.controller.SacnControllerConfig
import baaahs.device.DeviceType
import baaahs.dmx.DirectDmxControllerConfig
import baaahs.dmx.DirectDmxTransportConfig
import baaahs.dmx.LixadaMiniMovingHead
import baaahs.dmx.Shenzarpy
import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.LinkException
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.glsl.RandomSurfacePixelStrategy
import baaahs.glsl.SurfacePixelStrategy
import baaahs.mapper.SacnTransportConfig
import baaahs.mapper.TransportConfig
import baaahs.model.*
import baaahs.plugin.core.CorePlugin
import baaahs.scene.ControllerConfig
import baaahs.show.DataSource
import baaahs.show.DataSourceBuilder
import baaahs.show.UnknownDataSource
import baaahs.show.appearsToBePurposeBuiltFor
import baaahs.show.mutable.MutableDataSourcePort
import baaahs.sim.BridgeClient
import baaahs.sm.server.PinkyArgs
import baaahs.util.Clock
import baaahs.util.Time
import kotlinx.cli.ArgParser
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.SerializersModuleBuilder
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

@Serializable
data class PluginRef(
    /** A unique ID for plugin. Should be lowercase alphanums and dots, like a package name. */
    val pluginId: String,
    /** A unique ID for a resource within a plugin. Should be CamelCase alphanums, like a class name. */
    val resourceName: String
) {
    fun toRef() = "$pluginId:$resourceName"
    fun shortRef() = if (pluginId == CorePlugin.id) resourceName else toRef()

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

class SafePlugins(
    pluginContext: PluginContext,
    plugins: List<OpenPlugin>
) : Plugins(pluginContext, plugins)

class ServerPlugins(
    plugins: List<OpenServerPlugin>,
    pluginContext: PluginContext,
    val pinkyArgs: PinkyArgs
) : Plugins(pluginContext, plugins)

class ClientPlugins : Plugins {
    constructor(pluginContext: PluginContext, plugins: List<Plugin<*>>) : super(
        pluginContext,
        plugins.map { it.openForClient(pluginContext) }
    )

    constructor(
        plugins: List<OpenClientPlugin>,
        pluginContext: PluginContext
    ) : super(pluginContext, plugins)
}

class SimulatorPlugins(
    private val bridgeClient: BridgeClient,
    plugins: List<Plugin<*>>
) {
    private val simulatorPlugins: List<OpenSimulatorPlugin>
    private var pluginsToSimulatorPlugins: List<Pair<Plugin<*>, OpenSimulatorPlugin?>>

    init {
        val forSimulator = mutableListOf<OpenSimulatorPlugin>()

        pluginsToSimulatorPlugins = plugins.map {
            it as Plugin<Any>
            it to (it as? SimulatorPlugin)?.openForSimulator()
        }
        simulatorPlugins = forSimulator
    }

    fun openServerPlugins(pluginContext: PluginContext) =
        ServerPlugins(
            pluginsToSimulatorPlugins.map { (plugin, simulatorPlugin) ->
                simulatorPlugin?.getServerPlugin(pluginContext, bridgeClient)
                    ?: run {
                        plugin as Plugin<Any>
                        val parser = ArgParser("void")
                        val args = plugin.getArgs(parser)
//                        parser.parse(emptyArray())
                        plugin.openForServer(pluginContext, args)
                    }
            },
            pluginContext,
            PinkyArgs.defaults
        )

    fun openClientPlugins(pluginContext: PluginContext) =
        ClientPlugins(
            pluginsToSimulatorPlugins.map { (plugin, simulatorPlugin) ->
                simulatorPlugin?.getClientPlugin(pluginContext)
                    ?: plugin.openForClient(pluginContext)
            },
            pluginContext
        )
}

sealed class Plugins private constructor(
    @Deprecated("Don't use this directly")
    val pluginContext: PluginContext,
    private val openPlugins: List<OpenPlugin>
) {
    private val byPackage: Map<String, OpenPlugin> = openPlugins.associateBy { it.packageName }

    val addControlMenuItems: List<AddControlMenuItem> = openPlugins.flatMap { it.addControlMenuItems }

    private val contentTypes = ContentTypes()

    private val controlSerialModule = SerializersModule {
        registerSerializers { controlSerializers }
    }

    val dataSourceBuilders = DataSourceBuilders()

    val deviceTypes = DeviceTypes()

    val controllers = Controllers()

    val shaderDialects = ShaderDialects()
    val shaderTypes = ShaderTypes()

    private inline fun <reified T : Any> serializersMap(
        getSerializersFn: OpenPlugin.() -> List<SerializerRegistrar<out T>>
    ) = openPlugins.associate { plugin ->
        plugin.packageName to SerializersModule {
            polymorphic(T::class) {
                plugin.getSerializersFn().forEach { classSerializer ->
                    with(classSerializer) { register(this@polymorphic) }
                }
            }
        }
    }

    private inline fun <reified T : Any> SerializersModuleBuilder.registerSerializers(
        getSerializersFn: OpenPlugin.() -> List<SerializerRegistrar<out T>>
    ) {
        polymorphic(T::class) {
            openPlugins.forEach { plugin ->
                plugin.getSerializersFn().forEach { classSerializer ->
                    with(classSerializer) { register(this@polymorphic) }
                }
            }
        }
    }

    class PluginDataSourceSerializer(
        private val byPlugin: Map<String, SerializersModule>
    ) : KSerializer<DataSource> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("baaahs.show.DataSource") {
            element("type", String.serializer().descriptor)
        }

        override fun deserialize(decoder: Decoder): DataSource {
            val obj = JsonObject(MapSerializer(String.serializer(), JsonElement.serializer()).deserialize(decoder))
            val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: error("Huh? No type?")
            val pluginRef = PluginRef.from(type)
            val plugin = byPlugin[pluginRef.pluginId]
                ?: return UnknownDataSource(
                    pluginRef, "Unknown plugin \"${pluginRef.pluginId}\".", ContentType.Unknown, obj
                )

            val serializer = plugin.getPolymorphic(DataSource::class, type)
                ?: return UnknownDataSource(
                    pluginRef, "Unknown datasource \"${pluginRef.toRef()}\".", ContentType.Unknown, obj
                )

            return try {
                Json.decodeFromJsonElement(serializer, buildJsonObject {
                    (obj.keys - "type").forEach { put(it, obj[it]!!) }
                })
            } catch (e: Exception) {
                UnknownDataSource(
                    pluginRef, e.message ?: "wha? unknown datasource?", ContentType.Unknown, obj
                )
            }
        }

        override fun serialize(encoder: Encoder, value: DataSource) {
            if (value is UnknownDataSource) {
                encoder.encodeSerializableValue(JsonObject.serializer(), value.data)
                return
            }

            val serializersModule = byPlugin.getBang(value.pluginPackage, "plugin id")
            val serializer = serializersModule.serializer<DataSource>()
            encoder.encodeSerializableValue(serializer, value)
        }
    }

    val serialModule = SerializersModule {
        include(Gadget.serialModule)
        include(contentTypes.serialModule)
        include(controlSerialModule)
        contextual(DataSource::class, PluginDataSourceSerializer(dataSourceBuilders.serialModulesByPlugin))
        include(dataSourceBuilders.serialModule)
        include(deviceTypes.serialModule)
        include(controllers.serialModule)

        polymorphic(SurfacePixelStrategy::class) {
            subclass(LinearSurfacePixelStrategy::class, LinearSurfacePixelStrategy.serializer())
            subclass(RandomSurfacePixelStrategy::class, RandomSurfacePixelStrategy.serializer())
        }

        polymorphic(EntityData::class) {
            subclass(ObjModelData::class, ObjModelData.serializer())
            subclass(MovingHeadData::class, MovingHeadData.serializer())
            subclass(LightBarData::class, LightBarData.serializer())
            subclass(PolyLineData::class, PolyLineData.serializer())
            subclass(GridData::class, GridData.serializer())
            subclass(LightRingData::class, LightRingData.serializer())
            subclass(SurfaceDataForTest::class, SurfaceDataForTest.serializer())
        }

        polymorphic(EntityMetadataProvider::class) {
            subclass(ConstEntityMetadataProvider::class, ConstEntityMetadataProvider.serializer())
            subclass(StrandCountEntityMetadataProvider::class, StrandCountEntityMetadataProvider.serializer())
        }

        polymorphic(MovingHeadAdapter::class) {
            subclass(LixadaMiniMovingHead::class, LixadaMiniMovingHead.serializer())
            subclass(Shenzarpy::class, Shenzarpy.serializer())
        }

        polymorphic(TransportConfig::class) {
            subclass(SacnTransportConfig::class, SacnTransportConfig.serializer())
        }
    }

    val json = Json { serializersModule = this@Plugins.serialModule }

    fun <T: OpenPlugin > findPlugin(pluginKlass: KClass<T>): T {
        @Suppress("UNCHECKED_CAST")
        return openPlugins.find { it::class == pluginKlass } as T
    }

    inline fun <reified T : OpenPlugin> findPlugin(): T = findPlugin(T::class)

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

    fun findDataSourceBuilder(pluginRef: PluginRef): DataSourceBuilder<out DataSource> {
        return dataSourceBuilders.byPluginRef[pluginRef]
            ?: throw LinkException("unknown plugin resource $pluginRef")
    }

    fun resolveDataSource(inputPort: InputPort): DataSource {
        val pluginRef = inputPort.pluginRef ?: error("no plugin specified")
        val builder = findDataSourceBuilder(pluginRef)
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

    private fun getPlugin(packageName: String): OpenPlugin {
        return byPackage[packageName]
            ?: error("no such plugin \"$packageName\"")
    }

    fun decodeDataSource(pluginId: String, pluginData: JsonObject): DataSource {
        TODO("not implemented")
    }

    fun find(packageName: String): OpenPlugin {
        return byPackage.getBang(packageName, "package")
    }

    fun getSettingsPanels(): List<DialogPanel> {
        return byPackage.values.filterIsInstance<OpenClientPlugin>()
            .mapNotNull { plugin -> plugin.getSettingsPanel() }
    }

    companion object {
        fun buildForServer(
            pluginContext: PluginContext,
            plugins: List<Plugin<*>>,
            programName: String,
            startupArgs: Array<String>
        ): ServerPlugins {
            val parser = ArgParser(programName)
            val pinkyArgs = PinkyArgs(parser)
            val pluginToArgs = (listOf(CorePlugin) + plugins).map {
                it as Plugin<Any>
                it to it.getArgs(parser)
            }

            parser.parse(startupArgs)

            val serverPlugins = pluginToArgs.map { (plugin, pluginArgs) ->
                plugin.openForServer(pluginContext, pluginArgs)
            }

            return ServerPlugins(serverPlugins, pluginContext, pinkyArgs)
        }

        fun buildForClient(pluginContext: PluginContext, plugins: List<Plugin<*>>): ClientPlugins =
            ClientPlugins(pluginContext, listOf(CorePlugin) + plugins)

        fun buildForSimulator(bridgeClient: BridgeClient, plugins: List<Plugin<*>>): SimulatorPlugins =
            SimulatorPlugins(bridgeClient, listOf(CorePlugin) + plugins)

        fun safe(pluginContext: PluginContext): Plugins =
            SafePlugins(pluginContext, listOf(CorePlugin.openSafe(pluginContext)))

        val default = CorePlugin.id

        /** Don't use me except from [baaahs.show.SampleData] and [baaahs.glsl.GuruMeditationError]. */
        internal val dummyContext = PluginContext(ZeroClock(), StubPubSub())

        private class ZeroClock : Clock {
            override fun now(): Time = 0.0
        }

        private class StubPubSub : PubSub.Endpoint() {
            override val commandChannels: PubSub.CommandChannels
                get() = PubSub.CommandChannels()

            override fun <T> openChannel(topic: PubSub.Topic<T>, initialValue: T, onUpdate: (T) -> Unit): PubSub.Channel<T> {
                return object : PubSub.Channel<T> {
                    override fun onChange(t: T) {}
                    override fun replaceOnUpdate(onUpdate: (T) -> Unit) {}
                    override fun unsubscribe() {} }

            }
        }
    }

    inner class ContentTypes {
        val all = openPlugins.flatMap { it.contentTypes }.toSet()
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
        val withPlugin = openPlugins.flatMap { plugin -> plugin.dataSourceBuilders.map { plugin to it } }

        val all = withPlugin.map { it.second }

        val byPluginRef = withPlugin.associate { (plugin, builder) ->
            PluginRef(plugin.packageName, builder.resourceName) to builder
        }

        val byContentType = all.groupBy { builder -> builder.contentType }

        val serialModulesByPlugin = serializersMap {
            dataSourceSerlializerRegistrars()
        }

        val serialModule = SerializersModule {
            registerSerializers {
                dataSourceSerlializerRegistrars()
            }
        }

        private fun OpenPlugin.dataSourceSerlializerRegistrars() =
            dataSourceBuilders.map { it.serializerRegistrar } +
                    deviceTypes.flatMap { it.dataSourceBuilders.map { builder -> builder.serializerRegistrar } }

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
        val all = openPlugins.flatMap { it.deviceTypes }

        val serialModule = SerializersModule {
            val serializer = DeviceType.Serializer(all.associateBy { it.id })

            contextual(DeviceType::class, serializer)
            all.forEach { deviceType ->
                @Suppress("UNCHECKED_CAST")
                contextual(deviceType::class as KClass<DeviceType>, serializer)
                include(deviceType.serialModule)
            }
        }

        fun buildForContentType(contentType: ContentType, inputPort: InputPort): List<DataSource> {
            return all.flatMap { deviceType ->
                deviceType.dataSourceBuilders.filter { dataSource -> dataSource.contentType == contentType }
            }.map { it.build(inputPort) }
        }
    }

    inner class Controllers {
        val serialModule = SerializersModule {
            polymorphic(ControllerConfig::class) {
                subclass(SacnControllerConfig::class, SacnControllerConfig.serializer())
                subclass(DirectDmxControllerConfig::class, DirectDmxControllerConfig.serializer())
            }

            polymorphic(TransportConfig::class) {
                subclass(DirectDmxTransportConfig::class, DirectDmxTransportConfig.serializer())
                subclass(SacnTransportConfig::class, SacnTransportConfig.serializer())
            }
        }
    }

    inner class ShaderDialects {
        val all = openPlugins.flatMap { it.shaderDialects }
    }

    inner class ShaderTypes {
        val all = openPlugins.flatMap { it.shaderTypes }
    }
}
