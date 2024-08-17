package baaahs.di

import baaahs.MediaDevices
import baaahs.PinkySettings
import baaahs.dmx.Dmx
import baaahs.dmx.JvmFtdiDmxDriver
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.net.JvmNetwork
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.sm.brain.DirectoryDaddy
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.brain.proto.Ports
import baaahs.sm.server.AiAssistantService
import baaahs.sm.server.OpenAiAssistantService
import baaahs.util.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.io.File

class JvmPlatformModule(
    private val clock_: Clock
) : PlatformModule {
    override val network: Network = JvmNetwork()
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
        get() = Plugins.buildForServer(get(), get(named(PluginsModule.Qualifier.ActivePlugins)), programName, startupArgs)
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
        get() = Dispatchers.Default.limitedParallelism(1)
    override val Scope.dmxDriver: Dmx.Driver
        get() = JvmFtdiDmxDriver
    override val Scope.pinkySettings: PinkySettings
        get() = PinkySettings()

    override val Scope.aiAssistant: AiAssistantService
        get() = OpenAiAssistantService(get(), get())
}
