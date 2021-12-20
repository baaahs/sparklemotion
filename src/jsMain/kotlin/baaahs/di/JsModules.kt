package baaahs.di

import baaahs.MediaDevices
import baaahs.ModelProvider
import baaahs.PubSub
import baaahs.app.ui.dialog.FileDialog
import baaahs.browser.RealMediaDevices
import baaahs.client.*
import baaahs.client.document.SceneManager
import baaahs.client.document.ShowManager
import baaahs.gl.RootToolchain
import baaahs.gl.Toolchain
import baaahs.io.PubSubRemoteFsClientBackend
import baaahs.io.RemoteFsSerializer
import baaahs.mapper.JsMapperUi
import baaahs.mapper.Mapper
import baaahs.mapper.MapperUi
import baaahs.monitor.MonitorUi
import baaahs.net.Network
import baaahs.plugin.ClientPlugins
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.sim.BrowserSandboxFs
import baaahs.sm.brain.proto.Ports
import baaahs.util.Clock
import baaahs.util.JsClock
import baaahs.visualizer.Visualizer
import baaahs.visualizer.remote.RemoteVisualizerClient
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
            scoped { ClientStorage(BrowserSandboxFs("Browser Local Storage"))  }
            scoped<Toolchain> { RootToolchain(get()) }
            scoped { WebClient(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
            scoped { ClientStageManager(get(), get(), get()) }
            scoped<RemoteFsSerializer> { PubSubRemoteFsClientBackend(get()) }
            scoped { FileDialog() }
            scoped { ShowManager(get(), get(), get(), get(), get(), get()) }
            scoped { SceneManager(get(), get(), get(), get(), get()) }
            scoped<ModelProvider> { get<SceneManager>() }
            scoped { Notifier(get()) }
            scoped { SceneEditorClient(get(), get()) }
            scoped {
                JsMapperUi(get()).also {
                    // This has side-effects on mapperUi. Ugly.
                    Mapper(get(), get(), it, get(), get(named(Qualifier.PinkyAddress)), get())
                }
            }
        }
    }
}

class JsAdminWebClientModule : KModule {
    override fun getModule(): Module = module {
        scope<MapperUi> {
            scoped { get<Network>().link("mapper") }
            scoped { PluginContext(get(), get()) }
            scoped { PubSub.Client(get(), pinkyAddress(), Ports.PINKY_UI_TCP) }
            scoped<PubSub.Endpoint> { get<PubSub.Client>() }
            scoped { Plugins.buildForClient(get(), get(named(PluginsModule.Qualifier.ActivePlugins))) }
            scoped<Plugins> { get<ClientPlugins>() }
            scoped { PubSub.Client(get(), get(named(WebClientModule.Qualifier.PinkyAddress)), Ports.PINKY_UI_TCP) }
            scoped { SceneManager(get(), get(), get(), get(), get()) }
            scoped<ModelProvider> { get<SceneManager>() }
            scoped { SceneEditorClient(get(), get()) }
            scoped {
                JsMapperUi(get()).also {
                    // This has side-effects on mapperUi. Ugly.
                    Mapper(get(), get(), it, get(), pinkyAddress(), get())
                }
            }
        }

        scope<MonitorUi> {
            scoped { get<Network>().link("monitor") }
            scoped { PluginContext(get(), get()) }
            scoped { PubSub.Client(get(), pinkyAddress(), Ports.PINKY_UI_TCP) }
            scoped<PubSub.Endpoint> { get<PubSub.Client>() }
            scoped { Plugins.buildForClient(get(), get(named(PluginsModule.Qualifier.ActivePlugins))) }
            scoped<Plugins> { get<ClientPlugins>() }
            scoped { SceneManager(get(), get(), get(), get(), get()) }
            scoped<ModelProvider> { get<SceneManager>() }
            scoped { Visualizer(get(), get()) }
            scoped { RemoteVisualizerClient(get(), pinkyAddress(), get(), get(), get(), get()) }
            scoped { MonitorUi(get(), get()) }
        }
    }

    private fun Scope.pinkyAddress(): Network.Address =
        get(named(WebClientModule.Qualifier.PinkyAddress))
}