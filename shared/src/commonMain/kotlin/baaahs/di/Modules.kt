package baaahs.di

import baaahs.*
import baaahs.app.settings.FeatureFlags
import baaahs.app.settings.FeatureFlagsManager
import baaahs.app.settings.Provider
import baaahs.client.EventManager
import baaahs.controller.ControllersManager
import baaahs.controller.SacnManager
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.dmx.DmxManagerImpl
import baaahs.dmx.DmxUniverseListener
import baaahs.fixtures.FixtureManager
import baaahs.fixtures.FixtureManagerImpl
import baaahs.fixtures.FixturePublisher
import baaahs.gl.GlBase
import baaahs.gl.RootToolchain
import baaahs.gl.Toolchain
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.io.FsServerSideSerializer
import baaahs.io.ResourcesFs
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.MappingStore
import baaahs.mapper.PinkyMapperHandlers
import baaahs.mapping.MappingManager
import baaahs.mapping.MappingManagerImpl
import baaahs.model.ModelManager
import baaahs.model.ModelManagerImpl
import baaahs.net.Network
import baaahs.plugin.Plugin
import baaahs.plugin.PluginContext
import baaahs.plugin.Plugins
import baaahs.plugin.ServerPlugins
import baaahs.plugin.midi.MidiManager
import baaahs.scene.SceneMonitor
import baaahs.scene.SceneProvider
import baaahs.show.ShowMonitor
import baaahs.show.ShowProvider
import baaahs.sim.FakeDmxUniverse
import baaahs.sim.FakeFs
import baaahs.sim.FakeNetwork
import baaahs.sim.MergedFs
import baaahs.sim.SimulatorSettingsManager
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.brain.ProdBrainSimulator
import baaahs.sm.brain.proto.Ports
import baaahs.sm.server.*
import baaahs.util.Clock
import kotlinx.coroutines.*
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.core.scope.Scope
import org.koin.dsl.module

class PluginsModule(private val plugins: List<Plugin>) : KModule {
    override fun getModule(): Module = module {
        single(named(Qualifier.ActivePlugins)) { plugins }
    }

    enum class Qualifier {
        ActivePlugins
    }
}

interface PlatformModule : KModule {
    val exceptionReporter: ExceptionReporter
    val networkDispatcher: CoroutineDispatcher
    val Scope.network: Network
    val Scope.clock: Clock
    val Scope.mediaDevices: MediaDevices

    object Named {
        val networkScope = named("NetworkScope")
        val fallback = named("Fallback")
    }

