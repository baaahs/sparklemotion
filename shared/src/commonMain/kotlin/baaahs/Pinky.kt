package baaahs

import baaahs.api.ws.WebSocketRouter
import baaahs.app.settings.DocumentFeatureFlags
import baaahs.app.settings.FeatureFlags
import baaahs.app.settings.Provider
import baaahs.client.EventManager
import baaahs.client.document.showStore
import baaahs.controller.ControllersManager
import baaahs.dmx.DmxManager
import baaahs.fixtures.FixtureManager
import baaahs.gl.Toolchain
import baaahs.gl.glsl.CompilationException
import baaahs.io.Fs
import baaahs.io.ResourcesFs
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.PinkyMapperHandlers
import baaahs.mapping.MappingManager
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.scene.Scene
import baaahs.show.Show
import baaahs.sm.brain.BrainManager
import baaahs.sm.brain.FirmwareDaddy
import baaahs.sm.server.DocumentService
import baaahs.sm.server.PinkyConfigStore
import baaahs.sm.server.ServerNotices
import baaahs.sm.server.StageManager
import baaahs.sm.webapi.Topics
import baaahs.util.Clock
import baaahs.util.Framerate
import baaahs.util.Logger
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable

class Pinky(
    val clock: Clock,
    val firmwareDaddy: FirmwareDaddy,
    val plugins: Plugins,
//    private val switchShowAfterIdleSeconds: Int? = 600,
//    private val adjustShowAfterIdleSeconds: Int? = null,
    private val dataDir: Fs.File,
    private val link: Network.Link,
    val httpServer: Network.HttpServer,
    pubSub: PubSub.Server,
    private val dmxManager: DmxManager,
    private val mappingManager: MappingManager,
    internal val fixtureManager: FixtureManager,
    private val pinkyMainScope: CoroutineScope,
    val toolchain: Toolchain,
    val stageManager: StageManager,
    private val controllersManager: ControllersManager,
    val brainManager: BrainManager,
    private val shaderLibraryManager: ShaderLibraryManager,
    private val networkStats: NetworkStats,
    private val pinkySettings: PinkySettings,
    private val serverNotices: ServerNotices,
    private val pinkyMapperHandlers: PinkyMapperHandlers,
    private val pinkyConfigStore: PinkyConfigStore,
    private val eventManager: EventManager,
    private val featureFlagsProvider: Provider<FeatureFlags>,
    private val resourcesFs: ResourcesFs
) {
    private var daemonJobs: Job? = null
    val facade = Facade()

    fun switchTo(newShow: Show?, file: Fs.File? = null) {
        stageManager.switchTo(newShow, file = file)
    }

    fun switchToScene(newScene: Scene?, file: Fs.File? = null) {
        stageManager.switchToScene(newScene, file)
    }

    val address: Network.Address get() = link.myAddress

    private var pinkyState = PinkyState.Initializing
    private val pinkyStateChannel = pubSub.publish(Topics.pinkyState, pinkyState) {}
    private var mapperIsRunning = false
    private var isPaused = false

    init {
        httpServer.listenWebSocket("/ws/api") {
            WebSocketRouter(plugins, pinkyMainScope) { pinkyMapperHandlers.register(this) }
        }

        httpServer.listenWebSocket("/ws/visualizer") {
            fixtureManager.newRemoteVisualizerServer()
        }

        fixtureManager.addFrameListener(controllersManager)
    }

    private var keepRunning = true

    suspend fun indexShaderLibrary(libraryName: String) {
        shaderLibraryManager.buildIndex(libraryName)
    }

    suspend fun startAndRun(beforeRun: suspend CoroutineScope.() -> Unit = {}) {
        val startupJobs = pinkyMainScope.launch(CoroutineName("Pinky Startup Jobs")) {
            launchStartupJobs()
        }

        val daemonJobs = pinkyMainScope.launch(CoroutineName("Pinky Daemon Jobs")) {
            launchDaemonJobs()
        }.also { this@Pinky.daemonJobs = it }

        startupJobs.join()

        pinkyMainScope.beforeRun()
        logger.info { "Pinky is up!" }

        run()
        daemonJobs.cancelAndJoin()
    }

    private suspend fun run() {
        while (keepRunning) {
            throttle(pinkySettings.targetFramerate) {
                if (mapperIsRunning || isPaused) {
                    disableDmx()
                    return@throttle
                }

                networkStats.reset()
                val elapsedMs = time {
                    try {
                        stageManager.renderAndSendNextFrame()
                    } catch (e: Exception) {
                        logger.error(e) { "Error rendering frame for ${stageManager.facade.currentShow?.title}"}
                        if (e is CompilationException) {
                            e.source?.let { logger.info { it } }
                        }
                        delay(1000)
                    }
                }
                facade.framerate.elapsed(elapsedMs)

                maybeChangeThingsIfUsersAreIdle()
            }
        }
    }

    private var mappingResultsLoaderJob: Job? = null

    internal suspend fun CoroutineScope.launchStartupJobs() {
        launch(CoroutineName("Pre-Mapper Startup Jobs")) {
            launch { firmwareDaddy.start() }

            mappingResultsLoaderJob = launch { mappingManager.start() }

            launch {
                val config = pinkyConfigStore.load()
                val featureFlags = featureFlagsProvider.get()

                loadDocument(
                    stageManager.sceneDocumentService,
                    featureFlags.scenes,
                    "SparkleMotion.scene",
                    config?.runningScenePath
                ) { Scene.Empty }

                loadDocument(
                    stageManager.showDocumentService,
                    featureFlags.shows,
                    "SparkleMotion.sparkle",
                    config?.runningShowPath
                ) {
                    val template = resourcesFs.resolve("templates", "shows", "Default for iPad.sparkle")
                    plugins.showStore.load(template) ?: error("Failed to load template $template.")
                }
            }

            launch { eventManager.start() }
        }.join()

        brainManager.listenForMapperMessages { message ->
            logger.info { "Mapper isRunning=${message.isRunning}" }
            if (pinkyState == PinkyState.Running && message.isRunning) {
                pinkyState = PinkyState.Mapping
                pinkyStateChannel.onChange(PinkyState.Mapping)
                mapperIsRunning = true
            } else if (pinkyState == PinkyState.Mapping && !message.isRunning) {
                pinkyState = PinkyState.Running
                pinkyStateChannel.onChange(PinkyState.Running)
                mapperIsRunning = false
            }
        }

        // This needs to go last-ish, otherwise we start getting network traffic too early.
        controllersManager.start()

        updatePinkyState(PinkyState.Running)
    }

    private suspend fun <T : Any> loadDocument(
        documentService: DocumentService<T, *>,
        documentFeatureFlags: DocumentFeatureFlags,
        solitaryFileName: String,
        runningPath: String?,
        solitaryTemplate: suspend () -> T
    ) {
        if (documentFeatureFlags.multiDoc) {
            runningPath?.let { path ->
                val file = dataDir.resolve(path)
                documentService.loadAndSwitchTo(file)
            }
        } else {
            val singleFile = dataDir.resolve(solitaryFileName)
            if (!singleFile.exists()) {
                documentService.save(singleFile, solitaryTemplate())
            }
            documentService.loadAndSwitchTo(singleFile)
        }
    }

    private suspend fun <T : Any> DocumentService<T, *>.loadAndSwitchTo(file: Fs.File) {
        try {
            val doc = load(file)
            if (doc == null) {
                reportError("No ${documentType.title.lowercase()} found at $file.")
            } else {
                switchTo(doc, file = file)
            }
        } catch (e: Exception) {
            reportError("Failed to load ${documentType.title.lowercase()} at $file", e)
        }
    }

    internal suspend fun awaitMappingResultsLoaded() {
        // Gross hack to ensure mapping results have loaded before we start fixture simulators.
        while (mappingResultsLoaderJob == null) {
            delay(5)
            yield()
        }
        mappingResultsLoaderJob!!.join()
    }

    private fun updatePinkyState(newState: PinkyState) {
        pinkyState = newState
        pinkyStateChannel.onChange(newState)
    }

    private fun CoroutineScope.launchDaemonJobs() {
        launch(CoroutineName("Shader Library Manager")) {
            shaderLibraryManager.start()
        }

        launch(CoroutineName("Periodic Logging")) {
            while (true) {
                if (mapperIsRunning) {
                    logger.info { "Mapping ${brainManager.brainCount} brains." }
                } else {
                    stageManager.logStatus()
                    controllersManager.logStatus()
                }
                delay(10000)
            }
        }

        launch(CoroutineName("Framerate Logging")) {
            while (keepRunning) {
                delay(10000)
                logger.info { "Framerate: ${facade.framerate.summarize()}" }
            }
        }
    }

    suspend fun stop() {
        keepRunning = false
        daemonJobs?.cancelAndJoin()
        httpServer.stop()
    }

    private fun maybeChangeThingsIfUsersAreIdle() {
//        val now = DateTime.now()
//        val secondsSinceUserInteraction = now.minus(gadgetManager.lastUserInteraction).seconds
//        if (switchShowAfterIdleSeconds != null
//            && now.minus(selectedNewShowAt).seconds > switchShowAfterIdleSeconds
//            && secondsSinceUserInteraction > switchShowAfterIdleSeconds
//        ) {
////            TODO switchToShow(shows.random())
//            selectedNewShowAt = now
//        }
//
//        if (adjustShowAfterIdleSeconds != null
//            && secondsSinceUserInteraction > adjustShowAfterIdleSeconds
//        ) {
//            gadgetManager.adjustSomething()
//        }
    }

    private fun disableDmx() {
        dmxManager.allOff()
    }

    class NetworkStats(var bytesSent: Int = 0, var packetsSent: Int = 0) {
        internal fun reset() {
            bytesSent = 0
            packetsSent = 0
        }
    }

    private fun reportError(message: String, e: Exception? = null) {
        if (e == null) {
            logger.error { message }
            serverNotices.add(message)
        } else {
            logger.error(e) { message }
            serverNotices.add(message, e.message, e.stackTraceToString())
        }
    }

    companion object {
        val logger = Logger("Pinky")
    }

    inner class Facade : baaahs.ui.Facade() {
        val stageManager: StageManager.Facade
            get() = this@Pinky.stageManager.facade

        val fixtureManager
            get() = this@Pinky.fixtureManager.facade

        val networkStats: NetworkStats
            get() = this@Pinky.networkStats

        val brains: List<BrainManager.BrainController>
            get() = this@Pinky.brainManager.activeBrains.values.toList()

        val clock: Clock
            get() = this@Pinky.clock

        var isPaused: Boolean
            get() = this@Pinky.isPaused
            set(value) {
                logger.info { "Pinky: ${if (value) "paused" else "unpaused"}" }
                this@Pinky.isPaused = value
                notifyChanged()
            }

        val framerate = Framerate()
    }
}

data class PinkySettings(
    var targetFramerate: Float = 30f, // Frames per second
)

@Serializable
enum class PinkyState {
    Initializing,
    Running,
    Mapping,
    ShuttingDown
}