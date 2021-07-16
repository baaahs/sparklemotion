package baaahs.di

import baaahs.FirmwareDaddy
import baaahs.MediaDevices
import baaahs.PermissiveFirmwareDaddy
import baaahs.PinkySettings
import baaahs.browser.RealMediaDevices
import baaahs.dmx.Dmx
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.ResourcesFs
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.beatlink.BeatSource
import baaahs.proto.Ports
import baaahs.sim.*
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

class JsSimPlatformModule(
    network: FakeNetwork = FakeNetwork(),
    model: Model
) : JsPlatformModule(network, model) {
    override val Scope.mediaDevices: MediaDevices
        get() = FakeMediaDevices(get(), RealMediaDevices())
}

class JsSimulatorModule(
    private val simHostName: String,
    private val pixelDensity: Float = 0.2f,
    private val pixelSpacing: Float = 2f
) : SimulatorModule {
    override val Scope.fs: Fs
        get() {
            val resourcesFs = ResourcesFs()
            return MergedFs(
                BrowserSandboxFs("Browser Data"),
                get(named(SimulatorModule.Qualifier.MapperFs)),
                resourcesFs,
                name = "Browser Data"
            )
        }

    override fun getModule(): Module {
        return super.getModule().apply {
            single { BridgeClient("$simHostName:${Ports.SIMULATOR_BRIDGE_TCP}") }
            single { Visualizer(get(), get()) }
            single<PixelArranger> { SwirlyPixelArranger(pixelDensity, pixelSpacing) }
            single {
                FixturesSimulator(
                    get(), get(), get(), get(named("Fallback")),
                    get(named(SimulatorModule.Qualifier.PinkyFs)),
                    get(named(SimulatorModule.Qualifier.MapperFs)),
                    get(), get(), get()
                )
            }
        }
    }
}

class JsSimBeatLinkPluginModule : BeatLinkPluginModule {
    override val Scope.beatSource: BeatSource
        get() = get<BridgeClient>().beatSource
}

class JsSimPinkyModule(
    private val pinkyLink_: Network.Link,
    private val pinkySettings_: PinkySettings
) : PinkyModule {
    override val Scope.fs: Fs
        get() = get(named(SimulatorModule.Qualifier.PinkyFs))

    override val Scope.firmwareDir: Fs.File
        get() = TODO("not implemented")

    override val Scope.firmwareDaddy: FirmwareDaddy
        get() = PermissiveFirmwareDaddy()
    override val Scope.pinkyMainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val Scope.pinkyLink: Network.Link
        get() = pinkyLink_
    override val Scope.dmxDriver: Dmx.Driver
        get() = SimDmxDriver(get(named("Fallback")))
    override val Scope.renderManager: RenderManager
        get() = run {
            val sharedContext = get<Visualizer>().getGlContext()
            RenderManager(get(), direct = true) { sharedContext }
        }
    override val Scope.pinkySettings: PinkySettings
        get() = pinkySettings_
}
