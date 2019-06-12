package baaahs

import baaahs.geom.Vector2
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Pinky(
    val sheepModel: SheepModel,
    val showMetas: List<Show>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val display: PinkyDisplay
) : Network.UdpListener {
    private val link = FragmentingUdpLink(network.link())
    private val beatProvider = PinkyBeatProvider(120.0f)
    private var mapperIsRunning = false
    private var selectedShow = showMetas.first()
        set(value) {
            field = value
            display.selectedShow = value
            showRunner.nextShow = selectedShow
        }

    private val pubSub = PubSub.Server(link, Ports.PINKY_UI_TCP).apply { install(gadgetModule) }
    private val gadgetManager = GadgetManager(pubSub)
    private val showRunner =
        ShowRunner(sheepModel, selectedShow, gadgetManager, emptyList(), beatProvider, dmxUniverse)
    private val surfacesByName = sheepModel.allPanels.associateBy { it.name }
    private val pixelsBySurface = mutableMapOf<Surface, Array<Vector2>>()
    private val surfaceMappingsByBrain = mutableMapOf<BrainId, Surface>()

    private val brainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()
    private val pendingBrainInfos: MutableMap<BrainId, BrainInfo> = mutableMapOf()

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    suspend fun run(): Show.Renderer {
        GlobalScope.launch { beatProvider.run() }

        link.listenUdp(Ports.PINKY, this)

        display.listShows(showMetas)
        display.selectedShow = selectedShow

        pubSub.publish(Topics.availableShows, showMetas.map { showMeta -> showMeta.name }) {}
        val selectedShowChannel = pubSub.publish(Topics.selectedShow, showMetas[0].name) { selectedShow ->
            this.selectedShow = showMetas.find { it.name == selectedShow }!!
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
            display.nextFrameMs = elapsedMs.toInt()
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

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainHelloMessage -> foundBrain(fromAddress, BrainId(message.brainId), message.surfaceName)
            is MapperHelloMessage -> mapperIsRunning = message.isRunning
        }
    }

    private fun maybeSendMapping(address: Network.Address, brainId: BrainId) {
        val surface = surfaceMappingsByBrain[brainId]
        if (surface != null && surface is SheepModel.Panel) {
            val pixelLocations = pixelsBySurface[surface]
            val pixelCount = pixelLocations?.size ?: -1
            val pixelVertices = pixelLocations?.map { Vector2F(it.x.toFloat(), it.y.toFloat()) }
                ?: emptyList()
            val mappingMsg = BrainMappingMessage(brainId, surface.name, pixelCount, pixelVertices)
            link.sendUdp(address, Ports.BRAIN, mappingMsg)
        }
    }

    class UnknownSurface(val brainId: BrainId) : Surface {
        override val pixelCount = SparkleMotion.PIXEL_COUNT_UNKNOWN

        override fun describe(): String = "Unknown surface for $brainId"
        override fun equals(other: Any?): Boolean = other is UnknownSurface && brainId.equals(other.brainId)
        override fun hashCode(): Int = brainId.hashCode()
    }

    private fun foundBrain(
        brainAddress: Network.Address,
        brainId: BrainId,
        surfaceName: String?
    ) {
        val surface = surfaceName?.let { surfacesByName[surfaceName] } ?: UnknownSurface(brainId)
        if (surface is UnknownSurface) maybeSendMapping(brainAddress, brainId)

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
            link.sendUdp(brainAddress, Ports.BRAIN, message)

            networkStats.packetsSent++
            networkStats.bytesSent += message.size
        }


        val brainInfo = BrainInfo(brainAddress, brainId, surface, surfaceReceiver)
//        logger.debug("Map ${brainInfo.brainId} to ${brainInfo.surface.describe()}")
        pendingBrainInfos[brainId] = brainInfo
    }

    fun providePanelMapping(brainId: BrainId, surface: Surface) {
        surfaceMappingsByBrain[brainId] = surface
    }

    fun providePixelMapping(surface: Surface, pixelLocations: Array<Vector2>) {
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
