package baaahs.di

import baaahs.MediaDevices
import baaahs.admin.AdminClient
import baaahs.browser.RealMediaDevices
import baaahs.client.ClientStorage
import baaahs.client.WebClient
import baaahs.gl.RootToolchain
import baaahs.mapper.JsMapperUi
import baaahs.mapper.Mapper
import baaahs.mapper.MapperUi
import baaahs.model.Model
import baaahs.monitor.MonitorUi
import baaahs.net.Network
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.beatlink.BeatSource
import baaahs.plugin.sound_analysis.AudioInput
import baaahs.plugin.sound_analysis.SoundAnalysisPlatform
import baaahs.plugin.sound_analysis.SoundAnalysisPlugin
import baaahs.plugin.sound_analysis.SoundAnalyzer
import baaahs.sim.BrowserSandboxFs
import baaahs.util.Clock
import baaahs.util.JsClock
import baaahs.visualizer.Visualizer
import baaahs.visualizer.remote.RemoteVisualizerClient
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
        get() = Plugins.safe(get()) + get<BeatLinkPlugin.BeatLinkPluginBuilder>() + get<SoundAnalysisPlugin.SoundAnalysisPluginBuilder>()
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
            scoped { ClientStorage(BrowserSandboxFs("Browser Local Storage"))  }
            scoped { WebClient(get(), pinkyAddress, get(), get(), get(), RootToolchain(get())) }
        }
    }
}

class JsAdminClientModule(
    private val pinkyAddress: Network.Address
) : KModule {
    override fun getModule(): Module = module {
        scope<MapperUi> {
            scoped { get<Network>().link("mapper") }
            scoped {
                val adminClient = AdminClient(get(), pinkyAddress)
                JsMapperUi(adminClient).also {
                    // This has side-effects on mapperUi. Ugly.
                    Mapper(get(), get(), it, get(), pinkyAddress, get())
                }
            }
        }

        scope<MonitorUi> {
            scoped { get<Network>().link("monitor") }
            scoped { Visualizer(get(), get()) }
            scoped { RemoteVisualizerClient(get(), pinkyAddress, get(), get(), get(), get()) }
            scoped { MonitorUi(get(), get()) }
        }
    }
}

class JsBeatLinkPluginModule(private val beatSource_: BeatSource) : BeatLinkPluginModule {
    override val Scope.beatSource: BeatSource
        get() = beatSource_
}

class JsSoundAnalysisPluginModule : SoundAnalysisPluginModule {
    override val audioInput: AudioInput
        get() = object : AudioInput {
            override val id: String
                get() = "fake"
            override val title: String
                get() = "fake"
        }
    override val soundAnalysisPlatform: SoundAnalysisPlatform
        get() = object : SoundAnalysisPlatform {
            override suspend fun listAudioInputs(): List<AudioInput> {
                return emptyList()
            }

            override fun createConstantQAnalyzer(audioInput: AudioInput, sampleRate: Float): SoundAnalyzer {
                return object : SoundAnalyzer {
                    override val numberOfBuckets: Int
                        get() = 1

                    override fun listen(analysisListener: SoundAnalyzer.AnalysisListener) {
                    }

                    override fun unlisten(analysisListener: SoundAnalyzer.AnalysisListener) {
                        TODO("not implemented")
                    }
                }
            }
        }
}