    override fun getModule(): Module = module {
        single(Named.networkScope) {
            CoroutineScope(
                networkDispatcher +
                        CoroutineName("Network") +
                        CoroutineExceptionHandler { context, throwable ->
                            exceptionReporter.reportException(context, throwable)
                        })
        }
        single { exceptionReporter }
        single { network }
        single { clock }
        single { mediaDevices }
        single(Named.fallback) { FakeDmxUniverse() }
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
    val Scope.midiManager: MidiManager
    val Scope.pinkySettings: PinkySettings
    val Scope.sceneMonitor: SceneMonitor get() = SceneMonitor()
    val Scope.pinkyMapperHandlers: PinkyMapperHandlers get() = PinkyMapperHandlers(get())
    val Scope.featureFlags: FeatureFlags

    /**
     * On the server side, client resources are at htdocs/..., except
     * for in the simulator (see [baaahs.di.JsSimulatorModules]).
     */
    val Scope.resourcesFs: ResourcesFs get() = ResourcesFs("htdocs/")

    object Named {
        val pinkyScope = named("PinkyScope")
        val pinkyJob = named("PinkyJob")
        val fallbackDmxUniverse = PlatformModule.Named.fallback
        val dataDir = named("dataDir")
        val firmwareDir = named("firmwareDir")
    }

    override fun getModule(): Module = module {
        scope<Pinky> {
            scoped { fs }
            scoped { PluginContext(get(), get()) }
            scoped { serverPlugins }
            scoped<Plugins> { get<ServerPlugins>() }
            scoped { get<ServerPlugins>().pinkyArgs }
            scoped(Named.firmwareDir) { firmwareDir }
            scoped(Named.dataDir) { fs.rootFile }
            scoped { firmwareDaddy }
            scoped { pinkyLink }
            scoped(pinkyMainDispatcher) { this.pinkyMainDispatcher }
            scoped<Job>(Named.pinkyJob) { SupervisorJob() }
            scoped(Named.pinkyScope) {
                val exceptionReporter = get<ExceptionReporter>()
                CoroutineScope(
                    get<CoroutineDispatcher>(PinkyModule.pinkyMainDispatcher) +
                            CoroutineName("Pinky Main Scope") +
                            get<Job>(Named.pinkyJob) +
                            CoroutineExceptionHandler { context, throwable ->
                                exceptionReporter.reportException(context, throwable)
                            }
                )
            }
            scoped { PubSub.Server(get(), get(Named.pinkyScope)) }
            scoped<PubSub.Endpoint> { get<PubSub.Server>() }
            scoped<PubSub.IServer> { get<PubSub.Server>() }
            scoped { dmxDriver }
            scoped { midiManager }
            scoped { ShowMonitor() }
            scoped<ShowProvider> { get<ShowMonitor>() }
            scoped { EventManager(get(), get(), get()) }
            scoped { DmxUniverseListener(get()) }
            scoped<Dmx.UniverseListener> { get<DmxUniverseListener>() }
            scoped<DmxManager> { DmxManagerImpl(get(), get(), get(Named.fallbackDmxUniverse), get(), get(), get()) }
            scoped(named("PinkyGlContext")) {
                GlBase.manager.createContext("Pinky Render Context", SparkleMotion.TRACE_GLSL)
            }
            scoped { RenderManager(get(named("PinkyGlContext"))) }
            scoped { get<Network.Link>().createHttpServer(Ports.PINKY_UI_TCP) }
            scoped { FsServerSideSerializer() }
            scoped { MappingStore(get(Named.dataDir), get(), get()) }
            scoped<FixtureManager> { FixtureManagerImpl(get(), get()) }
            scoped { GadgetManager(get(), get(), get(Named.pinkyScope)) }
            scoped<Toolchain> { RootToolchain(get()) }
            scoped { PinkyConfigStore(get(), fs.resolve(".")) }
            scoped<Provider<FeatureFlags>> { FeatureFlagsManager.Server(get(), get()) }
            scoped { StageManager(get(), get(), get(), get(Named.dataDir), get(),get(), get(), get(), get(), get(), get(), get(), get()) }
            scoped { Pinky.NetworkStats() }
            scoped { BrainManager(get(), get(), get(), get(), get(Named.pinkyScope)) }
            scoped { SacnManager(get(), get(), get(), get(Named.pinkyScope), get(PlatformModule.Named.networkScope)) }
            scoped { sceneMonitor }
            scoped<SceneProvider> { get<SceneMonitor>() }
            scoped<MappingManager> {
                MappingManagerImpl(get(), get(), get(Named.pinkyScope), backupMappingManager)
            }
            scoped<ModelManager> { ModelManagerImpl() }
            scoped(named("ControllerManagers")) {
                listOf(
                    get<BrainManager>(), get<DmxManager>(), get<SacnManager>()
                )
            }
            scoped { FixturePublisher(get(), get()) }
            scoped {
                ControllersManager(
                    get(named("ControllerManagers")), get(), get(),
                    listOf(
                        get<FixtureManager>(),
                        get<FixturePublisher>(),
                    ),
                    get(), get()
                )
            }
            scoped { ProdBrainSimulator(get(), get()) }
            scoped { ShaderLibraryManager(get(), MergedFs(get(), ResourcesFs()), get(), get(), get()) }
            scoped { pinkySettings }
            scoped { ServerNotices(get(), get<CoroutineScope>(Named.pinkyScope).coroutineContext) }
            scoped { PinkyMapperHandlers(get()) }
            scoped { featureFlags }
            scoped { resourcesFs }
            scoped {
                Pinky(
                    get(), get(), get(), get(Named.dataDir), get(), get(),
                    get(), get(), get(), get(), get(Named.pinkyScope), get(), get(),
                    get(), get(), get(), get(), get(), get(),
                    pinkyMapperHandlers, get(), get(), get(), get()
                )
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
        single { SimulatorSettingsManager(get(named(Qualifier.PinkyFs))) }
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

