package baaahs

import baaahs.api.ws.WebSocketRouter
import baaahs.controller.WledManager
import baaahs.dmx.Dmx
import baaahs.dmx.DmxManager
import baaahs.fixtures.FixtureManager
import baaahs.gl.RootToolchain
import baaahs.gl.glsl.CompilationException
import baaahs.gl.render.RenderManager
import baaahs.io.Fs
import baaahs.libraries.ShaderLibraryManager
import baaahs.mapper.*
import baaahs.model.Model
import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.plugin.Plugins
import baaahs.proto.*
import baaahs.show.Show
import baaahs.sim.FakeNetwork
import baaahs.util.Clock
import baaahs.util.Framerate
import baaahs.util.Logger
import baaahs.visualizer.remote.RemoteVisualizerServer
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

class Pinky(
    val model: Model,
    val network: Network,
    val clock: Clock,
    fs: Fs,
    val firmwareDaddy: FirmwareDaddy,
//    private val switchShowAfterIdleSeconds: Int? = 600,
//    private val adjustShowAfterIdleSeconds: Int? = null,
    renderManager: RenderManager,
    val plugins: Plugins,
    val pinkyMainDispatcher: CoroutineDispatcher,
    private val link: Network.Link,
    val httpServer: Network.HttpServer,
    pubSub: PubSub.Server,
    dmxManager: DmxManager,
    val pinkySettings: PinkySettings
) : CoroutineScope, Network.UdpListener {
    val facade = Facade()
    private val storage = Storage(fs, plugins)
    private val mappingResults = FutureMappingResults()

    private val pinkyJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = pinkyMainDispatcher + pinkyJob

    val gadgetManager = GadgetManager(pubSub, clock, coroutineContext)
    internal val fixtureManager = FixtureManager(renderManager, model, mappingResults)

    val toolchain = RootToolchain(plugins)
    val stageManager: StageManager = StageManager(
        toolchain, renderManager, pubSub, storage, fixtureManager, clock, model, gadgetManager
    )

    fun switchTo(newShow: Show?, file: Fs.File? = null) {
        stageManager.switchTo(newShow, file = file)
    }

//    private var selectedNewShowAt = DateTime.now()

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    // This needs to go last-ish, otherwise we start getting network traffic too early.
    private val udpSocket = link.listenFragmentingUdp(Ports.PINKY, this)
    private val brainManager =
        BrainManager(fixtureManager, firmwareDaddy, mappingResults, udpSocket, networkStats, clock, pubSub)
    private val movingHeadManager = MovingHeadManager(fixtureManager, dmxManager.dmxUniverse, model.movingHeads)
    private val wledManager = WledManager(fixtureManager, model, link, pubSub, pinkyMainDispatcher, clock)
    val dmxUniverse: Dmx.Universe = dmxManager.dmxUniverse

    private val shaderLibraryManager = ShaderLibraryManager(storage, pubSub)

    private val serverNotices = arrayListOf<ServerNotice>()
    private val serverNoticesChannel = pubSub.publish(Topics.serverNotices, serverNotices) {
        launch {
            serverNotices.clear()
            serverNotices.addAll(it)
        }
    }

    private var pinkyState = PinkyState.Initializing
    private val pinkyStateChannel = pubSub.publish(Topics.pinkyState, pinkyState) {}
    private var mapperIsRunning = false

    init {
        httpServer.listenWebSocket("/ws/api") {
            WebSocketRouter(coroutineContext) { PinkyMapperHandlers(storage).register(this) }
        }

        httpServer.listenWebSocket("/ws/visualizer") {
            RemoteVisualizerServer(brainManager, wledManager, movingHeadManager)
        }
    }

    private var isStartedUp = false
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
        val mappingInfos = (mappingResults.actualMappingResults as SessionMappingResults).controllerData
        mappingInfos.forEach { (controllerId, info) ->
            when (controllerId.controllerType) {
                BrainManager.controllerTypeName -> {
                    brainManager.foundBrain(
                        FakeNetwork.FakeAddress("Simulated Brain ${controllerId.id}"),
                        BrainHelloMessage(controllerId.id, info.entity.name, null, null),
                        isSimulatedBrain = true
                    )
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

                updateFixtures()

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
                launch { movingHeadManager.start() }
                mappingResultsLoaderJob =
                    launch { mappingResults.actualMappingResults = storage.loadMappingData(model) }
                launch { loadConfig() }
                launch { shaderLibraryManager.start() }
            }.join()

            isStartedUp = true
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
                        logger.info { "Sending pixels to ${brainManager.brainCount} brains." }
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

    internal fun updateFixtures() {
        if (brainManager.updateFixtures()) {
            facade.notifyChanged()
        }
    }

    private fun disableDmx() {
        dmxUniverse.allOff()
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        if (!isStartedUp) return

        CoroutineScope(coroutineContext).launch {
            when (val message = parse(bytes)) {
                is BrainHelloMessage -> brainManager.foundBrain(fromAddress, message)
                is PingMessage -> brainManager.receivedPing(fromAddress, message)
                is MapperHelloMessage -> {
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
            }
        }
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
        serverNotices.add(ServerNotice(message, e.message, e.stackTraceToString()))
        serverNoticesChannel.onChange(serverNotices)
    }

    @Serializable
    data class ServerNotice(
        val title: String,
        val message: String?,
        val stackTrace: String?,
        val id: String = randomId("error")
    )

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

        val brains: List<BrainManager.BrainTransport>
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

private class FutureMappingResults : MappingResults {
    var actualMappingResults: MappingResults? = null

    override fun dataForController(controllerId: ControllerId): MappingResults.Info? {
        if (actualMappingResults == null) {
            Pinky.logger.warn { "Mapping results for $controllerId requested before available." }
        }
        return actualMappingResults?.dataForController(controllerId)
    }

    override fun dataForEntity(entityName: String): MappingResults.Info? {
        if (actualMappingResults == null) {
            Pinky.logger.warn { "Mapping results for $entityName requested before available." }
        }
        return actualMappingResults?.dataForEntity(entityName)
    }
}