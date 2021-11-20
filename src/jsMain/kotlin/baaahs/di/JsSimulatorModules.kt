package baaahs.di

import baaahs.MediaDevices
import baaahs.ModelProvider
import baaahs.PinkySettings
import baaahs.SheepSimulator
import baaahs.browser.RealMediaDevices
import baaahs.dmx.Dmx
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.ResourcesFs
import baaahs.net.BrowserNetwork
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.plugin.SimulatorPlugins
import baaahs.sim.*
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.brain.PermissiveFirmwareDaddy
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

class JsSimPlatformModule : JsPlatformModule(FakeNetwork()) {
    override val Scope.mediaDevices: MediaDevices
        get() = FakeMediaDevices(get(), RealMediaDevices())
}

class JsSimulatorModule(
    private val modelProvider_: ModelProvider,
    private val bridgeNetwork_: BrowserNetwork,
    private val pinkyAddress_: Network.Address,
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
    override val Scope.modelProvider: ModelProvider
        get() = modelProvider_

    override fun getModule(): Module {
        return super.getModule().apply {
            single { Visualizer(get(), get()) }
            single<PixelArranger> { SwirlyPixelArranger(pixelDensity, pixelSpacing) }
            single { BridgeClient(bridgeNetwork_, pinkyAddress_) }
            single { Plugins.buildForSimulator(get(), get(named(PluginsModule.Qualifier.ActivePlugins))) }
            single { (plugins: Plugins) ->
                FixturesSimulator(
                    get(), get(), get(), get(named("Fallback")),
                    get(named(SimulatorModule.Qualifier.PinkyFs)),
                    get(named(SimulatorModule.Qualifier.MapperFs)),
                    get(), plugins, get()
                )
            }
            single(named(SimulatorModule.Qualifier.PinkyLink)) { get<Network>().link("pinky") }
            single { (koin: Koin) -> SheepSimulator(get(), get()) { koin } }
        }
    }
}

class JsSimPinkyModule(
    private val modelProvider_: ModelProvider,
    private val pinkySettings_: PinkySettings
) : PinkyModule {
    override val Scope.serverPlugins: ServerPlugins
        get() = get<SimulatorPlugins>().openServerPlugins(get())
    override val Scope.fs: Fs
        get() = get(named(SimulatorModule.Qualifier.PinkyFs))
    override val Scope.firmwareDir: Fs.File
        get() = TODO("not implemented")
    override val Scope.firmwareDaddy: FirmwareDaddy
        get() = PermissiveFirmwareDaddy()
    override val Scope.pinkyMainDispatcher: CoroutineDispatcher
        get() = Dispatchers.Main
    override val Scope.pinkyLink: Network.Link
        get() = get(named(SimulatorModule.Qualifier.PinkyLink))
    override val Scope.dmxDriver: Dmx.Driver
        get() = SimDmxDriver(get(named("Fallback")))
    override val Scope.modelProvider: ModelProvider
        get() = modelProvider_
    override val Scope.renderManager: RenderManager
        get() = RenderManager(get()) { GlBase.manager.createContext() }
    override val Scope.pinkySettings: PinkySettings
        get() = pinkySettings_
}
