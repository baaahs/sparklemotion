package baaahs

import baaahs.geom.Vector2
import baaahs.geom.Vector2F
import baaahs.io.Fs
import baaahs.mapper.MapperEndpoint
import baaahs.mapper.Storage
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Pinky(
    val model: Model<*>,
    val shows: List<Show>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val fs: Fs,
    val display: PinkyDisplay
) : Network.UdpListener {
    private val link = FragmentingUdpLink(network.link())
    private val udpSocket = link.listenUdp(Ports.PINKY, this)
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
    private val pixelsBySurface = mutableMapOf<Model.Surface, Array<Vector2>>()
    private val surfaceMappingsByBrain = mutableMapOf<BrainId, Model.Surface>()

    private val brainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val pendingBrainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    private val storage = Storage(fs)

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

            delay(100)
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
        }
    }

    private fun foundBrain(
        brainAddress: Network.Address,
        brainId: BrainId,
        surfaceName: String?
    ) {
        println("Heard from brain $brainId at $brainAddress for $surfaceName")
        val surface = findModelSurface(surfaceName, brainId)?.let { modelSurface ->
            val pixelLocations = pixelsBySurface[modelSurface]
            val pixelCount = pixelLocations?.size ?: SparkleMotion.MAX_PIXEL_COUNT
            val pixelVertices = pixelLocations?.map { Vector2F(it.x.toFloat(), it.y.toFloat()) }
                ?: emptyList()
            val mappingMsg = BrainMappingMessage(
                brainId, modelSurface.name, null, Vector2F(0f, 0f),
                Vector2F(0f, 0f), pixelCount, pixelVertices
            )
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, mappingMsg)
            MappedSurface(modelSurface, pixelCount)
        } ?: UnmappedSurface(brainId)

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

        val surfaceReceiver = ShowRunner.SurfaceReceiver(surface) { shaderBuffer ->
            val message = BrainShaderMessage(shaderBuffer.shader, shaderBuffer).toBytes()
            udpSocket.sendUdp(brainAddress, Ports.BRAIN, message)

            networkStats.packetsSent++
            networkStats.bytesSent += message.size
        }


        val brainInfo = BrainInfo(brainAddress, brainId, surface, surfaceReceiver)
//        logger.debug("Map ${brainInfo.brainId} to ${brainInfo.surface.describe()}")
        pendingBrainInfos[brainId] = brainInfo
    }

    private fun findModelSurface(surfaceName: String?, brainId: BrainId) =
        surfaceName?.let { model.findModelSurface(surfaceName) } ?: surfaceMappingsByBrain[brainId]

    fun providePanelMapping(brainId: BrainId, surface: Model.Surface) {
        surfaceMappingsByBrain[brainId] = surface
    }

    fun providePixelMapping(surface: Model.Surface, pixelLocations: Array<Vector2>) {
        pixelsBySurface[surface] = pixelLocations
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
}

data class BrainId(val uuid: String)

class BrainInfo(
    val address: Network.Address,
    val brainId: BrainId,
    val surface: Surface,
    val surfaceReceiver: ShowRunner.SurfaceReceiver
)
