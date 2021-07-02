package baaahs.di

import baaahs.*
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.model.Model
import baaahs.net.Network
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.beatlink.BeatLinkPlugin
import baaahs.plugin.beatlink.BeatSource
import baaahs.proto.Ports
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.util.Clock
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

interface PlatformModule : KModule {
    val network: Network
    val Scope.clock: Clock
    val Scope.model: Model
    val Scope.pluginContext: PluginContext
    val Scope.plugins: Plugins
    val Scope.mediaDevices: MediaDevices

    override fun getModule(): Module = module {
        single { network }
        single { clock }
        single { model }
        single { pluginContext }
        single { plugins }
        single { mediaDevices }
        single(named("Fallback")) { FakeDmxUniverse() }
    }
}

interface PinkyModule : KModule {
    val Scope.fs: Fs
    val Scope.firmwareDir: Fs.File
    val Scope.firmwareDaddy: FirmwareDaddy
    val Scope.pinkyMainDispatcher: CoroutineDispatcher
    val Scope.pinkyLink: Network.Link get() = get<Network>().link("pinky")
    val Scope.dmxDriver: Dmx.Driver
    val Scope.renderManager: RenderManager

    override fun getModule(): Module = module {
        scope<Pinky> {
            scoped { fs }
            scoped(named("firmwareDir")) { firmwareDir }
            scoped { firmwareDaddy }
            scoped(named("PinkyLink")) { pinkyLink }
            scoped(named("PinkyMainDispatcher")) { pinkyMainDispatcher }
            scoped<Job>(named("PinkyJob")) { SupervisorJob() }
            scoped(named("PinkyContext")) {
                get<CoroutineDispatcher>(named("PinkyMainDispatcher")) + get<Job>(named("PinkyJob"))
            }
            scoped { PubSub.Server(get(), CoroutineScope(get(named("PinkyContext")))) }
            scoped { dmxDriver }
            scoped { DmxManager(get(), get(), get(named("Fallback"))) }
            scoped { renderManager }
            scoped { get<Network.Link>(named("PinkyLink")).startHttpServer(Ports.PINKY_UI_TCP) }
            scoped {
                Pinky(
                    get(), get(), get(), get(), get(),
                    get(), get(),
                    pinkyMainDispatcher = get(named("PinkyMainDispatcher")),
                    link = get(named("PinkyLink")),
                    httpServer = get(),
                    pubSub = get(),
                    dmxManager = get()
                )
            }
        }
    }
}

interface BeatLinkPluginModule : KModule {
    val Scope.beatSource: BeatSource

    override fun getModule(): Module = module {
        single { beatSource }
        single { BeatLinkPlugin.Builder(get()) }
    }
}

interface SoundAnalysisPluginModule : KModule {
    val soundAnalyzer: SoundAnalyzer

    override fun getModule(): Module = module {
        single { soundAnalyzer }
    }
}

interface SimulatorModule : KModule {
    val Scope.fs: Fs

    override fun getModule(): Module = module {
        single(named(Qualifier.PinkyFs)) { fs }
        single(named(Qualifier.MapperFs)) { FakeFs("Temporary Mapping Files") }
        single<Fs>(named(Qualifier.MapperFs)) { get<FakeFs>(named(Qualifier.MapperFs)) }
    }

    enum class Qualifier {
        PinkyFs,
        MapperFs
    }
}

interface KModule {
    fun getModule(): Module
}

enum class Scopes {
    Admin,
    WebClient
}

