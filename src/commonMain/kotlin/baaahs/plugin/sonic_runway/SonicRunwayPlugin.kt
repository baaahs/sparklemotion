package baaahs.plugin.sonic_runway

import baaahs.Color
import baaahs.PubSub
import baaahs.geom.Vector4F
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.internalTimerClock
import baaahs.plugin.*
import baaahs.rpc.Service
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.show.FeedOpenContext
import baaahs.sim.BridgeClient
import baaahs.util.*
import kotlinx.cli.ArgParser
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class SonicRunwayPlugin internal constructor(
    val sonicRunwayDataProvider: SonicRunwayDataProvider,
) : OpenServerPlugin, OpenClientPlugin {

    override val packageName: String = id
    override val title: String = "Sound Analysis"

    override val contentTypes: List<ContentType>
        get() = feedBuilders.map { it.contentType }

    override val feedBuilders: List<FeedBuilder<out Feed>> =
        listOf(SonicRunwayFeedBuilder())

    internal val feed = SonicRunwayFeed()

    @SerialName("baaahs.SonicRunway:SonicRunway")
    inner class SonicRunwayFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "Sonic Runway"
        override val contentType: ContentType get() = sonicRunwayContentType
        override fun getType(): GlslType = dataStruct

        override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext =
            SonicRunwayFeedContext(getVarName(id), sonicRunwayDataProvider)
    }

    inner class SonicRunwayFeedBuilder : FeedBuilder<SonicRunwayFeed> {
        override val title: String get() = "Sonic Runway"
        override val description: String get() = "Sonic Runway color data!"
        override val resourceName: String get() = "SonicRunway"
        override val contentType: ContentType get() = sonicRunwayContentType
        override val serializerRegistrar get() = objectSerializer("$id:$resourceName", feed)

        override fun build(inputPort: InputPort): SonicRunwayFeed = feed
    }

    companion object : Plugin<Args>, SimulatorPlugin {
        override val id = "baaahs.SonicRunway"

        private val commandsRpc = SonicRunwayCommands.getImpl("plugins/$id")

        private val logger = Logger<SonicRunwayPlugin>()

        val dataStruct = GlslType.Struct(
            "SonicRunwayData",
            "color0" to GlslType.Vec4,
            "color1" to GlslType.Vec4,
            "color2" to GlslType.Vec4,
            "color3" to GlslType.Vec4,
            "color4" to GlslType.Vec4
        )

        val sonicRunwayContentType = ContentType("sonic-runway", "Sonic Runway", dataStruct)

        override fun getArgs(parser: ArgParser): Args = Args(parser)

        override fun openForServer(pluginContext: PluginContext, args: Args): OpenServerPlugin {
            val dataProvider = createServerSonicRunwayDataProvider(pluginContext)
            PubSubPublisher(dataProvider, pluginContext) // Yuck.
            return SonicRunwayPlugin(dataProvider)
        }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin {
            return SonicRunwayPlugin(PubSubSubscriber(pluginContext.pubSub))
        }

        override fun openForSimulator(): OpenSimulatorPlugin =
            object : OpenSimulatorPlugin {
                override fun getBridgePlugin(pluginContext: PluginContext): OpenBridgePlugin =
                    PubSubPublisher(createServerSonicRunwayDataProvider(pluginContext), pluginContext)

                override fun getServerPlugin(pluginContext: PluginContext, bridgeClient: BridgeClient): SonicRunwayPlugin {
                    val sonicRunwayDataProvider = PubSubSubscriber(bridgeClient.pubSub, true)
                    PubSubPublisher(sonicRunwayDataProvider, pluginContext)
                    return SonicRunwayPlugin(sonicRunwayDataProvider)
                }

                override fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin =
                    openForClient(pluginContext)
            }
    }

    class PubSubPublisher(
        sonicRunwayDataProvider: SonicRunwayDataProvider,
        pluginContext: PluginContext
    ) : OpenBridgePlugin {
        private val pubSub = pluginContext.pubSub

        private var colors = listOf<Color>()
        private var colorsVersion = 0
        private var updateChannel = CompletableDeferred<Unit>()

        init {
            sonicRunwayDataProvider.listen { data: SonicRunwayData ->
                if (data.colors != colors) {
                    colors = data.colors
                    colorsVersion++
                }

                globalLaunch {
                    val oldUpdateChannel = updateChannel
                    updateChannel = CompletableDeferred()
                    oldUpdateChannel.complete(Unit)
                }
            }
        }

        init {
            suspend fun doUpdate(newColorsVersion: Int?): UpdateResponse {
                while (newColorsVersion == colorsVersion) {
                    val theUpdateChannel = updateChannel
                    theUpdateChannel.await()
                }

                return UpdateResponse(colorsVersion, colors)
            }

            commandsRpc.createReceiver(pubSub, object : SonicRunwayCommands {
                override suspend fun update(frequenciesVersion: Int?) =
                    doUpdate(frequenciesVersion)
            })
        }
    }

    class PubSubSubscriber(private val pubSub: PubSub.Endpoint, val generateData: Boolean? = null) : SonicRunwayDataProvider {
        private var colors: List<Color> = listOf()
        private var colorsVersion = -1
        private var updatedTimestamp: Instant = Instant.DISTANT_PAST

        private val listeners = mutableSetOf<SonicRunwayListener>()

        private val rpcClient = commandsRpc.createSender(pubSub)

        init {
            globalLaunch { requestUpdate() }
        }

        private suspend fun requestUpdate() {
            while (!(pubSub as PubSub.Client).isConnected) {
                if (generateData == true) {
                    generateRandomData()
                    continue
                }
                delay(100)
            }

            val response = rpcClient.update(colorsVersion)
            if (response.colorsVersion != null &&
                response.colors != null &&
                response.colorsVersion != colorsVersion
            ) {
                colors = response.colors
                colorsVersion = response.colorsVersion
            }

            sendSample()
            requestUpdate()
        }

        private fun sendSample() {
            if (colors.isNotEmpty()) {
                val sonicRunwayData = SonicRunwayData(colors, updatedTimestamp)
                listeners.forEach { it.onUpdate(sonicRunwayData) }
            }
        }

        override fun listen(listener: SonicRunwayListener) {
            listeners.add(listener)
        }

        override fun unlisten(listener: SonicRunwayListener) {
            listeners.remove(listener)
        }

        private suspend fun generateRandomData() {
            colors = (0 .. 5).map { Color.random() }
            val t = internalTimerClock.now().asDoubleSeconds.toFloat()
            sendSample()
            delay(20)
        }
    }
}

