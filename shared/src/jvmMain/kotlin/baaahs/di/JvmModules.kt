package baaahs.di

import baaahs.MediaDevices
import baaahs.PinkySettings
import baaahs.app.settings.DocumentFeatureFlags
import baaahs.app.settings.FeatureFlags
import baaahs.dmx.Dmx
import baaahs.dmx.JvmFtdiDmxDriver
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.libraries.ShaderLibraryManager
import baaahs.net.JvmNetwork
import baaahs.net.Network
import baaahs.plugin.Plugin
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.plugin.midi.JvmMidiSource
import baaahs.plugin.midi.MidiManager
import baaahs.sm.brain.DirectoryDaddy
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.brain.proto.Ports
import baaahs.sm.server.ExceptionReporter
import baaahs.sm.server.PinkyArgs
import baaahs.sm.server.PinkyArgs.Subcommand
import baaahs.util.Clock
import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.default
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.io.File
import java.util.concurrent.Executors

class JvmPlatformModule(
    override val exceptionReporter: ExceptionReporter,
    private val clock_: Clock
) : PlatformModule {
    override val networkDispatcher: CoroutineDispatcher = Dispatchers.IO
    override val Scope.network: Network
        get() = JvmNetwork(get(PlatformModule.Named.networkScope), exceptionReporter)
    override val Scope.clock: Clock
        get() = clock_
    override val Scope.mediaDevices: MediaDevices
        get() = object : MediaDevices {
            override suspend fun enumerate(): List<MediaDevices.Device> = emptyList()
            override fun getCamera(selectedDevice: MediaDevices.Device?): MediaDevices.Camera =
                TODO("not implemented")
        }
}

class JvmPinkyModule(
    private val programName: String,
    private val startupArgs: Array<String>
) : PinkyModule {
    private val dataDir = File(System.getProperty("user.home")).toPath().resolve("sparklemotion/data")

    override val Scope.serverPlugins: ServerPlugins
        get() {
            val plugins = get<List<Plugin>>(named(PluginsModule.Qualifier.ActivePlugins))

            val parser = ArgParser(programName)
            val pinkyArgs = ParserPinkyArgs(parser)
            parser.parse(startupArgs)

            return Plugins.buildForServer(get(), plugins, pinkyArgs)
        }
    override val Scope.fs: Fs
        get() = RealFs("Sparkle Motion Data", dataDir)
    override val Scope.firmwareDir: Fs.File
        get() = RealFs(
            "Sparkle Motion Firmware",
            File(System.getProperty("user.home")).toPath()
        ).resolve("sparklemotion/fw")

    override val Scope.firmwareDaddy: FirmwareDaddy
        get() {
            val fwDir = get<Fs.File>(named("firmwareDir"))
            val link = get<Network.Link>()
            val myAddress = link.myAddress as JvmNetwork.IpAddress
            val fwUrlBase = "http://${myAddress.address.hostAddress}:${Ports.PINKY_UI_TCP}/fw"
            return DirectoryDaddy(fwDir, fwUrlBase)
        }

    override val Scope.pinkyMainDispatcher: CoroutineDispatcher
        get() = Executors.newSingleThreadExecutor { runnable ->
            Thread(runnable, "Pinky Main")
        }.asCoroutineDispatcher()
    // Dispatchers.Default.limitedParallelism(1, "Pinky Main")
    override val Scope.dmxDriver: Dmx.Driver
        get() = JvmFtdiDmxDriver
    override val Scope.midiManager: MidiManager
        get() = MidiManager(listOf(JvmMidiSource(get())))
    override val Scope.pinkySettings: PinkySettings
        get() = PinkySettings()
    override val Scope.featureFlags: FeatureFlags
        get() = FeatureFlags.JVM
}

class ParserPinkyArgs(
    parser: ArgParser
) : PinkyArgs {
    override val sceneName by parser.option(ArgType.String, shortName = "m")

    override val showName by parser.option(ArgType.String, "show", "s")

    override val switchShowAfter by parser.option(ArgType.Int, description = "Switch show after no input for x seconds")

    override val adjustShowAfter by parser.option(
        ArgType.Int,
        description = "Start adjusting show inputs after no input for x seconds"
    )

    override val simulateBrains by parser.option(ArgType.Boolean, description = "Simulate connected brains")
        .default(false)

    override var subcommand: Subcommand? = null
        private set

    init {
        parser.subcommands(IndexShaderLibrary())
    }

    inner class IndexShaderLibrary : kotlinx.cli.Subcommand(
        "index-shader-library",
        "Generate an index for a shader library."
    ), Subcommand {
        val libraryName by argument(ArgType.String)

        override fun execute() { subcommand = this }

        override suspend fun Scope.execute() {
            get<ShaderLibraryManager>().buildIndex(libraryName)
        }
    }
}