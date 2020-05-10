package baaahs

import baaahs.api.ws.WebSocketRouter
import baaahs.geom.Vector2F
import baaahs.geom.Vector3F
import baaahs.glsl.GlslRenderer
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.io.Fs
import baaahs.mapper.MappingResults
import baaahs.mapper.PinkyMapperHandlers
import baaahs.mapper.Storage
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.GlslShader
import baaahs.shaders.PixelShader
import baaahs.shows.GuruMeditationErrorShow
import com.soywiz.klock.DateTime
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.*
import kotlin.math.min

class Pinky(
    val model: Model<*>,
    val shows: List<Show>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val beatSource: BeatSource,
    val clock: Clock,
    val fs: Fs,
    val firmwareDaddy: FirmwareDaddy,
    val display: PinkyDisplay,
    soundAnalyzer: SoundAnalyzer,
    private val switchShowAfterIdleSeconds: Int? = 600,
    private val adjustShowAfterIdleSeconds: Int? = null,
    glslRenderer: GlslRenderer
) : Network.UdpListener {
    private val storage = Storage(fs)
    private val mappingResults = storage.loadMappingData(model)

    private val link = FragmentingUdpLink(network.link())
    val httpServer = link.startHttpServer(Ports.PINKY_UI_TCP)
    private val mdns = link.mdns

    private val beatDisplayer = PinkyBeatDisplayer(beatSource)
    private var mapperIsRunning = false
    private var selectedShow = shows.random()
        set(value) {
            field = value
            display.selectedShow = value
            showRunner.nextShow = selectedShow
        }

    private val pubSub: PubSub.Server = PubSub.Server(httpServer).apply { install(gadgetModule) }
    private val gadgetManager = GadgetManager(pubSub)
    private val movingHeadManager = MovingHeadManager(fs, pubSub, model.movingHeads)
    private val showRunner = ShowRunner(
        model, selectedShow, gadgetManager, beatSource, dmxUniverse, movingHeadManager, clock, glslRenderer
    )

    private val selectedShowChannel: PubSub.Channel<String>
    private var selectedNewShowAt = DateTime.now()

    private val brainToSurfaceMap_CHEAT = mutableMapOf<BrainId, Model.Surface>()
    private val surfaceToPixelLocationMap_CHEAT = mutableMapOf<Model.Surface, List<Vector3F>>()

    private val brainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val pendingBrainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    // This needs to go last-ish, otherwise we start getting network traffic too early.
    private val udpSocket = link.listenUdp(Ports.PINKY, this)

    private val listeningVisualizers = hashSetOf<ListeningVisualizer>()

    init {
        PinkyHttp(httpServer).register(brainInfos, mappingResults, model)
        httpServer.listenWebSocket("/ws/api") {
            WebSocketRouter { PinkyMapperHandlers(storage).register(this) }
        }

        httpServer.listenWebSocket("/ws/visualizer") { ListeningVisualizer() }

        pubSub.publish(Topics.availableShows, shows.map { show -> show.name }) {}
        selectedShowChannel = pubSub.publish(Topics.selectedShow, shows[0].name) { selectedShow ->
            this.selectedShow = shows.find { it.name == selectedShow }!!
        }

        // save these if we want to explicitly unregister them or update their TXT records later
        mdns.register(link.myHostname, "_sparklemotion-pinky", "_udp", Ports.PINKY, "local.", mutableMapOf(Pair("MAX_UDP_SIZE", "1450")))
        mdns.register(link.myHostname, "_sparklemotion-pinky", "_tcp", Ports.PINKY_UI_TCP, "local.")
        mdns.listen("_sparklemotion-brain", "_udp", "local.", MdnsBrainListenHandler())
    }

    suspend fun run(): Show.Renderer {
        GlobalScope.launch { beatDisplayer.run() }
        GlobalScope.launch {
            while (true) {
                if (mapperIsRunning) {
                    logger.info { "Mapping ${brainInfos.size} brains..." }
                } else {
                    logger.info { "Sending to ${brainInfos.size} brains..." }
                }
                delay(10000)
            }
        }

        display.listShows(shows)
        display.selectedShow = selectedShow

        display.onShowChange = { switchToShow(display.selectedShow!!) }

        while (true) {
            if (mapperIsRunning) {
                disableDmx()
                delay(50)
                continue
            }

            updateSurfaces()

            networkStats.reset()
            val elapsedMs = time {
                try {
                    drawNextFrame()
                } catch (e: Exception) {
                    logger.error("Error rendering frame for ${selectedShow.name}", e)
                    delay(1000)
                    switchToShow(GuruMeditationErrorShow)
                }
            }
            display.showFrameMs = elapsedMs.toInt()
            display.stats = networkStats

            maybeChangeThingsIfUsersAreIdle()

            delay(30)
        }
    }

    private fun maybeChangeThingsIfUsersAreIdle() {
        val now = DateTime.now()
        val secondsSinceUserInteraction = now.minus(gadgetManager.lastUserInteraction).seconds
        if (switchShowAfterIdleSeconds != null
            && now.minus(selectedNewShowAt).seconds > switchShowAfterIdleSeconds
            && secondsSinceUserInteraction > switchShowAfterIdleSeconds
        ) {
            switchToShow(shows.random())
            selectedNewShowAt = now
        }

        if (adjustShowAfterIdleSeconds != null
            && secondsSinceUserInteraction > adjustShowAfterIdleSeconds
        ) {
            gadgetManager.adjustSomething()
        }
    }

    fun switchToShow(nextShow: Show) {
        this.selectedShow = nextShow
        selectedShowChannel.onChange(nextShow.name)
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

            showRunner.surfacesChanged(brainSurfacesToAdd, brainSurfacesToRemove)
            listeningVisualizers.forEach { listeningVisualizer ->
                brainSurfacesToAdd.forEach {
                    listeningVisualizer.sendPixelData(it.surface)
                }
            }

            pendingBrainInfos.clear()
        }

        display.brainCount = brainInfos.size
    }

    internal fun drawNextFrame() {
        showRunner.nextFrame()
    }

    private fun disableDmx() {
        dmxUniverse.allOff()
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
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

    private fun foundBrain(
        brainAddress: Network.Address,
        msg: BrainHelloMessage
    ) {
        val brainId = BrainId(msg.brainId)
        val surfaceName = msg.surfaceName

        logger.info {
            "Hello from ${brainId.uuid}" +
                    " (${mappingResults.dataForBrain(brainId)?.surface?.name ?: "[unknown]"})" +
                    " at $brainAddress: $msg"
        }
        if (firmwareDaddy.doesntLikeThisVersion(msg.firmwareVersion)) {
            // You need the new hotness bro
            logger.info {
                "The firmware daddy doesn't like $brainId" +
                        " (${mappingResults.dataForBrain(brainId)?.surface?.name ?: "[unknown]"})" +
                        " having ${msg.firmwareVersion}" +
                        " so we'll send ${firmwareDaddy.urlForPreferredVersion}"
            }
            val newHotness = UseFirmwareMessage(firmwareDaddy.urlForPreferredVersion)
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, newHotness)
        }


        // println("Heard from brain $brainId at $brainAddress for $surfaceName")
        val dataFor = mappingResults.dataForBrain(brainId)
            ?: mappingResults.dataForSurface(msg.surfaceName ?: "__nope")?.get(brainId)
            ?: findMappingInfo_CHEAT(surfaceName, brainId)

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

        val sendFn: (Shader.Buffer) -> Unit = { shaderBuffer ->
            val message = BrainShaderMessage(shaderBuffer.shader, shaderBuffer).toBytes()
            try {
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

        val pixelShader = PixelShader(PixelShader.Encoding.DIRECT_RGB)
        val surfaceReceiver = object : ShowRunner.SurfaceReceiver {
            override val surface = surface
            private val pixelBuffer = pixelShader.createBuffer(surface)

            override fun send(pixels: Pixels) {
                pixelBuffer.indices.forEach { i ->
                    pixelBuffer.colors[i] = pixels[i]
                }
                sendFn(pixelBuffer)
            }
        }

        val brainInfo = BrainInfo(brainAddress, brainId, surface, msg.firmwareVersion, msg.idfVersion, surfaceReceiver)
//        logger.debug("Map ${brainInfo.brainId} to ${brainInfo.surface.describe()}")
        pendingBrainInfos[brainId] = brainInfo

        // Decide whether or not to tell this brain it should use a different firmware

    }

    private fun findMappingInfo_CHEAT(surfaceName: String?, brainId: BrainId): MappingResults.Info? {
        val modelSurface = surfaceName?.let { model.findModelSurface(surfaceName) } ?: brainToSurfaceMap_CHEAT[brainId]
        return if (modelSurface != null) {
            MappingResults.Info(modelSurface, surfaceToPixelLocationMap_CHEAT[modelSurface])
        } else {
            null
        }
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

    fun providePanelMapping_CHEAT(brainId: BrainId, surface: Model.Surface) {
        brainToSurfaceMap_CHEAT[brainId] = surface
    }

    fun providePixelMapping_CHEAT(surface: Model.Surface, pixelLocations: List<Vector3F>) {
        surfaceToPixelLocationMap_CHEAT[surface] = pixelLocations
    }

    inner class PinkyBeatDisplayer(val beatSource: BeatSource) {
        suspend fun run() {
            while (true) {
                val beatData = beatSource.getBeatData()
                display.beat = beatData.beatWithinMeasure(clock).toInt()
                display.bpm = beatData.bpm
                display.beatConfidence = beatData.confidence
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

    class RenderTree(val shader: GlslShader, val renderer: GlslRenderer, val buffer: GlslShader.Buffer) {
        fun draw(pixels: Pixels) {
            renderer.draw()
            pixels.finishedFrame()
        }

        fun release() {
            renderer.release()
        }
    }

//    var lastSentAt: Long = 0
//
//    private fun aroundNextFrame(callNextFrame: () -> Unit) {
//        /**
//         * [ShowRunner.SurfaceReceiver.send] is called here; if [prerenderPixels] is true, it won't
//         * actually send; we need to do that ourselves.
//         */
//
//        val preDrawElapsed = timeSync {
//            showRunner.preDraw()
//        }
//
//        val sendElapsed = timeSync {
//            brainInfos.values.forEach { brainInfo ->
//                val surfaceReceiver = brainInfo.surfaceReceiver.send()
//                surfaceReceiver.actuallySend()
//            }
//        }
//
////            println("preDraw took ${preDrawElapsed}ms, send took ${sendElapsed}ms")
//        val now = getTimeMillis()
//        val elapsedMs = now - lastSentAt
////        println("It's been $elapsedMs")
//        lastSentAt = now
//    }

    private inner class MdnsBrainListenHandler : Network.MdnsListenHandler {
        override fun resolved(service: Network.MdnsService) {
            val brainId = service.hostname
            val address = service.getAddress()
            if (address != null) {
                val version = service.getTXT("version")
                val idfVersion = service.getTXT("idf_ver")
                val msg = BrainHelloMessage(brainId, null, version, idfVersion)
                foundBrain(address, msg)
            }
        }

        override fun removed(service: Network.MdnsService) {
            TODO("not implemented: What do when brain disconnects?")
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

    private fun updateListeningVisualizers(surface: Surface, colors: MutableList<Color>) {
        if (listeningVisualizers.isNotEmpty()) {
            listeningVisualizers.forEach {
                it.sendFrame(surface, colors)
            }
        }
    }

    companion object {
        val logger = Logger("Pinky")
    }
}

@Serializable(with = BrainIdSerializer::class)
data class BrainId(val uuid: String)

class BrainIdSerializer : KSerializer<BrainId> {
    override val descriptor: SerialDescriptor = PrimitiveDescriptor("BrainId", PrimitiveKind.STRING)
    override fun deserialize(decoder: Decoder): BrainId = BrainId(decoder.decodeString())
    override fun serialize(encoder: Encoder, value: BrainId) = encoder.encodeString(value.uuid)
}

class BrainInfo(
    val address: Network.Address,
    val brainId: BrainId,
    val surface: Surface,
    val firmwareVersion: String?,
    val idfVersion: String?,
    val surfaceReceiver: ShowRunner.SurfaceReceiver,
    var hadException: Boolean = false
)
