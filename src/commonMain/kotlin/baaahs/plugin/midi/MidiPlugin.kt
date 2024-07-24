package baaahs.plugin.midi

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
import baaahs.show.FeedOpenContext
import baaahs.sim.BridgeClient
import baaahs.sim.SimulatorSettingsManager
import baaahs.ui.Observable
import baaahs.ui.addObserver
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import baaahs.util.globalLaunch
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

class MidiPlugin internal constructor(
    internal val midiSystem: MidiSystem,
) : OpenServerPlugin, OpenClientPlugin {
    val midiSource = midiSystem.midiSources.firstOrNull() ?: MidiSource.None

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

        override fun open(feedOpenContext: FeedOpenContext, id: String): FeedContext {
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
            val midiSystem = if (args.enableMidi) {
                createMidiSystem(pluginContext)
            } else MidiSystem.None

            return MidiPlugin(midiSystem)
        }

        override fun openForClient(pluginContext: PluginContext): OpenClientPlugin =
            MidiPlugin(MidiSystem.None)

        override fun openForSimulator(
            simulatorSettingsManager: SimulatorSettingsManager
        ): OpenSimulatorPlugin =
            object : OpenSimulatorPlugin {
                private val midiHardwareSimulator = MidiHardwareSimulator(simulatorSettingsManager)

                override fun getBridgePlugin(pluginContext: PluginContext): OpenBridgePlugin? = null

                override fun getServerPlugin(pluginContext: PluginContext, bridgeClient: BridgeClient) =
                    openForServer(pluginContext, object : Args { override val enableMidi: Boolean get() = true })

                override fun getClientPlugin(pluginContext: PluginContext): OpenClientPlugin =
                    openForClient(pluginContext)

                override fun getHardwareSimulators(): List<HardwareSimulator> =
                    listOf(midiHardwareSimulator)
            }
    }
}

class MidiHardwareSimulator(
    private val simulatorSettingsManager: SimulatorSettingsManager
) : HardwareSimulator, MidiSystem, Observable() {
    override val title: String = "MIDI"

    override var midiSources: List<MidiSource> = emptyList()
        private set

    override suspend fun start() {
        logger.info { "Starting MIDI hardware simulator." }
        simulatorSettingsManager.addObserver(fireImmediately = true) {
            globalLaunch {
                onSettingsChange()
            }
        }
    }

    private fun onSettingsChange() {
        val config = simulatorSettingsManager.simSettings
            .getConfig(MidiPlugin.id, MidiHardwareSimulatorSettings.serializer())
            ?: MidiHardwareSimulatorSettings(emptyList())

        println("config = $config")
        midiSources = config.devices.map { SimMidiSource(it.name) }
    }

    companion object {
        private val logger = Logger<MidiHardwareSimulator>()
    }
}

@Serializable
data class MidiHardwareSimulatorSettings(
    val devices: List<SimMidiDevice>
)

@Serializable
data class SimMidiDevice(
    val name: String
)

class SimMidiSource(
    override val name: String
) : MidiSource, Observable() {
    override fun getMidiData(): MidiData {
        TODO("not implemented")
    }
}

internal expect fun createMidiSystem(pluginContext: PluginContext): MidiSystem
