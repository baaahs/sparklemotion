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
    val showMetas: List<Show.MetaData>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val display: PinkyDisplay
) : Network.UdpListener {
    private val link = FragmentingUdpLink(network.link())
    private val brainsById: MutableMap<String, RemoteBrain> = mutableMapOf()
    private val beatProvider = PinkyBeatProvider(120.0f)
    private var mapperIsRunning = false
    private var selectedShow = showMetas.first()
        set(value) {
            field = value
            display.selectedShow = value
            showRunner.nextShow = selectedShow
        }

    private val pubSub = PubSub.Server(link, Ports.PINKY_UI_TCP).apply { install(gadgetModule) }
    private val gadgetProvider = GadgetProvider(pubSub)
    private val showRunner =
        ShowRunner(sheepModel, selectedShow, gadgetProvider, emptyList(), beatProvider, dmxUniverse)
    private val surfacesByName = sheepModel.allPanels.associateBy { it.name }
    private val pixelsBySurface = mutableMapOf<Surface, Array<Vector2>>()
    private val surfacesByBrainId = mutableMapOf<String, Surface>()

    private val surfacesToAdd = mutableSetOf<ShowRunner.SurfaceReceiver>()
    private val surfacesToRemove = mutableSetOf<ShowRunner.SurfaceReceiver>()

    val address: Network.Address get() = link.myAddress
    private val networkStats = NetworkStats()

    suspend fun run(): Show {
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
        if (surfacesToAdd.isNotEmpty() || surfacesToRemove.isNotEmpty()) {
            showRunner.surfacesChanged(surfacesToAdd, surfacesToRemove)
            surfacesToAdd.clear()
            surfacesToRemove.clear()
        }
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
            is BrainHelloMessage -> {
                val surfaceName = message.surfaceName
                val surface = surfacesByName[surfaceName] ?: UnknownSurface(message.brainId)
                foundBrain(RemoteBrain(fromAddress, message.brainId, surface))

                maybeMoreMapping(fromAddress, surfaceName, message)
            }

            is MapperHelloMessage -> {
                mapperIsRunning = message.isRunning
            }
        }
    }

    private fun maybeMoreMapping(address: Network.Address, surfaceName: String?, message: BrainHelloMessage) {
        if (surfaceName == null) {
            val surface = surfacesByBrainId[message.brainId]
            if (surface != null && surface is SheepModel.Panel) {
                val pixelLocations = pixelsBySurface[surface]
                val pixelCount = pixelLocations?.size ?: -1
                val pixelVertices = pixelLocations?.map { Vector2F(it.x.toFloat(), it.y.toFloat()) }
                    ?: emptyList<Vector2F>()
                val mappingMsg = BrainMapping(message.brainId, surface.name, pixelCount, pixelVertices)
                link.sendUdp(address, Ports.BRAIN, mappingMsg)
            }
        }
    }

    class UnknownSurface(val brainId: String) : Surface {
        override val pixelCount = SparkleMotion.PIXEL_COUNT_UNKNOWN

        override fun describe(): String = "Unknown surface for $brainId"
        override fun equals(other: Any?): Boolean = other is UnknownSurface && brainId.equals(other.brainId)
        override fun hashCode(): Int = brainId.hashCode()
    }

    private fun foundBrain(remoteBrain: RemoteBrain) {
        val oldRemoteBrain = brainsById[remoteBrain.brainId]
        if (oldRemoteBrain != null
            && oldRemoteBrain.brainId == remoteBrain.brainId
            && oldRemoteBrain.surface === remoteBrain.surface
        ) {
            // Duplicate packet?
            return
        }

//        println("Found ${remoteBrain.brainId} -> ${remoteBrain.surface}")

        oldRemoteBrain?.let { it.surfaceReceiver?.let { receiver ->
//            println("Remove listener for ${oldRemoteBrain.brainId} -> ${oldRemoteBrain.surface}")
            surfacesToAdd.remove(receiver)
            surfacesToRemove.add(receiver)
        } }

        brainsById.put(remoteBrain.brainId, remoteBrain)
        display.brainCount = brainsById.size

        val surfaceReceiver = ShowRunner.SurfaceReceiver(remoteBrain.surface) { shaderBuffer ->
            val message = BrainShaderMessage(shaderBuffer.shader, shaderBuffer).toBytes()
            link.sendUdp(remoteBrain.address, Ports.BRAIN, message)

            networkStats.packetsSent++
            networkStats.bytesSent += message.size
        }
        surfacesToAdd.add(surfaceReceiver)
        surfacesToRemove.remove(surfaceReceiver)
        remoteBrain.surfaceReceiver = surfaceReceiver
    }

    fun providePanelMapping(brainId: String, surface: Surface) {
        surfacesByBrainId[brainId] = surface
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

class RemoteBrain(
    val address: Network.Address,
    val brainId: String,
    val surface: Surface,
    var surfaceReceiver: ShowRunner.SurfaceReceiver? = null
)
