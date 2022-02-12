package baaahs.di

import baaahs.MediaDevices
import baaahs.PinkySettings
import baaahs.SheepSimulator
import baaahs.browser.RealMediaDevices
import baaahs.dmx.Dmx
import baaahs.gl.GlBase
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.mapping.MappingManager
import baaahs.net.BrowserNetwork
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.plugin.SimulatorPlugins
import baaahs.scene.SceneMonitor
import baaahs.scene.SceneProvider
import baaahs.sim.*
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.brain.PermissiveFirmwareDaddy
import baaahs.visualizer.PixelArranger
import baaahs.visualizer.SwirlyPixelArranger
import baaahs.visualizer.Visualizer
import kotlinx.coroutines.CoroutineDispatcher
import org.koin.core.Koin
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope

class JsSimPlatformModule(fakeNetwork: FakeNetwork) : JsPlatformModule(fakeNetwork) {
    override val Scope.mediaDevices: MediaDevices
        get() = FakeMediaDevices(get(), RealMediaDevices())
}

class JsSimulatorModule(
    private val sceneMonitor_: SceneMonitor,
    private val fakeNetwork_: FakeNetwork,
    private val bridgeNetwork_: BrowserNetwork,
    private val pinkyAddress_: Network.Address,
    private val pixelDensity: Float = 0.2f,
    private val pixelSpacing: Float = 2f,
    private val simMappingManager: SimMappingManager
) : SimulatorModule {
    override val Scope.fs: Fs
        get() {
            return MergedFs(
                BrowserSandboxFs("Browser Data", "data"),
                get(named(SimulatorModule.Qualifier.MapperFs)),
                name = "Browser Data"
            )
        }
    override val Scope.fakeNetwork: FakeNetwork
        get() = fakeNetwork_

    override fun getModule(): Module {
        return super.getModule().apply {
            single { Visualizer(get()) }
            single<SceneProvider> { sceneMonitor_ }
            single { simMappingManager }
            single<PixelArranger> { SwirlyPixelArranger(pixelDensity, pixelSpacing) }
            single { BridgeClient(bridgeNetwork_, pinkyAddress_) }
            single { Plugins.buildForSimulator(get(), get(named(PluginsModule.Qualifier.ActivePlugins))) }
            single { (plugins: Plugins) ->
                FixturesSimulator(
                    get(), get(), get(), get(named("Fallback")),
                    get(), get(), get()
                )
            }
            single(named(SimulatorModule.Qualifier.PinkyLink)) { get<Network>().link("pinky") }
            single { (koin: Koin) -> SheepSimulator(get(), get()) { koin } }
        }
    }
}

class JsSimPinkyModule(
    private val sceneMonitor_: SceneMonitor,
    private val pinkySettings_: PinkySettings,
    private val pinkyMainDispatcher_: CoroutineDispatcher,
    private val simMappingManager: SimMappingManager
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
        get() = pinkyMainDispatcher_
    override val Scope.pinkyLink: Network.Link
        get() = get(named(SimulatorModule.Qualifier.PinkyLink))
    override val Scope.backupMappingManager: MappingManager
        get() = simMappingManager
    override val Scope.dmxDriver: Dmx.Driver
        get() = SimDmxDriver(get(named("Fallback")))
    override val Scope.renderManager: RenderManager
        get() = RenderManager { GlBase.manager.createContext() }
    override val Scope.pinkySettings: PinkySettings
        get() = pinkySettings_
    override val Scope.sceneMonitor: SceneMonitor
        get() = sceneMonitor_

    override fun getModule(): Module {
        return super.getModule()
    }
}
