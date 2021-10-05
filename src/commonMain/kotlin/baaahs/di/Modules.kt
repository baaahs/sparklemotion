package baaahs.di

import baaahs.*
import baaahs.controller.ControllersManager
import baaahs.controller.SacnManager
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.dmx.DmxManagerImpl
import baaahs.fixtures.FixtureManager
import baaahs.gl.RootToolchain
import baaahs.gl.Toolchain
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.Storage
import baaahs.mapping.MappingManager
import baaahs.mapping.MappingManagerImpl
import baaahs.model.Model
import baaahs.model.ModelInfo
import baaahs.model.ModelManager
import baaahs.model.ModelManagerImpl
import baaahs.net.Network
import baaahs.plugin.Plugin
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.proto.Ports
import baaahs.scene.SceneManager
import baaahs.server.ServerNotices
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

class PluginsModule(private val plugins: List<Plugin<*>>) : KModule {
    override fun getModule(): Module = module {
        single(named(Qualifier.ActivePlugins)) { plugins }
    }

    enum class Qualifier {
        ActivePlugins
    }
}

interface PlatformModule : KModule {
    val network: Network
    val Scope.clock: Clock
    val Scope.pluginContext: PluginContext
    val Scope.mediaDevices: MediaDevices

    override fun getModule(): Module = module {
        single { network }
        single { clock }
        single { pluginContext }
        single { mediaDevices }
        single(named("Fallback")) { FakeDmxUniverse() }
    }

}

interface PinkyModule : KModule {
    val Scope.serverPlugins: ServerPlugins
    val Scope.fs: Fs
    val Scope.firmwareDir: Fs.File
    val Scope.firmwareDaddy: FirmwareDaddy
    val Scope.pinkyMainDispatcher: CoroutineDispatcher
    val Scope.pinkyLink: Network.Link get() = get<Network>().link("pinky")
    val Scope.dmxDriver: Dmx.Driver
    val Scope.model: Model
    val Scope.renderManager: RenderManager
    val Scope.pinkySettings: PinkySettings

    override fun getModule(): Module {
        val pinkyContext = named("PinkyContext")
        val pinkyMainDispatcher = named("PinkyMainDispatcher")
        val pinkyJob = named("PinkyJob")
        val fallbackDmxUniverse = named("Fallback")

        return module {
            scope<Pinky> {
                scoped { fs }
                scoped { serverPlugins }
                scoped { get<ServerPlugins>().pinkyArgs }
                scoped<Plugins> { get<ServerPlugins>() }
                scoped(named("firmwareDir")) { firmwareDir }
                scoped { firmwareDaddy }
                scoped { this.pinkyLink }
                scoped(pinkyMainDispatcher) { this.pinkyMainDispatcher }
                scoped<Job>(pinkyJob) { SupervisorJob() }
                scoped(pinkyContext) {
                    get<CoroutineDispatcher>(pinkyMainDispatcher) + get<Job>(pinkyJob)
                }
                scoped { PubSub.Server(get(), CoroutineScope(get(pinkyContext))) }
                scoped<PubSub.IServer> { get<PubSub.Server>() }
                scoped { dmxDriver }
                scoped<ModelInfo> { get<Model>() }
                scoped<DmxManager> { DmxManagerImpl(get(), get(), get(fallbackDmxUniverse)) }
                scoped { model }
                scoped { renderManager }
                scoped { get<Network.Link>().startHttpServer(Ports.PINKY_UI_TCP) }
                scoped { Storage(get(), get()) }
                scoped { FixtureManager(get(), get()) }
                scoped { GadgetManager(get(), get(), get(pinkyContext)) }
                scoped<Toolchain> { RootToolchain(get()) }
                scoped { StageManager(get(), get(), get(), get(), get(), get(), get(), get(), get(), get()) }
                scoped { Pinky.NetworkStats() }
                scoped { BrainManager(get(), get(), get(), get(), get(), get(pinkyContext)) }
                scoped { SacnManager(get(), get(), get(pinkyMainDispatcher), get()) }
                scoped { SceneManager(get(), get()) }
                scoped<MappingManager> { MappingManagerImpl(get(), get()) }
                scoped<ModelManager> { ModelManagerImpl() }
                scoped(named("ControllerManagers")) {
                    listOf(
                        get<BrainManager>(), get<DmxManager>(), get<SacnManager>()
                    )
                }
                scoped { ControllersManager(get(named("ControllerManagers")), get(), get(), get<FixtureManager>()) }
                scoped { ShaderLibraryManager(get(), get()) }
                scoped { pinkySettings }
                scoped { ServerNotices(get(), get(pinkyContext)) }
                scoped {
                    Pinky(
                        get(), get(), get(), get(), get(), get(),
                        get(), get(), get(), get(), get(pinkyContext), get(), get(),
                        get(), get(), get(), get(), get(), get(), get()
                    )
                }
            }
        }
    }
}

interface SoundAnalysisPluginModule : KModule {
    val soundAnalyzer: SoundAnalyzer

    override fun getModule(): Module = module {
        single { soundAnalyzer }
    }
}

interface SimulatorModule : KModule {
    val Scope.model: Model
    val Scope.fs: Fs

    override fun getModule(): Module = module {
        single { model }
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

