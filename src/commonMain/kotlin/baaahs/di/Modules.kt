package baaahs.di

import baaahs.MediaDevices
import baaahs.Pinky
import baaahs.PinkySettings
import baaahs.PubSub
import baaahs.controller.ControllersManager
import baaahs.controller.ControllersPublisher
import baaahs.controller.SacnManager
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.dmx.DmxManagerImpl
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.FixtureManagerImpl
import baaahs.fixtures.FixturePublisher
import baaahs.gl.RootToolchain
import baaahs.gl.Toolchain
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.Storage
import baaahs.mapping.MappingManager
import baaahs.mapping.MappingManagerImpl
import baaahs.model.ModelManager
import baaahs.model.ModelManagerImpl
import baaahs.net.Network
import baaahs.plugin.Plugin
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.scene.SceneMonitor
import baaahs.scene.SceneProvider
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sim.FakeNetwork
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.brain.ProdBrainSimulator
import baaahs.sm.brain.proto.Ports
import baaahs.sm.server.GadgetManager
import baaahs.sm.server.ServerNotices
import baaahs.sm.server.StageManager
import baaahs.util.Clock
import baaahs.util.coroutineExceptionHandler
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
    val Scope.mediaDevices: MediaDevices

    override fun getModule(): Module = module {
        single { network }
        single { clock }
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
    val Scope.backupMappingManager: MappingManager? get() = null
    val Scope.dmxDriver: Dmx.Driver
    val Scope.renderManager: RenderManager
    val Scope.pinkySettings: PinkySettings
    val Scope.sceneMonitor: SceneMonitor get() = SceneMonitor()

    override fun getModule(): Module {
        val pinkyContext = named("PinkyContext")
        val pinkyJob = named("PinkyJob")
        val fallbackDmxUniverse = named("Fallback")

        return module {
            scope<Pinky> {
                scoped { fs }
                scoped { PluginContext(get(), get()) }
                scoped { serverPlugins }
                scoped<Plugins> { get<ServerPlugins>() }
                scoped { get<ServerPlugins>().pinkyArgs }
                scoped(named("firmwareDir")) { firmwareDir }
                scoped { firmwareDaddy }
                scoped { pinkyLink }
                scoped(pinkyMainDispatcher) { this.pinkyMainDispatcher }
                scoped<Job>(pinkyJob) { SupervisorJob() }
                scoped(pinkyContext) {
                    get<CoroutineDispatcher>(PinkyModule.pinkyMainDispatcher) +
                            get<Job>(pinkyJob) +
                            coroutineExceptionHandler
                }
                scoped { PubSub.Server(get(), CoroutineScope(get(pinkyContext))) }
                scoped<PubSub.Endpoint> { get<PubSub.Server>() }
                scoped<PubSub.IServer> { get<PubSub.Server>() }
                scoped { dmxDriver }
                scoped<DmxManager> { DmxManagerImpl(get(), get(), get(fallbackDmxUniverse)) }
                scoped { renderManager }
                scoped { get<Network.Link>().startHttpServer(Ports.PINKY_UI_TCP) }
                scoped { Storage(get(), get()) }
                scoped<FixtureManager> { FixtureManagerImpl(get(), get()) }
                scoped { GadgetManager(get(), get(), get(pinkyContext)) }
                scoped<Toolchain> { RootToolchain(get()) }
                scoped { StageManager(get(), get(), get(), get(), get(), get(), get(), get(), get()) }
                scoped { Pinky.NetworkStats() }
                scoped { BrainManager(get(), get(), get(), get(), get(pinkyContext)) }
                scoped { SacnManager(get(), get(PinkyModule.pinkyMainDispatcher), get()) }
                scoped { sceneMonitor }
                scoped<SceneProvider> { get<SceneMonitor>() }
                scoped<MappingManager> {
                    MappingManagerImpl(get(), get(), CoroutineScope(get(pinkyContext)), backupMappingManager)
                }
                scoped<ModelManager> { ModelManagerImpl() }
                scoped(named("ControllerManagers")) {
                    listOf(
                        get<BrainManager>(), get<DmxManager>(), get<SacnManager>()
                    )
                }
                scoped { FixturePublisher(get(), get()) }
                scoped { ControllersPublisher(get(), get()) }
                scoped {
                    ControllersManager(
                        get(named("ControllerManagers")), get(), get(),
                        listOf(
                            get<FixtureManager>(),
                            get<FixturePublisher>(),
                        ),
                        listOf(
                            get<ControllersPublisher>()
                        )
                    )
                }
                scoped { ProdBrainSimulator(get(), get()) }
                scoped { ShaderLibraryManager(get(), get()) }
                scoped { pinkySettings }
                scoped { ServerNotices(get(), get(pinkyContext)) }
                scoped {
                    Pinky(
                        get(), get(), get(), get(), get(), get(),
                        get(), get(), get(), get(), get(pinkyContext), get(), get(),
                        get(), get(), get(), get(), get(), get()
                    )
                }
            }
        }
    }

    companion object {
        val pinkyMainDispatcher = named("PinkyMainDispatcher")
    }
}

abstract class WebClientModule : KModule {
    enum class Qualifier {
        PinkyAddress
    }
}

interface SimulatorModule : KModule {
    val Scope.fs: Fs
    val Scope.fakeNetwork: FakeNetwork

    override fun getModule(): Module = module {
        single { fakeNetwork }
        single(named(Qualifier.PinkyFs)) { fs }
        single(named(Qualifier.MapperFs)) { FakeFs("Temporary Mapping Files") }
        single<Fs>(named(Qualifier.MapperFs)) { get<FakeFs>(named(Qualifier.MapperFs)) }
        single(named(WebClientModule.Qualifier.PinkyAddress)) { get<Network.Link>(named(Qualifier.PinkyLink)).myAddress }
    }

    enum class Qualifier {
        PinkyFs,
        MapperFs,
        PinkyLink
    }
}

interface KModule {
    fun getModule(): Module
}

