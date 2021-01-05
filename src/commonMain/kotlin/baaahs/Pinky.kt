package baaahs

import baaahs.api.ws.WebSocketRouter
import baaahs.dmx.Dmx
import baaahs.fixtures.Fixture
import baaahs.fixtures.FixtureManager
import baaahs.gl.RootToolchain
import baaahs.gl.glsl.CompilationException
import baaahs.gl.render.RenderManager
import baaahs.io.ByteArrayWriter
import baaahs.io.Fs
import baaahs.mapper.MappingResults
import baaahs.mapper.PinkyMapperHandlers
import baaahs.mapper.SessionMappingResults
import baaahs.mapper.Storage
import baaahs.model.Model
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.plugin.Plugins
import baaahs.proto.*
import baaahs.show.Show
import baaahs.util.Clock
import baaahs.util.Framerate
import baaahs.util.Logger
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

class Pinky(
    val model: Model,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val clock: Clock,
    fs: Fs,
    val firmwareDaddy: FirmwareDaddy,
    soundAnalyzer: SoundAnalyzer,
    private val switchShowAfterIdleSeconds: Int? = 600,
    private val adjustShowAfterIdleSeconds: Int? = null,
    renderManager: RenderManager,
    val plugins: Plugins,
    val pinkyMainDispatcher: CoroutineDispatcher
) : CoroutineScope, Network.UdpListener {
    val facade = Facade()
    private val storage = Storage(fs, plugins)
    private val mappingResults = FutureMappingResults()

    private val link = FragmentingUdpLink(network.link("pinky"))
    val httpServer = link.startHttpServer(Ports.PINKY_UI_TCP)

    private var mapperIsRunning = false

    private val pinkyJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = pinkyMainDispatcher + pinkyJob

    private val pubSub: PubSub.Server = PubSub.Server(httpServer, coroutineContext)
//    private val gadgetManager = GadgetManager(pubSub)
    internal val fixtureManager = FixtureManager(renderManager)

    val toolchain = RootToolchain(plugins)
    var stageManager: StageManager = StageManager(
        toolchain, renderManager, pubSub, storage, fixtureManager, clock, model, coroutineContext
    )

    fun switchTo(newShow: Show?, file: Fs.File? = null) {
        stageManager.switchTo(newShow, file = file)
    }

//    private var selectedNewShowAt = DateTime.now()

    var pixelCount: Int = 0

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    // This needs to go last-ish, otherwise we start getting network traffic too early.
    private val udpSocket = link.listenUdp(Ports.PINKY, this)
    private val brainManager =
        BrainManager(fixtureManager, firmwareDaddy, model, mappingResults, udpSocket, networkStats, clock)
    private val movingHeadManager = MovingHeadManager(fixtureManager, dmxUniverse, model.movingHeads)

    private val serverNotices = arrayListOf<ServerNotice>()
    private val serverNoticesChannel = pubSub.publish(Topics.serverNotices, serverNotices) {
        launch {
            serverNotices.clear()
            serverNotices.addAll(it)
        }
    }

    private var pinkyState = PinkyState.Initializing
    private val pinkyStateChannel = pubSub.publish(Topics.pinkyState, pinkyState) {}

    init {
        httpServer.listenWebSocket("/ws/api") {
            WebSocketRouter(coroutineContext) { PinkyMapperHandlers(storage).register(this) }
        }

        httpServer.listenWebSocket("/ws/visualizer") { ListeningVisualizer() }
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

    fun addSimulatedBrains() {
        val fakeAddress = object : Network.Address {}
        val mappingInfos = (mappingResults.actualMappingResults as SessionMappingResults).brainData
        mappingInfos.forEach { (brainId, info) ->
            brainManager.foundBrain(
                fakeAddress, BrainHelloMessage(brainId.uuid, info.surface.name, null, null),
                isSimulatedBrain = true
            )
        }
    }

    private suspend fun run() {
        while (keepRunning) {
            if (mapperIsRunning) {
                disableDmx()
                delay(50)
                continue
            }

            updateFixtures()

            networkStats.reset()
            val elapsedMs = time {
                try {
                    stageManager.renderAndSendNextFrame()
                } catch (e: Exception) {
                    logger.error("Error rendering frame for ${stageManager.facade.currentShow?.title}", e)
                    if (e is CompilationException) {
                        e.source?.let { logger.info { it } }
                    }
                    delay(1000)
                }
            }
            facade.notifyChanged()
            facade.framerate.elapsed(elapsedMs)

            maybeChangeThingsIfUsersAreIdle()

            delay(30)
        }
    }

    internal suspend fun launchStartupJobs(): Job {
        return CoroutineScope(coroutineContext).launch {
            CoroutineScope(coroutineContext).launch {
                launch { firmwareDaddy.start() }
                launch { movingHeadManager.start() }
                launch { mappingResults.actualMappingResults = storage.loadMappingData(model) }
                launch { loadConfig() }
            }.join()

            isStartedUp = true
            updatePinkyState(PinkyState.Running)
        }
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
                        logger.info { "Mapping ${brainManager.brainCount} brains..." }
                    } else {
                        logger.info { "Sending to ${brainManager.brainCount} brains..." }
                    }
                    delay(10000)
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

    internal fun renderAndSendNextFrame() {
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
                    logger.debug { "Mapper isRunning=${message.isRunning}" }
                    mapperIsRunning = message.isRunning
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

    inner class ListeningVisualizer : Network.WebSocketListener {
        lateinit var tcpConnection: Network.TcpConnection

        override fun connected(tcpConnection: Network.TcpConnection) {
            this.tcpConnection = tcpConnection
            brainManager.addListeningVisualizer(this)
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            TODO("not implemented")
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            brainManager.removeListeningVisualizer(this)
        }

        fun sendPixelData(fixture: Fixture) {
            if (fixture.modelEntity != null) {
                val pixelLocations = fixture.pixelLocations

                val out = ByteArrayWriter(fixture.name.length + fixture.pixelCount * 3 * 4 + 20)
                out.writeByte(0)
                out.writeString(fixture.name)
                out.writeInt(fixture.pixelCount)
                pixelLocations.forEach { it.serialize(out) }
                tcpConnection.send(out.toBytes())
            }
        }

        fun sendFrame(fixture: Fixture, colors: List<Color>) {
            if (fixture.modelEntity != null) {
                val out = ByteArrayWriter(fixture.name.length + colors.size * 3 + 20)
                out.writeByte(1)
                out.writeString(fixture.name)
                out.writeInt(colors.size)
                colors.forEach {
                    it.serializeWithoutAlpha(out)
                }
                tcpConnection.send(out.toBytes())
            }
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
        serverNotices.add(ServerNotice(message, e.message))
        serverNoticesChannel.onChange(serverNotices)
    }

    @Serializable
    data class ServerNotice(
        val title: String,
        val message: String?,
        val id: String = randomId("error")
    )

    companion object {
        val logger = Logger("Pinky")
    }

    inner class Facade : baaahs.ui.Facade() {
        val stageManager: StageManager.Facade
            get() = this@Pinky.stageManager.facade

        val networkStats: NetworkStats
            get() = this@Pinky.networkStats

        val brains: List<BrainManager.BrainTransport>
            get() = this@Pinky.brainManager.activeBrains.values.toList()

        val clock: Clock
            get() = this@Pinky.clock

        val framerate = Framerate()

        val pixelCount: Int
            get() = this@Pinky.pixelCount
    }
}

@Serializable
data class PinkyConfig(
    val runningShowPath: String?
)

@Serializable
enum class PinkyState {
    Initializing,
    Running,
    ShuttingDown
}

private class FutureMappingResults : MappingResults {
    var actualMappingResults: MappingResults? = null

    override fun dataFor(brainId: BrainId): MappingResults.Info? {
        if (actualMappingResults == null) {
            Pinky.logger.warn { "Mapping results for $brainId requested before available." }
        }
        return actualMappingResults?.dataFor(brainId)
    }

    override fun dataFor(surfaceName: String): MappingResults.Info? {
        if (actualMappingResults == null) {
            Pinky.logger.warn { "Mapping results for $surfaceName requested before available." }
        }
        return actualMappingResults?.dataFor(surfaceName)
    }
}