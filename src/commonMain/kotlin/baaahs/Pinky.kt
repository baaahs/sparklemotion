package baaahs

import baaahs.api.ws.WebSocketRouter
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.gl.glsl.CompilationException
import baaahs.gl.render.ModelRenderer
import baaahs.io.ByteArrayReader
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
import baaahs.shaders.PixelBrainShader
import baaahs.show.Show
import baaahs.util.Framerate
import kotlinx.coroutines.*
import kotlinx.serialization.Serializable
import kotlin.coroutines.CoroutineContext

class Pinky(
    val model: Model<*>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val beatSource: BeatSource,
    val clock: Clock,
    fs: Fs,
    val firmwareDaddy: FirmwareDaddy,
    soundAnalyzer: SoundAnalyzer,
    private val switchShowAfterIdleSeconds: Int? = 600,
    private val adjustShowAfterIdleSeconds: Int? = null,
    modelRenderer: ModelRenderer,
    val plugins: Plugins,
    val pinkyMainDispatcher: CoroutineDispatcher
) : CoroutineScope, Network.UdpListener {
    val facade = Facade()
    private val storage = Storage(fs, plugins)
    private lateinit var mappingResults: MappingResults

    private val link = FragmentingUdpLink(network.link("pinky"))
    val httpServer = link.startHttpServer(Ports.PINKY_UI_TCP)

    private val beatDisplayer = PinkyBeatDisplayer(beatSource)
    private var mapperIsRunning = false

    private val pinkyJob = SupervisorJob()
    override val coroutineContext: CoroutineContext = pinkyMainDispatcher + pinkyJob

    private val pubSub: PubSub.Server = PubSub.Server(httpServer, coroutineContext)
//    private val gadgetManager = GadgetManager(pubSub)
    private val movingHeadManager = MovingHeadManager(fs, pubSub, model.movingHeads)
    internal val surfaceManager = SurfaceManager(modelRenderer)

    var stageManager: StageManager = StageManager(
        plugins, modelRenderer, pubSub, storage, surfaceManager, dmxUniverse, movingHeadManager, clock, model,
        coroutineContext
    )

    fun switchTo(newShow: Show?, file: Fs.File? = null) {
        stageManager.switchTo(newShow, file = file)
    }

//    private var selectedNewShowAt = DateTime.now()

    private val brainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val pendingBrainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    var pixelCount: Int = 0

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    // This needs to go last-ish, otherwise we start getting network traffic too early.
    private val udpSocket = link.listenUdp(Ports.PINKY, this)

    private val listeningVisualizers = hashSetOf<ListeningVisualizer>()

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
        val mappingInfos = (mappingResults as SessionMappingResults).brainData
        mappingInfos.forEach { (brainId, info) ->
            foundBrain(
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

            updateSurfaces()

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
            facade.framerate.elapsed(elapsedMs.toInt())

            maybeChangeThingsIfUsersAreIdle()

            delay(30)
        }
    }

    internal suspend fun launchStartupJobs(): Job {
        return CoroutineScope(coroutineContext).launch {
            CoroutineScope(coroutineContext).launch {
                launch { firmwareDaddy.start() }
                launch { movingHeadManager.start() }
                launch { mappingResults = storage.loadMappingData(model) }
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
            launch { beatDisplayer.run() }

            launch {
                while (true) {
                    if (mapperIsRunning) {
                        logger.info { "Mapping ${brainInfos.size} brains..." }
                    } else {
                        logger.info { "Sending to ${brainInfos.size} brains..." }
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

    internal fun updateSurfaces() {
        if (pendingBrainInfos.isNotEmpty()) {
            val brainSurfacesToRemove = mutableListOf<ShowRunner.SurfaceReceiver>()
            val brainSurfacesToAdd = mutableListOf<ShowRunner.SurfaceReceiver>()

            pendingBrainInfos.forEach { (brainId, incomingBrainInfo) ->
                val priorBrainInfo = brainInfos[brainId]
                if (priorBrainInfo != null) {
                    brainSurfacesToRemove.add(priorBrainInfo.surfaceReceiver)
                }

                if (incomingBrainInfo.hadException) {
                    // Existing Brain has had exceptions so we're forgetting about it.
                    brainInfos.remove(brainId)
                } else {
                    brainSurfacesToAdd.add(incomingBrainInfo.surfaceReceiver)
                    brainInfos[brainId] = incomingBrainInfo
                }
            }

            surfaceManager.surfacesChanged(brainSurfacesToAdd, brainSurfacesToRemove)
            listeningVisualizers.forEach { listeningVisualizer ->
                brainSurfacesToAdd.forEach {
                    listeningVisualizer.sendPixelData(it.surface)
                }
            }

            pendingBrainInfos.clear()

            facade.notifyChanged()
        }
    }

    private fun disableDmx() {
        dmxUniverse.allOff()
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        if (!isStartedUp) return

        CoroutineScope(coroutineContext).launch {
            val message = parse(bytes)
            when (message) {
                is BrainHelloMessage -> foundBrain(fromAddress, message)
                is MapperHelloMessage -> {
                    logger.debug { "Mapper isRunning=${message.isRunning}" }
                    mapperIsRunning = message.isRunning
                }
                is PingMessage -> if (message.isPong) receivedPong(message, fromAddress)
            }
        }
    }

    private fun foundBrain(
        brainAddress: Network.Address,
        msg: BrainHelloMessage,
        isSimulatedBrain: Boolean = false
    ) {
        val brainId = BrainId(msg.brainId)
        val surfaceName = msg.surfaceName

        logger.debug {
            "Hello from ${brainId.uuid}" +
                    " (${mappingResults.dataFor(brainId)?.surface?.name ?: "[unknown]"})" +
                    " at $brainAddress: $msg"
        }
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            logger.debug {
                "The firmware daddy doesn't like $brainId" +
                        " (${mappingResults.dataFor(brainId)?.surface?.name ?: "[unknown]"})" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }


        // println("Heard from brain $brainId at $brainAddress for $surfaceName")
        val dataFor = mappingResults.dataFor(brainId)
            ?: mappingResults.dataFor(msg.surfaceName ?: "__nope")
            ?: msg.surfaceName?.let { MappingResults.Info(model.findModelSurface(it), null) }

        val surface = dataFor?.let {
            val pixelLocations = dataFor.pixelLocations?.map { it ?: Vector3F(0f, 0f, 0f) } ?: emptyList()
            val pixelCount = dataFor.pixelLocations?.size ?: SparkleMotion.MAX_PIXEL_COUNT

            if (msg.surfaceName != dataFor.surface.name) {
                val mappingMsg = BrainMappingMessage(
                    brainId, dataFor.surface.name, null, Vector2F(0f, 0f),
                    Vector2F(0f, 0f), pixelCount, pixelLocations
                )
                udpSocket.sendUdp(brainAddress, Ports.BRAIN, mappingMsg)
            }

            IdentifiedSurface(dataFor.surface, pixelCount, dataFor.pixelLocations)
        } ?: AnonymousSurface(brainId)


        val priorBrainInfo = brainInfos[brainId]
        if (priorBrainInfo != null) {
            if (priorBrainInfo.brainId == brainId && priorBrainInfo.surface == surface) {
                // Duplicate packet?
//                logger.debug(
//                    "Ignore ${priorBrainInfo.brainId} ${priorBrainInfo.surface.describe()} ->" +
//                            " ${surface.describe()} because probably duplicate?"
//                )
                return
            }

//            logger.debug(
//                "Remapping ${priorBrainInfo.brainId} from ${priorBrainInfo.surface.describe()} ->" +
//                        " ${surface.describe()}"
//            )
        }

        val sendFn: (BrainShader.Buffer) -> Unit = { shaderBuffer ->
            val message = BrainShaderMessage(shaderBuffer.brainShader, shaderBuffer).toBytes()
            try {
                if (!isSimulatedBrain)
                    udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)
            } catch (e: Exception) {
                // Couldn't send to Brain? Schedule to remove it.
                val brainInfo = brainInfos[brainId]!!
                brainInfo.hadException = true
                pendingBrainInfos[brainId] = brainInfo

                logger.error("Error sending to $brainId, will take offline", e)
            }

            networkStats.packetsSent++
            networkStats.bytesSent += message.size
        }

        val pixelShader = PixelBrainShader(PixelBrainShader.Encoding.DIRECT_RGB)
        val surfaceReceiver = object : ShowRunner.SurfaceReceiver {
            override val surface = surface
            private val pixelBuffer = pixelShader.createBuffer(surface)

            override fun send(pixels: Pixels) {
                pixelBuffer.indices.forEach { i ->
                    pixelBuffer.colors[i] = pixels[i]
                }
                sendFn(pixelBuffer)

                updateListeningVisualizers(surface, pixelBuffer.colors)
            }
        }

        val brainInfo = BrainInfo(brainAddress, brainId, surface, msg.firmwareVersion, msg.idfVersion, surfaceReceiver)
//        logger.debug("Map ${brainInfo.brainId} to ${brainInfo.surface.describe()}")
        pendingBrainInfos[brainId] = brainInfo

        // Decide whether or not to tell this brain it should use a different firmware

    }

    /** If we want a pong back from a [BrainShaderMessage], send this. */
    private fun generatePongPayload(): ByteArray {
        return ByteArrayWriter().apply {
            writeLong(getTimeMillis())
        }.toBytes()
    }

    private fun receivedPong(message: PingMessage, fromAddress: Network.Address) {
        val originalSentAt = ByteArrayReader(message.data).readLong()
        val elapsedMs = getTimeMillis() - originalSentAt
        logger.debug { "Shader pong from $fromAddress took ${elapsedMs}ms" }
    }

    inner class PinkyBeatDisplayer(private val beatSource: BeatSource) {
        private var previousBeatData = beatSource.getBeatData()

        suspend fun run() {
            while (true) {
                val beatData = beatSource.getBeatData()
                if (beatData != previousBeatData) {
                    facade.notifyChanged()
                    previousBeatData = beatData
                }

                delay(10)
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
            listeningVisualizers.add(this)

            brainInfos.values.forEach { sendPixelData(it.surface) }
        }

        override fun receive(tcpConnection: Network.TcpConnection, bytes: ByteArray) {
            TODO("not implemented")
        }

        override fun reset(tcpConnection: Network.TcpConnection) {
            listeningVisualizers.remove(this)
        }

        fun sendPixelData(surface: Surface) {
            if (surface is IdentifiedSurface) {
                val pixelLocations = surface.pixelLocations ?: return

                val out = ByteArrayWriter(surface.name.length + surface.pixelCount * 3 * 4 + 20)
                out.writeByte(0)
                out.writeString(surface.name)
                out.writeInt(surface.pixelCount)
                pixelLocations.forEach {
                    (it ?: Vector3F(0f, 0f, 0f)).serialize(out)
                }
                tcpConnection.send(out.toBytes())
            }
        }

        fun sendFrame(surface: Surface, colors: List<Color>) {
            if (surface is IdentifiedSurface) {
                val out = ByteArrayWriter(surface.name.length + colors.size * 3 + 20)
                out.writeByte(1)
                out.writeString(surface.name)
                out.writeInt(colors.size)
                colors.forEach {
                    it.serializeWithoutAlpha(out)
                }
                tcpConnection.send(out.toBytes())
            }
        }
    }

    private fun updateListeningVisualizers(surface: Surface, colors: List<Color>) {
        if (listeningVisualizers.isNotEmpty()) {
            listeningVisualizers.forEach {
                it.sendFrame(surface, colors)
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

        val brains: List<BrainInfo>
            get() = this@Pinky.brainInfos.values.toList()

        val beatData: BeatData
            get() = this@Pinky.beatSource.getBeatData()

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

data class BrainId(val uuid: String)

class BrainInfo(
    val address: Network.Address,
    val brainId: BrainId,
    val surface: Surface,
    val firmwareVersion: String?,
    val idfVersion: String?,
    val surfaceReceiver: ShowRunner.SurfaceReceiver,
    var hadException: Boolean = false
)

@Serializable
enum class PinkyState {
    Initializing,
    Running,
    ShuttingDown
}
