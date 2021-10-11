package baaahs.di

import baaahs.*
import baaahs.dmx.Dmx
import baaahs.dmx.JvmFtdiDmxDriver
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.RealFs
import baaahs.model.Model
import baaahs.net.JvmNetwork
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.proto.Ports
import baaahs.server.PinkyArgs
import baaahs.util.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
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
        get() = newSingleThreadContext("Pinky Main")
    override val Scope.dmxDriver: Dmx.Driver
        get() = JvmFtdiDmxDriver
    override val Scope.model: Model
        get() = Pluggables.loadModel(get<PinkyArgs>().model)
    override val Scope.renderManager: RenderManager
        get() = runBlocking(get(named("PinkyContext"))) {
            RenderManager(get()) { GlBase.manager.createContext() }
        }
    override val Scope.pinkySettings: PinkySettings
        get() = PinkySettings()
}

class JvmSoundAnalysisPluginModule : SoundAnalysisPluginModule {
    override val soundAnalyzer: SoundAnalyzer
        get() = JvmSoundAnalyzer()

}