package baaahs.plugin.midi

import baaahs.PubSub
import baaahs.ShowPlayer
import baaahs.gl.GlContext
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
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
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.serialization.SerialName

class MidiPlugin internal constructor(
    internal val midiSource: MidiSource,
) : OpenServerPlugin, OpenClientPlugin {
    override val packageName: String = MidiPlugin.id
    override val title: String = "Midi"

    internal val midiFeed = MidiFeed()

    override val contentTypes: List<ContentType>
        get() = listOf(
            midiContentType
        )

    override val feedBuilders
        get() = listOf(
            MidiFeedBuilder()
        )

    inner class MidiFeedBuilder : FeedBuilder<MidiFeed> {
        override val title: String get() = "Midi"
        override val description: String get() = "A struct containing information about the midi events."
        override val resourceName: String get() = "Midi"
        override val contentType: ContentType get() = midiContentType
        override val serializerRegistrar
            get() = objectSerializer("$id:Midi", midiFeed)

        override fun looksValid(inputPort: InputPort, suggestedContentTypes: Set<ContentType>): Boolean =
            inputPort.contentType == midiContentType
                    || suggestedContentTypes.contains(midiContentType)
                    || inputPort.type == midiStruct

        override fun build(inputPort: InputPort): MidiFeed = midiFeed
    }


    @SerialName("baaahs.Midi:Midi")
    inner class MidiFeed internal constructor() : Feed {
        override val pluginPackage: String get() = id
        override val title: String get() = "Midi"
        override fun getType(): GlslType = midiStruct
        override val contentType: ContentType
            get() = midiContentType

        override fun open(showPlayer: ShowPlayer, id: String): FeedContext {
            val varPrefix = getVarName(id)
            return object : FeedContext, RefCounted by RefCounter() {
                override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                    override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                        return object : ProgramFeedContext {
                            val sustainPedalCount = glslProgram.getUniform("${varPrefix}.sustainPedalCount")
                            val noteCount = glslProgram.getUniform("${varPrefix}.noteCount")
                            override val isValid: Boolean
                                get() = sustainPedalCount != null && noteCount != null

                            override fun setOnProgram() {
                                val midiData = midiSource.getMidiData()

                                sustainPedalCount?.set(midiData.sustainPedalCount)
                                noteCount?.set(midiData.noteCount)
                            }
                        }
                    }
                }
            }
        }
    }

    class ParserArgs(parser: ArgParser) : Args {
        override val enableMidi by parser.option(ArgType.Boolean, description = "Enable midi detection")
            .default(true)
    }

    interface Args {
        val enableMidi: Boolean
        val midiSource: MidiSource? get() = null
    }

    companion object : Plugin<Args>, SimulatorPlugin {
        private val logger = Logger<MidiPlugin>()

        override val id = "baaahs.Midi"

        val midiStruct = GlslType.Struct(
            "Midi",
            "sustainPedalCount" to GlslType.Int,
            "noteCount" to GlslType.Int
        )

        val midiContentType = ContentType("midi", "Midi", midiStruct)

        private val simulatorDefaultMidi = MidiData(0, 0)
        private val unknownMidi = MidiData(0, 0)

        override fun getArgs(parser: ArgParser): Args = ParserArgs(parser)

        override fun openForServer(pluginContext: PluginContext, args: Args): OpenServerPlugin {
            val midiSource = if (args.enableMidi) {
                args.midiSource ?: createServerMidiSource(pluginContext)
            } else MidiSource.None
            return MidiPlugin(
                PubSubPublisher(midiSource, pluginContext)
            )
        }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin =
            MidiPlugin(PubSubSubscriber(pluginContext.pubSub))

        override fun openForSimulator(): OpenSimulatorPlugin =
            object : OpenSimulatorPlugin {
                override fun getBridgePlugin(pluginContext: PluginContext): OpenBridgePlugin =
                    MidiBridgePlugin(createServerMidiSource(pluginContext), pluginContext)

                override fun getServerPlugin(pluginContext: PluginContext, bridgeClient: BridgeClient) =
                    MidiPlugin(
                        PubSubPublisher(
                            PubSubSubscriber(bridgeClient.pubSub, simulatorDefaultMidi),
                            pluginContext
                        )
                    )

                override fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin =
                    openForClient(pluginContext)
            }

        private val midiDataTopic = PubSub.Topic("plugins/$id/midiData", MidiData.serializer())
    }

    /** Copy beat data from [midiSource] to a bridge PubSub channel. */
    class MidiBridgePlugin(
        private val midiSource: MidiSource,
        pluginContext: PluginContext
    ) : OpenBridgePlugin {
        private val channel = pluginContext.pubSub.openChannel(midiDataTopic, unknownMidi) { }

        init {
            midiSource.addObserver { channel.onChange(it.getMidiData()) }
        }
    }

    class PubSubPublisher(
        midiSource: MidiSource,
        pluginContext: PluginContext
    ) : Observable(), MidiSource {
        private var midiData: MidiData = midiSource.getMidiData()

        val channel = pluginContext.pubSub.openChannel(midiDataTopic, midiData) {
            logger.warn { "MidiData update from client? Huh?" }
            midiData = it
            notifyChanged()
        }

        init {
            midiSource.addObserver {
                val newMidiData = it.getMidiData()
                midiData = newMidiData
                notifyChanged()
                channel.onChange(newMidiData)
            }
        }

        override fun getMidiData(): MidiData = midiData

    }

    class PubSubSubscriber(
        pubSub: PubSub.Endpoint,
        defaultMidiData: MidiData = unknownMidi
    ) : Observable(), MidiSource {
        private var midiData: MidiData = defaultMidiData

        init {
            pubSub.openChannel(midiDataTopic, midiData) {
                midiData = it
                notifyChanged()
            }
        }

        override fun getMidiData(): MidiData = midiData

    }
}

internal expect fun createServerMidiSource(pluginContext: PluginContext): MidiSource
