package baaahs.di

import baaahs.MediaDevices
import baaahs.PubSub
import baaahs.app.settings.FeatureFlags
import baaahs.app.settings.FeatureFlagsManager
import baaahs.app.settings.Provider
import baaahs.app.ui.PatchEditorApp
import baaahs.app.ui.dialog.FileDialog
import baaahs.browser.RealMediaDevices
import baaahs.client.*
import baaahs.client.document.IFileDialog
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.RootToolchain
import baaahs.gl.Toolchain
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.io.RemoteFsSerializer
import baaahs.mapper.JsMapperBuilder
import baaahs.monitor.MonitorUi
import baaahs.net.Network
import baaahs.plugin.ClientPlugins
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.midi.MidiManager
import baaahs.scene.SceneMonitor
import baaahs.scene.SceneProvider
import baaahs.show.ShowMonitor
import baaahs.show.ShowProvider
import baaahs.sim.BrowserSandboxFs
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.SimulationEnv
import baaahs.sm.brain.proto.Ports
import baaahs.util.Clock
import baaahs.util.JsClock
import baaahs.visualizer.Visualizer
import baaahs.visualizer.remote.RemoteVisualizerClient
import baaahs.visualizer.sim.PixelArranger
import baaahs.visualizer.sim.SwirlyPixelArranger
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

open class JsPlatformModule(
    override val network: Network
) : PlatformModule {
    override val Scope.clock: Clock
        get() = JsClock
    override val Scope.mediaDevices: MediaDevices
        get() = RealMediaDevices()

}

class JsStandaloneWebClientModule(
    private val pinkyAddress: Network.Address
) : KModule {
    override fun getModule(): Module = module {
        single(named(WebClientModule.Qualifier.PinkyAddress)) { pinkyAddress }
    }
}

open class JsUiWebClientModule : WebClientModule() {
    override fun getModule(): Module = module {
        scope<WebClient> {
            scoped { get<Network>().link("app") }
            scoped { PluginContext(get(), get()) }
            scoped { PubSub.Client(get(), get(named(Qualifier.PinkyAddress)), Ports.PINKY_UI_TCP) }
            scoped<PubSub.Endpoint> { get<PubSub.Client>() }
            scoped { Plugins.buildForClient(get(), get(named(PluginsModule.Qualifier.ActivePlugins))) }
            scoped<Plugins> { get<ClientPlugins>() }
            scoped { ClientStorage(BrowserSandboxFs("Browser Local Storage")) }
            scoped<Toolchain> { RootToolchain(get()) }
            scoped { WebClient(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
            scoped { ClientStageManager(get(), get(), get()) }
            scoped<RemoteFsSerializer> { PubSubRemoteFsClientBackend(get()) }
            scoped { FileDialog() }
            scoped<IFileDialog> { get<FileDialog>() }
            scoped<Provider<FeatureFlags>> { FeatureFlagsManager.Client(get()) }
            scoped { ShowManager(get(), get(), get(), get(), get(), get(), get(), get()) }
            scoped { SceneMonitor() }
            scoped { SceneManager(get(), get(), get(), get(), get(), get(), get()) }
            scoped<SceneProvider> { get<SceneMonitor>() }
            scoped { Notifier(get()) }
            scoped { SceneEditorClient(get(), get()) }
            scoped {
                JsMapperBuilder(get(), get(), get(), null, get(), get(), get(), get(named(Qualifier.PinkyAddress)), get(), get())
            }
            scoped { EventManager(get(), get(), get()) }
            scoped { ShowMonitor() }
            scoped<ShowProvider> { get<ShowMonitor>() }
            // Uncomment this for MIDI via the browser (which only works on desktops).
            scoped { MidiManager(listOf(/*JsMidiSource(get())*/)) }

            // Dev only:
            scoped { PatchEditorApp(get(), get(), get()) }
        }
    }
}

class JsMonitorWebClientModule : KModule {
    override fun getModule(): Module = module {
        scope<MonitorUi> {
            scoped { get<Network>().link("monitor") }
            scoped { PluginContext(get(), get()) }
            scoped { PubSub.Client(get(), pinkyAddress(), Ports.PINKY_UI_TCP) }
            scoped<PubSub.Endpoint> { get<PubSub.Client>() }
            scoped { Plugins.buildForClient(get(), get(named(PluginsModule.Qualifier.ActivePlugins))) }
            scoped<Plugins> { get<ClientPlugins>() }
            scoped<RemoteFsSerializer> { PubSubRemoteFsClientBackend(get()) }
            scoped { SceneManager(get(), get(), get(), get(), get(), get(), get()) }
            scoped { SceneMonitor() }
            scoped<SceneProvider> { get<SceneMonitor>() }
            scoped { FileDialog() }
            scoped<IFileDialog> { get<FileDialog>() }
            scoped { Notifier(get()) }
            scoped { Visualizer(get()) }
            scoped {
                val simulationEnv = SimulationEnv {
                    component(get<Clock>())
                    component(FakeDmxUniverse())
                    component<PixelArranger>(SwirlyPixelArranger(0.2f, 3f))
                }
                RemoteVisualizerClient(get(), pinkyAddress(), get<Visualizer>(), get(), get(), simulationEnv, get())
            }
            scoped { MonitorUi(get(), get()) }
        }
    }

    private fun Scope.pinkyAddress(): Network.Address =
        get(named(WebClientModule.Qualifier.PinkyAddress))
}