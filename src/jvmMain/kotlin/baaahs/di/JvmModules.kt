package baaahs.di

import baaahs.*
import baaahs.dmx.Dmx
import baaahs.dmx.DmxDevice
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
import baaahs.proto.Ports
import baaahs.sim.FakeDmxUniverse
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.SystemClock
import kotlinx.coroutines.*
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import java.io.File
import kotlin.coroutines.CoroutineContext

class JvmPlatformModule(private val args: PinkyMain.Args) : PlatformModule {
    override val network: Network = JvmNetwork()
    override val Scope.clock: Clock
        get() = SystemClock
    override val Scope.model: Model
        get() = Pluggables.loadModel(args.model)
    override val Scope.pluginContext
        get() = PluginContext(get())
    override val Scope.plugins: Plugins
        get() = Plugins.safe(get()) + BeatLinkPlugin.Builder(get())
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
            val link = get<Network.Link>(named("PinkyLink"))
            val myAddress = link.myAddress as JvmNetwork.IpAddress
            val fwUrlBase = "http://${myAddress.address.hostAddress}:${Ports.PINKY_UI_TCP}/fw"
            return DirectoryDaddy(fwDir, fwUrlBase)
        }

    @ObsoleteCoroutinesApi
    override val Scope.pinkyMainDispatcher: CoroutineDispatcher
        get() = newSingleThreadContext("Pinky Main")
    override val Scope.dmxUniverse: Dmx.Universe
        get() = findDmxUniverse()
    override val Scope.renderManager: RenderManager
        get() = runBlocking(get(named("PinkyContext"))) {
            RenderManager(get()) { GlBase.manager.createContext() }
        }

    private fun findDmxUniverse(): Dmx.Universe {
        val dmxDevices = try {
            DmxDevice.listDevices()
        } catch (e: UnsatisfiedLinkError) {
            logger.warn { "DMX driver not found, DMX will be disabled." }
            e.printStackTrace()
            return FakeDmxUniverse()
        }

        if (dmxDevices.isNotEmpty()) {
            if (dmxDevices.size > 1) {
                logger.warn { "Multiple DMX USB devices found, using ${dmxDevices.first()}." }
            }

            return dmxDevices.first()
        }

        logger.warn { "No DMX USB devices found, DMX will be disabled." }
        return FakeDmxUniverse()
    }

    companion object {
        private val logger = Logger<JvmPinkyModule>()
    }
}

class JvmBeatLinkPluginModule(private val args: PinkyMain.Args) : BeatLinkPluginModule {
    override val Scope.beatSource: BeatSource
        get() = if (args.enableBeatLink) {
            BeatLinkBeatSource(get()).also { it.start() }
        } else {
            BeatSource.None
        }

}

class JvmSoundAnalysisPluginModule(private val args: PinkyMain.Args) : SoundAnalysisPluginModule {
    override val soundAnalyzer: SoundAnalyzer
        get() = JvmSoundAnalyzer()

}