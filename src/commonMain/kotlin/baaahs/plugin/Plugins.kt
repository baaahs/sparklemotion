package baaahs.plugin

import baaahs.Gadget
import baaahs.PubSub
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.PortLinkOption
import baaahs.controller.*
import baaahs.device.EnumeratedPixelLocations
import baaahs.device.FixtureType
import baaahs.device.PixelLocations
import baaahs.dmx.*
import baaahs.fixtures.TransportConfig
import baaahs.getBang
import baaahs.gl.glsl.GlslType
import baaahs.gl.glsl.LinkException
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.glsl.LinearSurfacePixelStrategy
import baaahs.glsl.NullSurfacePixelStrategy
import baaahs.glsl.RandomSurfacePixelStrategy
import baaahs.glsl.SurfacePixelStrategy
import baaahs.mapper.MappingStrategy
import baaahs.mapper.OneAtATimeMappingStrategy
import baaahs.mapper.TwoLogNMappingStrategy
import baaahs.model.*
import baaahs.plugin.core.CorePlugin
import baaahs.scene.ControllerConfig
import baaahs.scene.MutableControllerConfig
import baaahs.show.*
import baaahs.show.mutable.MutableFeedPort
import baaahs.sim.BridgeClient
import baaahs.sm.brain.BrainControllerConfig
import baaahs.sm.brain.BrainManager
import baaahs.sm.server.PinkyArgs
import baaahs.util.Clock
import baaahs.util.Logger
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

