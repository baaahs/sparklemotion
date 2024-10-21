package baaahs.plugin.beatlink

import baaahs.PubSub
import baaahs.app.ui.CommonIcons
import baaahs.gl.patch.ContentType
import baaahs.plugin.*
import baaahs.sim.BridgeClient
import baaahs.ui.Facade
import baaahs.ui.Observable
import baaahs.util.Logger

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
            rawBeatInfoFeed.Builder()
        )

    companion object : Plugin, SimulatorPlugin {
        private val logger = Logger<BeatLinkPlugin>()

        override val id = "baaahs.BeatLink"
        const val PLAYER_COUNT = 4

        private val unknownBpm = BeatData(0.0, 500, confidence = 0f)

        override fun openForServer(pluginContext: PluginContext): OpenServerPlugin {
            val beatSource = createServerBeatSource(pluginContext)
            return openForServer(pluginContext, beatSource)
        }

        fun openForServer(
            pluginContext: PluginContext,
            beatSource: BeatSource
        ): BeatLinkPlugin {
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

        fun forTest(beatSource: BeatSource): Plugin {
            return object : Plugin by BeatLinkPlugin {
                override fun openForServer(pluginContext: PluginContext): OpenServerPlugin =
                    BeatLinkPlugin(beatSource, pluginContext)
            }
        }

        private val beatDataTopic = PubSub.Topic("plugins/$id/beatData", BeatData.serializer())
        private val playerStateTopic = PubSub.Topic("plugins/$id/playerState", PlayerStates.serializer())
    }

    /** Stateful, [Observable] holder of BeatLink data. */
    class BeatLinkFacade(
        private val beatSource: BeatSource
    ) : Facade() {
        var beatData: BeatData = unknownBpm
            private set

        var playerStates = PlayerStates()
            private set

        private val listener = object : BeatLinkListener {
            override fun onBeatData(beatData: BeatData) {
                this@BeatLinkFacade.beatData = beatData
                notifyChanged()
            }

            override fun onPlayerStateUpdate(deviceNumber: Int, playerState: PlayerState) {
                playerStates = playerStates.updateWith(deviceNumber, playerState)
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
        private var playerStates = PlayerStates()

        private val beatDataChannel = pluginContext.pubSub.openChannel(beatDataTopic, unknownBpm) { }
        private val playerWaveformsChannel = pluginContext.pubSub.openChannel(playerStateTopic, playerStates) { }

        init {
            beatSource.addListener(object : BeatLinkListener {
                override fun onBeatData(beatData: BeatData) {
                    beatDataChannel.onChange(beatData)
                }

                override fun onPlayerStateUpdate(deviceNumber: Int, playerState: PlayerState) {
                    playerStates = playerStates.updateWith(deviceNumber, playerState)
                    playerWaveformsChannel.onChange(playerStates)
                }
            })
        }
    }

    class PubSubPublisher(
        beatSource: BeatSource,
        pluginContext: PluginContext
    ) : BeatSource {
        private var playerStates = PlayerStates()

        private var beatLinkListeners = BeatLinkListeners()

        val beatDataChannel = pluginContext.pubSub.openChannel(beatDataTopic, BeatSource.None.none) {
            error("BeatData update from client? Huh?")
        }
        val playerStateChannel = pluginContext.pubSub.openChannel(playerStateTopic, playerStates) {
            error("PlayerState update from client? Huh?")
        }

        init {
            beatSource.addListener(object : BeatLinkListener {
                override fun onBeatData(beatData: BeatData) {
                    beatLinkListeners.notifyListeners { it.onBeatData(beatData) }
                    beatDataChannel.onChange(beatData)
                }

                override fun onPlayerStateUpdate(deviceNumber: Int, playerState: PlayerState) {
                    playerStates = playerStates.updateWith(deviceNumber, playerState)
                    playerStateChannel.onChange(playerStates)
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
            pubSub.openChannel(playerStateTopic, PlayerStates()) { playerWaveforms ->
                listeners.notifyListeners {
                    playerWaveforms.byDeviceNumber.forEach { (deviceNumber, waveform) ->
                        it.onPlayerStateUpdate(deviceNumber, waveform)
                    }
                }
            }
        }

        override fun addListener(listener: BeatLinkListener) { listeners.addListener(listener) }
        override fun removeListener(listener: BeatLinkListener) { listeners.removeListener(listener) }
    }

}

internal expect fun createServerBeatSource(pluginContext: PluginContext): BeatSource