package baaahs

import baaahs.geom.Vector2
import baaahs.geom.Vector2F
import baaahs.io.ByteArrayReader
import baaahs.io.ByteArrayWriter
import baaahs.io.Fs
import baaahs.mapper.MapperEndpoint
import baaahs.mapper.MappingResults
import baaahs.mapper.Storage
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.PixelShader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.min

class Pinky(
    val model: Model<*>,
    val shows: List<Show>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val fs: Fs,
    val display: PinkyDisplay,
    private val prerenderPixels: Boolean = false
) : Network.UdpListener {
    private val storage = Storage(fs)
    private val mappingResults = storage.loadMappingData(model)

    private val link = FragmentingUdpLink(network.link())
    val httpServer = link.startHttpServer(Ports.PINKY_UI_TCP)

    private val beatProvider = PinkyBeatProvider(120.0f)
    private var mapperIsRunning = false
    private var selectedShow = shows.first()
        set(value) {
            field = value
            display.selectedShow = value
            showRunner.nextShow = selectedShow
        }

    private val pubSub: PubSub.Server = PubSub.Server(httpServer).apply { install(gadgetModule) }
    private val gadgetManager = GadgetManager(pubSub)
    private val movingHeadManager = MovingHeadManager(fs, pubSub, model.movingHeads)
    private val showRunner =
        ShowRunner(model, selectedShow, gadgetManager, beatProvider, dmxUniverse, movingHeadManager)

    private val brainToSurfaceMap_CHEAT = mutableMapOf<BrainId, Model.Surface>()
    private val surfaceToPixelLocationMap_CHEAT = mutableMapOf<Model.Surface, List<Vector2F>>()

    private val brainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val pendingBrainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    // This needs to go last-ish, otherwise we start getting network traffic too early.
    private val udpSocket = link.listenUdp(Ports.PINKY, this)

    init {
        httpServer.listenWebSocket("/ws/mapper") { MapperEndpoint(storage) }
    }

    suspend fun run(): Show.Renderer {
        GlobalScope.launch { beatProvider.run() }

        display.listShows(shows)
        display.selectedShow = selectedShow

        pubSub.publish(Topics.availableShows, shows.map { show -> show.name }) {}
        val selectedShowChannel = pubSub.publish(Topics.selectedShow, shows[0].name) { selectedShow ->
            this.selectedShow = shows.find { it.name == selectedShow }!!
        }

        display.onShowChange = {
            this.selectedShow = display.selectedShow!!
            selectedShowChannel.onChange(this.selectedShow.name)
        }

        while (true) {
            if (mapperIsRunning) {
                disableDmx()
                delay(50)
                continue
            }

            updateSurfaces()

            networkStats.reset()
            val elapsedMs = time {
                drawNextFrame()
            }
            display.showFrameMs = elapsedMs.toInt()
            display.stats = networkStats

            delay(50)
        }
    }

    internal fun updateSurfaces() {
        if (pendingBrainInfos.isNotEmpty()) {
            val brainSurfacesToRemove = mutableListOf<ShowRunner.SurfaceReceiver>()
            val brainSurfacesToAdd = mutableListOf<ShowRunner.SurfaceReceiver>()

            pendingBrainInfos.forEach { (brainId, brainInfo) ->
                val priorBrainInfo = brainInfos[brainId]
                if (priorBrainInfo != null) {
                    brainSurfacesToRemove.add(priorBrainInfo.surfaceReceiver)
                }
                brainSurfacesToAdd.add(brainInfo.surfaceReceiver)

                brainInfos[brainId] = brainInfo
            }

            showRunner.surfacesChanged(brainSurfacesToAdd, brainSurfacesToRemove)

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
            is BrainHelloMessage -> foundBrain(fromAddress, BrainId(message.brainId), message.surfaceName)
            is MapperHelloMessage -> {
                println("Mapper isRunning=${message.isRunning}")
                mapperIsRunning = message.isRunning
            }
            is PingMessage -> if (message.isPong) receivedPong(message, fromAddress)
        }
    }

    private fun foundBrain(
        brainAddress: Network.Address,
        brainId: BrainId,
        surfaceName: String?
    ) {
        println("Heard from brain $brainId at $brainAddress for $surfaceName")
        val dataFor = mappingResults.dataFor(brainId) ?: findMappingInfo_CHEAT(surfaceName, brainId)

        val surface = dataFor?.let {
            val pixelVertices = dataFor.pixelLocations
            val pixelCount = pixelVertices?.size ?: SparkleMotion.MAX_PIXEL_COUNT

            val mappingMsg = BrainMappingMessage(
                brainId, dataFor.surface.name, null, Vector2F(0f, 0f),
                Vector2F(0f, 0f), pixelCount, pixelVertices ?: emptyList()
            )
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, mappingMsg)
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

        val surfaceReceiver = PrerenderingSurfaceReceiver(surface) { shaderBuffer ->
            sendBrainShaderMessage(brainAddress, shaderBuffer)
        }


        val brainInfo = BrainInfo(brainAddress, brainId, surface, surfaceReceiver)
//        logger.debug("Map ${brainInfo.brainId} to ${brainInfo.surface.describe()}")
        pendingBrainInfos[brainId] = brainInfo
    }

    private fun sendBrainShaderMessage(brainAddress: Network.Address, shaderBuffer: Shader.Buffer) {
        val pongData = ByteArrayWriter().apply {
                writeLong(getTimeMillis())
            }.toBytes()

            val message = BrainShaderMessage(shaderBuffer.shader, shaderBuffer, pongData).toBytes()
        udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)

        networkStats.packetsSent++
        networkStats.bytesSent += message.size
    }


    private fun findMappingInfo_CHEAT(surfaceName: String?, brainId: BrainId): MappingResults.Info? {
        val modelSurface = surfaceName?.let { model.findModelSurface(surfaceName) } ?: brainToSurfaceMap_CHEAT[brainId]
        return if (modelSurface != null) {
            MappingResults.Info(modelSurface, surfaceToPixelLocationMap_CHEAT[modelSurface])
        } else {
            null
        }
    }

    private fun receivedPong(message: PingMessage, fromAddress: Network.Address) {
        val originalSentAt = ByteArrayReader(message.data).readLong()
        val elapsedMs = getTimeMillis() - originalSentAt
        logger.debug("Shader pong from $fromAddress took ${elapsedMs}ms")
    }

    fun providePanelMapping_CHEAT(brainId: BrainId, surface: Model.Surface) {
        brainToSurfaceMap_CHEAT[brainId] = surface
    }

    fun providePixelMapping_CHEAT(surface: Model.Surface, pixelLocations: List<Vector2F>) {
        surfaceToPixelLocationMap_CHEAT[surface] = pixelLocations
    }

    interface BeatProvider {
        var bpm: Float
        val beat: Float
    }

    inner class PinkyBeatProvider(override var bpm: Float) : BeatProvider {
        private var startTimeMillis = 0L
        private var beatsPerMeasure = 4

        private val millisPerBeat = 1000 / (bpm / 60)

        override val beat: Float
            get() {
                val now = getTimeMillis()
                return (now - startTimeMillis) / millisPerBeat % beatsPerMeasure
            }

        suspend fun run() {
            startTimeMillis = getTimeMillis()

            while (true) {
                display.beat = beat.toInt()

                val offsetMillis = getTimeMillis() - startTimeMillis
                val millsPer = millisPerBeat
                val delayTimeMillis = millsPer - offsetMillis % millsPer
                delay(delayTimeMillis.toLong())
            }
        }
    }

    class NetworkStats(var bytesSent: Int = 0, var packetsSent: Int = 0) {
        internal fun reset() {
            bytesSent = 0
            packetsSent = 0
        }
    }


    private inner class PrerenderingSurfaceReceiver(surface: Surface, sendFn: (Shader.Buffer) -> Unit) :
        ShowRunner.SurfaceReceiver(surface, sendFn) {
        private var currentRenderTree: Brain.RenderTree<*>? = null
        private var pixels: PixelsAdapter? = null

        @Suppress("UNCHECKED_CAST")
        override fun send(shaderBuffer: Shader.Buffer) {
            if (prerenderPixels) {
                val shader = shaderBuffer.shader as Shader<Shader.Buffer>
                var renderTree = currentRenderTree
                if (renderTree == null || renderTree.shader != shader) {
                    val renderer = shader.createRenderer(surface)
                    renderTree = Brain.RenderTree(shader, renderer, shaderBuffer)
                    currentRenderTree = renderTree

                    if (pixels == null) {
                        val pixelBuffer = PixelShader(PixelShader.Encoding.DIRECT_RGB).createBuffer(surface)
                        pixels = PixelsAdapter(pixelBuffer)
                    }
                }

                renderTree.draw(pixels!!)
                super.send(pixels!!.buffer)
            } else {
                super.send(shaderBuffer)
            }
        }
    }

    private class PixelsAdapter(internal val buffer: PixelShader.Buffer) : Pixels {
        override val size: Int = buffer.colors.size

        override fun get(i: Int): Color = buffer.colors[i]

        override fun set(i: Int, color: Color) {
            buffer.colors[i] = color
        }

        override fun set(colors: Array<Color>) {
            for (i in 0 until min(colors.size, size)) {
                buffer.colors[i] = colors[i]
            }
        }

    }
}

data class BrainId(val uuid: String)

class BrainInfo(
    val address: Network.Address,
    val brainId: BrainId,
    val surface: Surface,
    val surfaceReceiver: ShowRunner.SurfaceReceiver
)
