package baaahs

import baaahs.api.ws.WebSocketRouter
import baaahs.controller.ControllersManager
import baaahs.dmx.DmxManager
import baaahs.fixtures.FixtureManager
import baaahs.gl.Toolchain
import baaahs.gl.glsl.CompilationException
import baaahs.io.Fs
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.PinkyMapperHandlers
import baaahs.mapper.Storage
import baaahs.mapping.MappingManager
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.proto.BrainHelloMessage
import baaahs.scene.SceneManager
import baaahs.server.ServerNotices
import baaahs.show.Show
import baaahs.sim.FakeNetwork
import baaahs.util.Clock
import baaahs.util.Framerate
import baaahs.util.Logger
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

class Pinky(
    val clock: Clock,
    val firmwareDaddy: FirmwareDaddy,
    val plugins: Plugins,
//    private val switchShowAfterIdleSeconds: Int? = 600,
//    private val adjustShowAfterIdleSeconds: Int? = null,
    private val storage: Storage,
    private val link: Network.Link,
    val httpServer: Network.HttpServer,
    pubSub: PubSub.Server,
    private val dmxManager: DmxManager,
    private val mappingManager: MappingManager,
    internal val fixtureManager: FixtureManager,
    override val coroutineContext: CoroutineContext,
    val toolchain: Toolchain,
    val stageManager: StageManager,
    private val sceneManager: SceneManager,
    private val controllersManager: ControllersManager,
    val brainManager: BrainManager,
    private val shaderLibraryManager: ShaderLibraryManager,
    private val networkStats: NetworkStats,
    val pinkySettings: PinkySettings,
    val serverNotices: ServerNotices
) : CoroutineScope {
    val facade = Facade()

    fun switchTo(newShow: Show?, file: Fs.File? = null) {
        stageManager.switchTo(newShow, file = file)
    }

    val address: Network.Address get() = link.myAddress

    private var pinkyState = PinkyState.Initializing
    private val pinkyStateChannel = pubSub.publish(Topics.pinkyState, pinkyState) {}
    private var mapperIsRunning = false

    init {
        httpServer.listenWebSocket("/ws/api") {
            WebSocketRouter(coroutineContext) { PinkyMapperHandlers(storage).register(this) }
        }

        httpServer.listenWebSocket("/ws/visualizer") {
            fixtureManager.newRemoteVisualizerServer()
        }
    }

    private var keepRunning = true

    suspend fun startAndRun(simulateBrains: Boolean = false) {
        withContext(coroutineContext) {
            val startupJobs = launchStartupJobs()
            val daemonJobs = launchDaemonJobs()

            startupJobs.join()

            if (simulateBrains) addSimulatedBrains()

            run()
            daemonJobs.cancelAndJoin()
        }
    }

    private fun addSimulatedBrains() {
        val mappingInfos = mappingManager.getAllControllerMappings()
        mappingInfos.forEach { (controllerId, mappings) ->
            when (controllerId.controllerType) {
                BrainManager.controllerTypeName -> {
                    mappings.forEach { mapping ->
                        brainManager.foundBrain(
                            FakeNetwork.FakeAddress("Simulated Brain ${controllerId.id}"),
                            BrainHelloMessage(controllerId.id, mapping.entity!!.name, null, null),
                            isSimulatedBrain = true
                        )
                    }
                }
                else -> {
                    logger.error { "Unknown controller type for $controllerId." }
                }
            }
        }
    }

    private suspend fun run() {
        while (keepRunning) {
            throttle(pinkySettings.targetFramerate) {
                if (mapperIsRunning) {
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
                facade.notifyChanged()
                facade.framerate.elapsed(elapsedMs)

                maybeChangeThingsIfUsersAreIdle()
            }
        }
    }

    private var mappingResultsLoaderJob: Job? = null

    internal suspend fun launchStartupJobs(): Job {
        return CoroutineScope(coroutineContext).launch {
            CoroutineScope(coroutineContext).launch {
                launch { firmwareDaddy.start() }
                mappingResultsLoaderJob = launch { mappingManager.start() }
                launch { loadConfig() }
                launch { shaderLibraryManager.start() }
                launch { sceneManager.onStart() }
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
            launch { controllersManager.start() }

            updatePinkyState(PinkyState.Running)
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

    private suspend fun launchDaemonJobs(): Job {
        return CoroutineScope(coroutineContext).launch {
            launch {
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

            launch {
                while (keepRunning) {
                    delay(10000)
                    logger.info { "Framerate: ${facade.framerate.summarize()}" }
                }
            }
        }
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

    internal suspend fun renderAndSendNextFrame() {
        stageManager.renderAndSendNextFrame()
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

    suspend fun loadConfig() {
        val config = storage.loadConfig()
        config?.runningShowPath?.let { lastRunningShowPath ->
            val lastRunningShowFile = storage.resolve(lastRunningShowPath)
            try {
                val show = storage.loadShow(lastRunningShowFile)
                if (show == null) {
                    logger.warn { "No show found at $lastRunningShowPath" }
                } else {
                    switchTo(show, file = lastRunningShowFile)
                }
            } catch (e: Exception) {
                reportError("Failed to load show at $lastRunningShowPath", e)
            }
        }
    }

    private fun reportError(message: String, e: Exception) {
        logger.error(e) { message }
        serverNotices.add(message, e.message, e.stackTraceToString())
    }

    companion object {
        val logger = Logger("Pinky")
    }

    inner class Facade : baaahs.ui.Facade() {
        val stageManager: StageManager.Facade
            get() = this@Pinky.stageManager.facade

        val fixtureManager : FixtureManager.Facade
            get() = this@Pinky.fixtureManager.facade

        val networkStats: NetworkStats
            get() = this@Pinky.networkStats

        val brains: List<BrainManager.BrainController>
            get() = this@Pinky.brainManager.activeBrains.values.toList()

        val clock: Clock
            get() = this@Pinky.clock

        val framerate = Framerate()
    }
}

@Serializable
data class PinkyConfig(
    val runningShowPath: String?
)

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