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
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkBeatSource
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.beatlink.BeatSource
import baaahs.plugin.sound_analysis.AudioInput
import baaahs.plugin.sound_analysis.JvmSoundAnalysisPlatform
import baaahs.plugin.sound_analysis.SoundAnalysisPlatform
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin
import baaahs.proto.Ports
import baaahs.util.Clock
import baaahs.util.SystemClock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.io.File

class JvmPlatformModule(private val args: PinkyMain.Args) : PlatformModule {
    override val network: Network = JvmNetwork()
    override val Scope.clock: Clock
        get() = SystemClock
    override val Scope.model: Model
        get() = Pluggables.loadModel(args.model)
    override val Scope.pluginContext
        get() = PluginContext(get())
    override val Scope.plugins: Plugins
        get() = Plugins.safe(get()) + get<BeatLinkPlugin.BeatLinkPluginBuilder>() + get<SoundAnalysisPlugin.SoundAnalysisPluginBuilder>()
    override val Scope.mediaDevices: MediaDevices
        get() = object : MediaDevices {
            override suspend fun enumerate(): List<MediaDevices.Device> = emptyList()
            override fun getCamera(selectedDevice: MediaDevices.Device?): MediaDevices.Camera =
                TODO("not implemented")
        }
}

class JvmPinkyModule : PinkyModule {
    private val dataDir = File(System.getProperty("user.home")).toPath().resolve("sparklemotion/data")

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

    @ObsoleteCoroutinesApi
    override val Scope.pinkyMainDispatcher: CoroutineDispatcher
        get() = newSingleThreadContext("Pinky Main")
    override val Scope.dmxDriver: Dmx.Driver
        get() = JvmFtdiDmxDriver
    override val Scope.renderManager: RenderManager
        get() = runBlocking(get(named("PinkyContext"))) {
            RenderManager(get()) { GlBase.manager.createContext() }
        }
    override val Scope.pinkySettings: PinkySettings
        get() = PinkySettings()
}

class JvmBeatLinkPluginModule(private val args: PinkyMain.Args) : BeatLinkPluginModule {
    override val Scope.beatSource: BeatSource
        get() = if (args.enableBeatLink) {
            BeatLinkBeatSource(get()).also { it.start() }
        } else {
            BeatSource.None
        }
}

class JvmSoundAnalysisPluginModule(
    private val args: PinkyMain.Args,
    override val audioInput: AudioInput
) : SoundAnalysisPluginModule {
    override val soundAnalysisPlatform: SoundAnalysisPlatform
        get() = JvmSoundAnalysisPlatform()
}