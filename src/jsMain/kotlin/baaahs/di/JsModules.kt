package baaahs.di

import baaahs.*
import baaahs.browser.RealMediaDevices
import baaahs.client.WebClient
import baaahs.gl.RootToolchain
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.beatlink.BeatSource
import baaahs.plugin.core.CorePlugin
import baaahs.sim.FakeMediaDevices
import baaahs.util.Clock
import baaahs.util.JsClock
import org.koin.core.module.Module
import org.koin.core.scope.Scope
import org.koin.dsl.module

open class JsPlatformModule(
    override val network: Network,
    private val model_: Model
) : PlatformModule {
    override val Scope.clock: Clock
        get() = JsClock
    override val Scope.model: Model
        get() = model_
    override val Scope.pluginContext: PluginContext
        get() = PluginContext(get())
    override val Scope.plugins: Plugins
        get() = Plugins(listOf(CorePlugin, get<BeatLinkPlugin.Builder>()), get())
    override val Scope.mediaDevices: MediaDevices
        get() = RealMediaDevices()
}

interface WebClientModule : KModule

class JsWebClientModule(
    private val pinkyAddress: Network.Address
) : WebClientModule {
    override fun getModule(): Module = module {
        scope<WebClient> {
            scoped { get<Network>().link("app") }
            scoped { WebClient(get(), pinkyAddress, RootToolchain(get())) }
        }
    }
}

class JsMapperClientModule(
    private val pinkyAddress: Network.Address
) : KModule {
    override fun getModule(): Module = module {
        scope<AdminUi> {
            scoped { get<Network>().link("admin") }
            scoped { AdminUi(get(), pinkyAddress, get(), get()) }
        }

        scope<MapperUi> {
            scoped { get<Network>().link("mapper") }
            scoped {
                JsMapperUi().also {
                    // This has side-effects on mapperUi. Ugly.
                    Mapper(get(), get(), it, get(), pinkyAddress, get())
                }
            }
        }
    }
}

class JsBeatLinkPluginModule(private val beatSource_: BeatSource) : BeatLinkPluginModule {
    override val Scope.beatSource: BeatSource
        get() = beatSource_
}

//class JsSoundAnalysisPluginModule : SoundAnalysisPluginModule {
//    override val soundAnalyzer: SoundAnalyzer
//        get() =
//
//}