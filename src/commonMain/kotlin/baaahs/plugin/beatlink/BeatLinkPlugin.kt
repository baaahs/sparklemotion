package baaahs.plugin.beatlink

import baaahs.Color
import baaahs.PubSub
import baaahs.app.ui.CommonIcons
import baaahs.gl.patch.ContentType
import baaahs.plugin.*
import baaahs.show.Feed
import baaahs.show.FeedBuilder
import baaahs.sim.BridgeClient
import baaahs.ui.Facade
import baaahs.ui.Observable
import baaahs.util.Logger
import baaahs.util.globalLaunch
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.delay
import kotlin.random.Random

class BeatLinkPlugin internal constructor(
    beatSource: BeatSource,
    pluginContext: PluginContext
) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = id
    override val title: String = "Beat Link"

    private val clock = pluginContext.clock

    internal val facade = BeatLinkFacade(beatSource)

    // We'll just make one up-front. We only ever want one (because equality
    // is using object identity), and there's no overhead.
    internal val beatLinkFeed = BeatLinkFeed(facade, clock)
    private val beatInfoFeed = BeatInfoFeed(facade, clock)
    private val rawBeatInfoFeed = RawBeatInfoFeed(facade)
    private val waveformsFeed = WaveformsFeed(facade, clock)
    private val playerDataFeed = PlayerDataFeed(facade, clock)

    override val addControlMenuItems: List<AddControlMenuItem>
        get() = listOf(
            AddControlMenuItem("New BeatLink Control…", CommonIcons.BeatLinkControl) {
                MutableBeatLinkControl()
            }
        )
    override val contentTypes: List<ContentType>
        get() = feedBuilders.map { it.contentType }

    override val controlSerializers
        get() = listOf(
            classSerializer(BeatLinkControl.serializer())
        )

    override val feedBuilders
        get() = listOf(
            beatLinkFeed.Builder(),
            beatInfoFeed.Builder(),
            rawBeatInfoFeed.Builder(),
            waveformsFeed.Builder(),
            playerDataFeed.Builder()
        )

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
        const val PLAYER_COUNT = 4

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
                        PubSubPublisher(SimBeatSource(bridgeClient, pluginContext), pluginContext),
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
        private val playerWaveformsTopic = PubSub.Topic("plugins/$id/playerWaveforms", PlayerWaveforms.serializer())
    }

    /** Stateful, [Observable] holder of BeatLink data. */
    class BeatLinkFacade(
        private val beatSource: BeatSource
    ) : Facade() {
        var beatData: BeatData = unknownBpm
            private set

        var playerWaveforms = PlayerWaveforms()
            private set

        private val listener = object : BeatLinkListener {
            override fun onBeatData(beatData: BeatData) {
                this@BeatLinkFacade.beatData = beatData
                notifyChanged()
            }

            override fun onWaveformUpdate(deviceNumber: Int, waveform: Waveform) {
                playerWaveforms = playerWaveforms.updateWith(deviceNumber, waveform)
                notifyChanged()
            }
        }

        init { beatSource.addListener(listener) }
        fun release() = beatSource.removeListener(listener)
    }

    /** Copy beat data from [beatSource] to a bridge PubSub channel. */
    class BeatLinkBridgePlugin(
        private val beatSource: BeatSource,
        pluginContext: PluginContext
    ) : OpenBridgePlugin {
        private var playerWaveforms = PlayerWaveforms()

        private val beatDataChannel = pluginContext.pubSub.openChannel(beatDataTopic, unknownBpm) { }
        private val playerWaveformsChannel = pluginContext.pubSub.openChannel(playerWaveformsTopic, playerWaveforms) { }

        init {
            beatSource.addListener(object : BeatLinkListener {
                override fun onBeatData(beatData: BeatData) {
                    beatDataChannel.onChange(beatData)
                }

                override fun onWaveformUpdate(deviceNumber: Int, waveform: Waveform) {
                    playerWaveforms = playerWaveforms.updateWith(deviceNumber, waveform)
                    playerWaveformsChannel.onChange(playerWaveforms)
                }
            })
        }
    }

    class PubSubPublisher(
        beatSource: BeatSource,
        pluginContext: PluginContext
    ) : BeatSource {
        private var playerWaveforms = PlayerWaveforms()

        private var beatLinkListeners = BeatLinkListeners()

        val beatDataChannel = pluginContext.pubSub.openChannel(beatDataTopic, BeatSource.None.none) {
            error("BeatData update from client? Huh?")
        }
        val playerWaveformsChannel = pluginContext.pubSub.openChannel(playerWaveformsTopic, playerWaveforms) {
            error("PlayerWaveforms update from client? Huh?")
        }

        init {
            beatSource.addListener(object : BeatLinkListener {
                override fun onBeatData(beatData: BeatData) {
                    beatLinkListeners.notifyListeners { it.onBeatData(beatData) }
                    beatDataChannel.onChange(beatData)
                }

                override fun onWaveformUpdate(deviceNumber: Int, waveform: Waveform) {
                    playerWaveforms = playerWaveforms.updateWith(deviceNumber, waveform)
                    playerWaveformsChannel.onChange(playerWaveforms)
                }
            })
        }

        override fun addListener(listener: BeatLinkListener) { beatLinkListeners.addListener(listener) }
        override fun removeListener(listener: BeatLinkListener) { beatLinkListeners.removeListener(listener) }
    }

    open class PubSubSubscriber(
        pubSub: PubSub.Endpoint,
        defaultBeatData: BeatData = unknownBpm
    ) : BeatSource {
        protected val listeners = BeatLinkListeners()

        init {
            pubSub.openChannel(beatDataTopic, defaultBeatData) { beatData ->
                listeners.notifyListeners { it.onBeatData(beatData) }
            }
            pubSub.openChannel(playerWaveformsTopic, PlayerWaveforms()) { playerWaveforms ->
                listeners.notifyListeners {
                    playerWaveforms.byDeviceNumber.forEach { (deviceNumber, waveform) ->
                        it.onWaveformUpdate(deviceNumber, waveform)
                    }
                }
            }
        }

        override fun addListener(listener: BeatLinkListener) { listeners.addListener(listener) }
        override fun removeListener(listener: BeatLinkListener) { listeners.removeListener(listener) }
    }

    class SimBeatSource(bridgeClient: BridgeClient, pluginContext: PluginContext) : PubSubSubscriber(
        bridgeClient.pubSub,
        simulatorDefaultBpm
    ) {
        init {
            globalLaunch {
                delay(1000)
                listeners.notifyListeners { it.onBeatData(simulatorDefaultBpm) }

                while (true) {
                    delay(10000)

                    val deviceNumber = Random.nextInt(4) + 1
                    val waveform = Waveform.Builder(pluginContext.clock.now()).apply {
                        for (i in 0 until Random.nextInt(1000)) {
                            add(Random.nextInt(32), Color(Random.nextInt(0xffffff)))
                        }
                    }.build()
                    listeners.notifyListeners {
                        it.onWaveformUpdate(deviceNumber, waveform)
                    }
                }
            }
        }
    }
}

internal expect fun createServerBeatSource(pluginContext: PluginContext): BeatSource
