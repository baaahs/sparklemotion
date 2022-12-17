package baaahs.plugin.beatlink

import baaahs.PubSub
import baaahs.ShowPlayer
import baaahs.app.ui.CommonIcons
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.data.SingleUniformFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.glsl.GlslType
import baaahs.gl.patch.ContentType
import baaahs.gl.shader.InputPort
import baaahs.plugin.*
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.sim.BridgeClient
import baaahs.ui.Observable
import baaahs.ui.addObserver
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import baaahs.util.makeSafeForGlsl
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.serialization.SerialName

class BeatLinkPlugin internal constructor(
    internal val beatSource: BeatSource,
    pluginContext: PluginContext
) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = id
    override val title: String = "Beat Link"

    private val clock = pluginContext.clock

    // We'll just make one up-front. We only ever want one (because equality
    // is using object identity), and there's no overhead.
    internal val beatLinkFeed = BeatLinkFeed()
    internal val beatInfoFeed = BeatInfoFeed()
    internal val rawBeatInfoFeed = RawBeatInfoFeed()

    override val addControlMenuItems: List<AddControlMenuItem>
        get() = listOf(
            AddControlMenuItem("New BeatLink Control…", CommonIcons.BeatLinkControl) {
                MutableBeatLinkControl()
            }
        )
    override val contentTypes: List<ContentType>
        get() = listOf(
            beatDataContentType,
            beatInfoContentType,
            rawBeatInfoContentType
        )

    override val controlSerializers
        get() = listOf(
            classSerializer(BeatLinkControl.serializer())
        )

    override val feedBuilders
        get() = listOf(
            BeatLinkFeedBuilder(),
            BeatInfoFeedBuilder(),
            RawBeatInfoFeedBuilder()
        )

    inner class BeatLinkFeedBuilder : FeedBuilder<BeatLinkFeed> {
        override val title: String get() = "Beat Link"
        override val description: String
            get() = "A float representing the current beat intensity, between 0 and 1."
        override val resourceName: String get() = "BeatLink"
        override val contentType: ContentType get() = beatDataContentType
        override val serializerRegistrar
            get() = objectSerializer("$id:BeatLink", beatLinkFeed)

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == beatDataContentType
                    || suggestedContentTypes.contains(beatDataContentType)
                    || inputPort.type == GlslType.Float

        override fun build(inputPort: InputPort): BeatLinkFeed = beatLinkFeed
    }

    inner class BeatInfoFeedBuilder : FeedBuilder<BeatInfoFeed> {
        override val title: String get() = "Beat Info"
        override val description: String get() = "A struct containing information about the beat."
        override val resourceName: String get() = "BeatInfo"
        override val contentType: ContentType get() = beatInfoContentType
        override val serializerRegistrar
            get() = objectSerializer("$id:BeatInfo", beatInfoFeed)

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == beatInfoContentType
                    || suggestedContentTypes.contains(beatInfoContentType)
                    || inputPort.type == beatInfoStruct

        override fun build(inputPort: InputPort): BeatInfoFeed = beatInfoFeed
    }

    inner class RawBeatInfoFeedBuilder : FeedBuilder<RawBeatInfoFeed> {
        override val title: String get() = "Raw Beat Data"
        override val description: String get() = "A struct containing low-level information about the beat."
        override val resourceName: String get() = "RawBeatInfo"
        override val contentType: ContentType get() = rawBeatInfoContentType
        override val serializerRegistrar
            get() = objectSerializer("$id:RawBeatInfo", rawBeatInfoFeed)
        override val internalOnly: Boolean
            get() = true

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == rawBeatInfoContentType
                    || suggestedContentTypes.contains(rawBeatInfoContentType)
                    || inputPort.type == rawBeatInfoStruct

        override fun build(inputPort: InputPort): RawBeatInfoFeed = rawBeatInfoFeed
    }

    @SerialName("baaahs.BeatLink:BeatLink")
    inner class BeatLinkFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "BeatLink"
        override val contentType: ContentType get() = beatDataContentType

        override fun getType(): GlslType = GlslType.Float

        override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
            return object : FeedContext, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                    override fun bind(glslProgram: GlslProgram): ProgramFeedContext =
                        SingleUniformFeedContext(glslProgram, this@BeatLinkFeed, id) { uniform ->
                            uniform.set(beatSource.getBeatData().fractionTillNextBeat(clock))
                        }
                }
            }
        }
    }

    @SerialName("baaahs.BeatLink:BeatInfo")
    inner class BeatInfoFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "BeatInfo"
        override fun getType(): GlslType = beatInfoStruct
        override val contentType: ContentType
            get() = beatInfoContentType

        override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
            val varPrefix = getVarName(id)
            return object : FeedContext, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                    override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                        return object : ProgramFeedContext {
                            val beatUniform = glslProgram.getUniform("${varPrefix}.beat")
                            val bpmUniform = glslProgram.getUniform("${varPrefix}.bpm")
                            val intensityUniform = glslProgram.getUniform("${varPrefix}.intensity")
                            val confidenceUniform = glslProgram.getUniform("${varPrefix}.confidence")

                            override val isValid: Boolean
                                get() = beatUniform != null ||
                                        bpmUniform != null ||
                                        intensityUniform != null ||
                                        confidenceUniform != null

                            override fun setOnProgram() {
                                val beatData = beatSource.getBeatData()

                                beatUniform?.set(beatData.beatWithinMeasure(clock))
                                bpmUniform?.set(beatData.bpm)
                                intensityUniform?.set(beatData.fractionTillNextBeat(clock))
                                confidenceUniform?.set(beatData.confidence)
                            }
                        }
                    }
                }
            }
        }
    }

    @SerialName("baaahs.BeatLink:RawBeatInfo")
    inner class RawBeatInfoFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "RawBeatInfo"
        override fun getType(): GlslType = rawBeatInfoStruct
        override val contentType: ContentType
            get() = rawBeatInfoContentType

        override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
            val varPrefix = getVarName(id)
            return object : FeedContext, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                    override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                        return object : ProgramFeedContext {
                            val measureStartTime = glslProgram.getUniform("${varPrefix}.measureStartTime")
                            val beatIntervalMsUniform = glslProgram.getUniform("${varPrefix}.beatIntervalMs")
                            val bpmUniform = glslProgram.getUniform("${varPrefix}.bpm")
                            val beatsPerMeasureUniform = glslProgram.getUniform("${varPrefix}.beatsPerMeasure")
                            val confidenceUniform = glslProgram.getUniform("${varPrefix}.confidence")

                            override val isValid: Boolean
                                get() = measureStartTime != null ||
                                        beatIntervalMsUniform != null ||
                                        bpmUniform != null ||
                                        beatsPerMeasureUniform != null ||
                                        confidenceUniform != null

                            override fun setOnProgram() {
                                val beatData = beatSource.getBeatData()

                                measureStartTime?.set(beatData.measureStartTime.makeSafeForGlsl())
                                beatIntervalMsUniform?.set(beatData.beatIntervalMs.toFloat())
                                bpmUniform?.set(beatData.bpm)
                                beatsPerMeasureUniform?.set(beatData.beatsPerMeasure.toFloat())
                                confidenceUniform?.set(beatData.confidence)
                            }
                        }
                    }
                }
            }
        }
    }

    class ParserArgs(parser: ArgParser) : Args {
        override val enableBeatLink by parser.option(ArgType.Boolean, description = "Enable beat detection")
            .default(true)
    }

    interface Args {
        val enableBeatLink: Boolean
        val beatSource: BeatSource? get() = null
    }

    companion object : Plugin<Args>, SimulatorPlugin {
        private val logger = Logger<BeatLinkPlugin>()

        override val id = "baaahs.BeatLink"
        val beatDataContentType = ContentType("beat-link", "Beat Link", GlslType.Float)

        val beatInfoStruct = GlslType.Struct(
            "BeatInfo",
            "beat" to GlslType.Float,
            "bpm" to GlslType.Float,
            "intensity" to GlslType.Float,
            "confidence" to GlslType.Float
        )

        val beatInfoContentType = ContentType("beat-info", "Beat Info", beatInfoStruct)

        val rawBeatInfoStruct = GlslType.Struct(
            "RawBeatInfo",
            "measureStartTime" to GlslType.Float,
            "beatIntervalMs" to GlslType.Float,
            "bpm" to GlslType.Float,
            "beatsPerMeasure" to GlslType.Float,
            "confidence" to GlslType.Float
        )
        val rawBeatInfoContentType = ContentType("raw-beat-info", "Raw Beat Info", rawBeatInfoStruct)

        private val simulatorDefaultBpm = BeatData(0.0, 500, confidence = 1f)
        private val unknownBpm = BeatData(0.0, 500, confidence = 0f)

        override fun getArgs(parser: ArgParser): Args = ParserArgs(parser)

        override fun openForServer(pluginContext: PluginContext, args: Args): OpenServerPlugin {
            val beatSource = if (args.enableBeatLink) {
                args.beatSource ?: createServerBeatSource(pluginContext)
            } else BeatSource.None
            return BeatLinkPlugin(
                PubSubPublisher(beatSource, pluginContext),
                pluginContext
            )
        }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin =
            BeatLinkPlugin(PubSubSubscriber(pluginContext.pubSub), pluginContext)

        override fun openForSimulator(): OpenSimulatorPlugin =
            object : OpenSimulatorPlugin {
                override fun getBridgePlugin(pluginContext: PluginContext): OpenBridgePlugin =
                    BeatLinkBridgePlugin(createServerBeatSource(pluginContext), pluginContext)

                override fun getServerPlugin(pluginContext: PluginContext, bridgeClient: BridgeClient) =
                    BeatLinkPlugin(
                        PubSubPublisher(
                            PubSubSubscriber(bridgeClient.pubSub, simulatorDefaultBpm),
                            pluginContext
                        ),
                        pluginContext
                    )

                override fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin =
                    openForClient(pluginContext)
            }

        fun forTest(beatSource: BeatSource): Plugin<Args> {
            return object : Plugin<Args> by BeatLinkPlugin {
                override fun openForServer(pluginContext: PluginContext, args: Args): OpenServerPlugin =
                    BeatLinkPlugin(beatSource, pluginContext)
            }
        }

        private val beatDataTopic = PubSub.Topic("plugins/$id/beatData", BeatData.serializer())
    }

    /** Copy beat data from [beatSource] to a bridge PubSub channel. */
    class BeatLinkBridgePlugin(
        private val beatSource: BeatSource,
        pluginContext: PluginContext
    ) : OpenBridgePlugin {
        private val channel = pluginContext.pubSub.openChannel(beatDataTopic, unknownBpm) { }

        init {
            beatSource.addObserver { channel.onChange(it.getBeatData()) }
        }
    }

    class PubSubPublisher(
        beatSource: BeatSource,
        pluginContext: PluginContext
    ) : Observable(), BeatSource {
        private var beatData: BeatData = beatSource.getBeatData()

        val channel = pluginContext.pubSub.openChannel(beatDataTopic, beatData) {
            logger.warn { "BeatData update from client? Huh?" }
            beatData = it
            notifyChanged()
        }

        init {
            beatSource.addObserver {
                val newBeatData = it.getBeatData()
                beatData = newBeatData
                notifyChanged()
                channel.onChange(newBeatData)
            }
        }

        override fun getBeatData(): BeatData = beatData

    }

    class PubSubSubscriber(
        pubSub: PubSub.Endpoint,
        defaultBeatData: BeatData = unknownBpm
    ) : Observable(), BeatSource {
        private var beatData: BeatData = defaultBeatData

        init {
            pubSub.openChannel(beatDataTopic, beatData) {
                beatData = it
                notifyChanged()
            }
        }

        override fun getBeatData(): BeatData = beatData

    }
}

internal expect fun createServerBeatSource(pluginContext: PluginContext): BeatSource