class SonicRunwayFeedContext(
    private val varPrefix: String,
    private val dataProvider: SonicRunwayDataProvider
) : FeedContext, RefCounted by RefCounter(), SonicRunwayListener {
    private var colors = listOf<Color>()

    init { dataProvider.listen(this) }

    override fun onUpdate(data: SonicRunwayData) {
        // Copy this sample's data into the buffer.
        colors = data.colors
    }

    override fun bind(gl: GlContext): EngineFeedContext = SonicRunwayEngineFeedContext(gl)

    inner class SonicRunwayEngineFeedContext(private val gl: GlContext) : EngineFeedContext {
        private val texture = gl.check { createTexture() }

        init { gl.checkForLinearFilteringOfFloatTextures() }

        override fun bind(glslProgram: GlslProgram): ProgramFeedContext = object : ProgramFeedContext {
            val color0Uniform = glslProgram.getFloat4Uniform("${varPrefix}.color0")
            val color1Uniform = glslProgram.getFloat4Uniform("${varPrefix}.color1")
            val color2Uniform = glslProgram.getFloat4Uniform("${varPrefix}.color2")
            val color3Uniform = glslProgram.getFloat4Uniform("${varPrefix}.color3")
            val color4Uniform = glslProgram.getFloat4Uniform("${varPrefix}.color4")

            override val isValid: Boolean
                get() = color0Uniform != null ||
                        color1Uniform != null ||
                        color2Uniform != null ||
                        color3Uniform != null ||
                        color4Uniform != null

            override fun setOnProgram() {
                val colors = colors
                fun getColor(index: Int) =
                    (if (colors.size > index) colors[index] else Color.BLACK)
                        .let { Vector4F(it.redF, it.greenF, it.blueF, it.alphaF) }

                color0Uniform?.set(getColor(0))
                color1Uniform?.set(getColor(1))
                color2Uniform?.set(getColor(2))
                color3Uniform?.set(getColor(3))
                color4Uniform?.set(getColor(4))
            }
        }

        override fun release() {
            gl.check { deleteTexture(texture) }
        }
    }

    override fun onRelease() {
        dataProvider.unlisten(this)
    }

    companion object {
        private val logger = Logger<SonicRunwayPlugin>()
    }
}

fun interface SonicRunwayListener {
    fun onUpdate(data: SonicRunwayData)
}

@Serializable
data class SonicRunwayData(val colors: List<Color>, val updatedTimestamp: Instant)

@Service
interface SonicRunwayCommands {
    suspend fun update(frequenciesVersion: Int?): UpdateResponse

    companion object
}

interface SonicRunwayDataProvider {
    fun listen(listener: SonicRunwayListener)
    fun unlisten(listener: SonicRunwayListener)
}

@Serializable
class UpdateResponse(
    val colorsVersion: Int? = null,
    val colors: List<Color>? = null,
)

internal expect fun createServerSonicRunwayDataProvider(pluginContext: PluginContext): SonicRunwayDataProvider