sealed class Plugins(
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

    val feedBuilders = FeedBuilders()

    val fixtureTypes = FixtureTypes()

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

    class PluginFeedSerializer(
        private val byPlugin: Map<String, SerializersModule>
    ) : KSerializer<Feed> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("baaahs.show.Feed") {
            element("type", String.serializer().descriptor)
        }

        override fun deserialize(decoder: Decoder): Feed {
            val obj = JsonObject(MapSerializer(String.serializer(), JsonElement.serializer()).deserialize(decoder))
            val type = obj["type"]?.jsonPrimitive?.contentOrNull ?: error("Huh? No type?")
            val pluginRef = PluginRef.from(type)
            val plugin = byPlugin[pluginRef.pluginId]
                ?: return UnknownFeed(
                    pluginRef, "Unknown plugin \"${pluginRef.pluginId}\".", ContentType.Unknown, obj
                )

            val serializer = plugin.getPolymorphic(Feed::class, type)
                ?: return UnknownFeed(
                    pluginRef, "Unknown feed \"${pluginRef.toRef()}\".", ContentType.Unknown, obj
                )

            return try {
                Json.decodeFromJsonElement(serializer, buildJsonObject {
                    (obj.keys - "type").forEach { put(it, obj[it]!!) }
                })
            } catch (e: Exception) {
                logger.error(e) { "Failed to deserialize feed $type." }
                UnknownFeed(
                    pluginRef, e.message ?: "wha? unknown feed?", ContentType.Unknown, obj
                )
            }
        }

        override fun serialize(encoder: Encoder, value: Feed) {
            if (value is UnknownFeed) {
                encoder.encodeSerializableValue(JsonObject.serializer(), value.data)
                return
            }

            val serializersModule = byPlugin.getBang(value.pluginPackage, "plugin id")
            val serializer = serializersModule.serializer<Feed>()
            encoder.encodeSerializableValue(serializer, value)
        }
    }

    val serialModule = SerializersModule {
        include(Gadget.serialModule)
        include(contentTypes.serialModule)
        include(controlSerialModule)
        contextual(Feed::class, PluginFeedSerializer(feedBuilders.serialModulesByPlugin))
        include(feedBuilders.serialModule)
        include(fixtureTypes.serialModule)
        include(controllers.serialModule)

        polymorphic(SurfacePixelStrategy::class) {
            subclass(NullSurfacePixelStrategy::class, NullSurfacePixelStrategy.serializer())
            subclass(LinearSurfacePixelStrategy::class, LinearSurfacePixelStrategy.serializer())
            subclass(RandomSurfacePixelStrategy::class, RandomSurfacePixelStrategy.serializer())
        }

        polymorphic(EntityData::class) {
            subclass(ImportedEntityData::class, ImportedEntityData.serializer())
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

        polymorphic(PixelLocations::class) {
            subclass(EnumeratedPixelLocations::class, EnumeratedPixelLocations.serializer())
        }

        polymorphic(MovingHeadAdapter::class) {
            subclass(LixadaMiniMovingHead::class, LixadaMiniMovingHead.serializer())
            subclass(Shenzarpy::class, Shenzarpy.serializer())
            subclass(Boryli::class, Boryli.serializer())
        }

        polymorphic(Tab::class) {
            subclass(LegacyTab::class, LegacyTab.serializer())
            subclass(GridTab::class, GridTab.serializer())
        }

        polymorphic(MappingStrategy.SessionMetadata::class) {
            subclass(OneAtATimeMappingStrategy.OneAtATimeSessionMetadata::class, OneAtATimeMappingStrategy.OneAtATimeSessionMetadata.serializer())
            subclass(TwoLogNMappingStrategy.TwoLogNSessionMetadata::class, TwoLogNMappingStrategy.TwoLogNSessionMetadata.serializer())
        }

//        polymorphic(MappingStrategy.EntityMetadata::class) {
//            subclass(OneAtATimeMappingStrategy.OneAtATimeEntityMetadata::class, OneAtATimeMappingStrategy.OneAtATimeEntityMetadata.serializer())
//            subclass(TwoLogNMappingStrategy.TwoLogNEntityMetadata::class, TwoLogNMappingStrategy.TwoLogNEntityMetadata.serializer())
//        }

        polymorphic(MappingStrategy.PixelMetadata::class) {
            subclass(OneAtATimeMappingStrategy.OneAtATimePixelMetadata::class, OneAtATimeMappingStrategy.OneAtATimePixelMetadata.serializer())
            subclass(TwoLogNMappingStrategy.TwoLogNPixelMetadata::class, TwoLogNMappingStrategy.TwoLogNPixelMetadata.serializer())
        }
    }

    val json = Json { serializersModule = this@Plugins.serialModule }

    fun <T: OpenPlugin > findPlugin(pluginKlass: KClass<T>): T? {
        @Suppress("UNCHECKED_CAST")
        return openPlugins.find { it::class == pluginKlass } as T?
    }

    inline fun <reified T : OpenPlugin> getPlugin(): T =
        findPlugin(T::class) ?: error("No such plugin ${T::class.simpleName}")

    inline fun <reified T : OpenPlugin> findPlugin(): T? =
        findPlugin(T::class)

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

    fun findFeedBuilder(pluginRef: PluginRef): FeedBuilder<out Feed> {
        return feedBuilders.byPluginRef[pluginRef]
            ?: throw LinkException("unknown plugin resource $pluginRef")
    }

    fun resolveFeed(inputPort: InputPort): Feed {
        val pluginRef = inputPort.pluginRef ?: error("no plugin specified")
        val builder = findFeedBuilder(pluginRef)
        return builder.build(inputPort)
    }

    fun suggestFeeds(
        inputPort: InputPort,
        suggestedContentTypes: Set<ContentType> = emptySet()
    ): List<PortLinkOption> {
        val suggestions = (setOfNotNull(inputPort.contentType) + suggestedContentTypes).flatMap { contentType ->
            val feedCandidates =
                feedBuilders.buildForContentType(contentType, inputPort) +
                        fixtureTypes.buildForContentType(contentType, inputPort)

            feedCandidates.map { feed ->
                PortLinkOption(
                    MutableFeedPort(feed),
                    wasPurposeBuilt = feed.appearsToBePurposeBuiltFor(inputPort),
                    isPluginSuggestion = true,
                    isExactContentType = feed.contentType == inputPort.contentType
                            // TODO: This is dodgy. Trying to get Slider to win over channel ref.
                            || inputPort.contentType.isUnknown()
                )
            }
        }

        return if (suggestions.isNotEmpty()) {
            suggestions
        } else {
            feedBuilders.all.map {
                it.suggestFeeds(inputPort, suggestedContentTypes)
            }.flatten()
        }
    }

    private fun getPlugin(packageName: String): OpenPlugin {
        return byPackage[packageName]
            ?: error("no such plugin \"$packageName\"")
    }

    fun decodeFeed(pluginId: String, pluginData: JsonObject): Feed {
        TODO("not implemented")
    }

    fun find(packageName: String): OpenPlugin {
        return byPackage.getBang(packageName, "package")
    }

    fun getSettingsPanels(): List<DialogPanel> {
        return byPackage.values.filterIsInstance<OpenClientPlugin>()
            .mapNotNull { plugin -> plugin.getSettingsPanel() }
    }

    fun createMutableControllerConfigFor(controllerId: ControllerId, state: ControllerState?): MutableControllerConfig {
        val controllerManager = controllers.all.find { it.controllerTypeName == controllerId.controllerType }
            ?: error("Unknown controller type ${controllerId.controllerType}.")
        return controllerManager.createMutableControllerConfigFor(controllerId, state)

    }

    // TODO: We should report errors back somehow.
    private fun safeBuild(
        builder: FeedBuilder<out Feed>,
        inputPort: InputPort
    ): Feed? = builder.safeBuild(inputPort)

    class PluginArgs(private val map: Map<Plugin<Any>, Any>) {
        constructor(allPlugins: Collection<Plugin<*>>, pinkyArgs: PinkyArgs): this(
            allPlugins.associate { it as Plugin<Any> to it.getArgs(pinkyArgs) }
        )

        fun openForServer(pluginContext: PluginContext): List<OpenServerPlugin> {
            return map.map { (plugin, pluginArgs) ->
                plugin.openForServer(pluginContext, pluginArgs)
            }
        }
    }

    companion object {
        private val logger = Logger<Plugins>()

        fun buildForServer(
            pluginContext: PluginContext,
            plugins: List<Plugin<*>>,
            programName: String,
            startupArgs: Array<String>
        ): ServerPlugins {
            val parser = ArgParser(programName)
            val pinkyArgs = PinkyArgs(parser)
            val allPlugins = listOf(CorePlugin) + plugins
            val pluginArgs = PluginArgs(allPlugins, pinkyArgs)
            val result = parser.parse(startupArgs)
            val openServerPlugins = pluginArgs.openForServer(pluginContext)
            return ServerPlugins(openServerPlugins, pluginContext, pinkyArgs)
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

    inner class FeedBuilders {
        val withPlugin = openPlugins.flatMap { plugin ->
            (plugin.feedBuilders + plugin.fixtureTypes.flatMap { it.feedBuilders })
                .distinct()
                .map { plugin to it }
        }

        val all = withPlugin.map { it.second }

        val byPluginRef = withPlugin.associate { (plugin, builder) ->
            PluginRef(plugin.packageName, builder.resourceName) to builder
        }

        val byContentType = all.groupBy { builder -> builder.contentType }

        val serialModulesByPlugin = serializersMap {
            feedSerializerRegistrars()
        }

        val serialModule = SerializersModule {
            registerSerializers {
                feedSerializerRegistrars()
            }
        }

        private fun OpenPlugin.feedSerializerRegistrars() =
            feedBuilders.map { it.serializerRegistrar } +
                    fixtureTypes.flatMap { it.feedBuilders.map { builder -> builder.serializerRegistrar } }

        fun buildForContentType(
            contentType: ContentType?,
            inputPort: InputPort
        ) = (
                feedBuilders.byContentType[contentType]
                    ?.mapNotNull { safeBuild(it, inputPort) }
                    ?: emptyList()
                )
    }

    inner class FixtureTypes {
        val all = openPlugins.flatMap { it.fixtureTypes }

        val serialModule = SerializersModule {
            val serializer = FixtureType.Serializer(all.associateBy { it.id })

            contextual(FixtureType::class, serializer)
            all.forEach { fixtureType ->
                @Suppress("UNCHECKED_CAST")
                contextual(fixtureType::class as KClass<FixtureType>, serializer)
                include(fixtureType.serialModule)
            }
        }

        fun buildForContentType(contentType: ContentType, inputPort: InputPort): List<Feed> {
            return all.flatMap { fixtureType ->
                fixtureType.feedBuilders.filter { feed -> feed.contentType == contentType }
            }.mapNotNull { safeBuild(it, inputPort) }
        }
    }

    inner class Controllers {
         val all = openPlugins.flatMap { it.controllerManagers }

        val serialModule = SerializersModule {
            polymorphic(ControllerConfig::class) {
                subclass(BrainControllerConfig::class, BrainControllerConfig.serializer())
                subclass(DirectDmxControllerConfig::class, DirectDmxControllerConfig.serializer())
                subclass(SacnControllerConfig::class, SacnControllerConfig.serializer())
            }

            polymorphic(TransportConfig::class) {
                subclass(DmxTransportConfig::class, DmxTransportConfig.serializer())
            }

            polymorphic(ControllerState::class) {
                subclass(BrainManager.State::class, BrainManager.State.serializer())
                subclass(DirectDmxController.State::class, DirectDmxController.State.serializer())
                subclass(SacnManager.State::class, SacnManager.State.serializer())
                subclass(NullController.State::class, NullController.State.serializer())
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