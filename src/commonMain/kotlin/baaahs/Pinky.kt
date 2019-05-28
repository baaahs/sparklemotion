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
    private val brains: MutableMap<Network.Address, RemoteBrain> = mutableMapOf()
    private val beatProvider = PinkyBeatProvider(120.0f)
    private var mapperIsRunning = false
    private var brainsChanged: Boolean = true
    private var selectedShow = showMetas.first()
        set(value) {
            field = value; display.selectedShow = value
        }
    private lateinit var showRunner: ShowRunner
    private val surfacesByName = sheepModel.allPanels.associateBy { it.name }
    private val pixelsBySurface = mutableMapOf<Surface, Array<Vector2>>()
    private val surfacesByBrainId = mutableMapOf<String, Surface>()

    val address: Network.Address get() = link.myAddress

    suspend fun run(): Show {
        GlobalScope.launch { beatProvider.run() }

        link.listenUdp(Ports.PINKY, this)

        display.listShows(showMetas)
        display.selectedShow = selectedShow

        val pubSub = PubSub.Server(link, Ports.PINKY_UI_TCP)
        pubSub.install(gadgetModule)

        pubSub.publish(Topics.availableShows, showMetas.map { showMeta -> showMeta.name }) {
        }
        val selectedShowChannel = pubSub.publish(Topics.selectedShow, showMetas[0].name) { selectedShow ->
            this.selectedShow = showMetas.find { it.name == selectedShow }!!
        }

        display.onShowChange = {
            this.selectedShow = display.selectedShow!!
            selectedShowChannel.onChange(this.selectedShow.name)
        }

        val gadgetProvider = GadgetProvider(pubSub)

        val buildShowRunner = {
            ShowRunner(gadgetProvider, brains.values.toList(), beatProvider, dmxUniverse)
        }

        var currentShowMetaData = selectedShow
        val buildShow = {
            selectedShow.createShow(sheepModel, showRunner).also { currentShowMetaData = selectedShow }
                .also { gadgetProvider.sync() }
        }

        showRunner = buildShowRunner()
        var show = buildShow()

        while (true) {
            if (mapperIsRunning) {
                disableDmx()
                delay(50)
                continue
            }

            if (brainsChanged || selectedShow != currentShowMetaData) {
                if (brainsChanged) {
                    logger.debug("Brains changed!")
                }

                showRunner.shutDown()
                showRunner = buildShowRunner()
                show = buildShow()
                brainsChanged = false
            }

            val elapsedMs = time {
                show.nextFrame()
            }
            display.nextFrameMs = elapsedMs.toInt()

            // send shader buffers out to brains
            //                println("Send frame from ${currentShowMetaData.name}…")
            val stats = ShowRunner.Stats()
            showRunner.send(link, stats)
            display.stats = stats

            //                    show!!.nextFrame(display.color, beatProvider.beat, brains, link)

            delay(50)
        }
    }

    private fun disableDmx() {
        dmxUniverse.allOff()
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainHelloMessage -> {
                val panelName = message.panelName
                val surface = surfacesByName[panelName]
                if (surface == null) {
                    maybeMoreMapping(fromAddress, message)
                } else {
                    foundBrain(RemoteBrain(fromAddress, message.brainId, surface))
                }

            }

            is MapperHelloMessage -> {
                mapperIsRunning = message.isRunning
            }
        }
    }

    private fun maybeMoreMapping(address: Network.Address, message: BrainHelloMessage) {
        val surface = surfacesByBrainId[message.brainId]
        if (surface != null && surface is SheepModel.Panel) {
            val pixelLocations = pixelsBySurface[surface]
            val pixelCount = pixelLocations?.size ?: -1
            val pixelVertices = pixelLocations?.map { Vector2F(it.x.toFloat(), it.y.toFloat()) }
                ?: emptyList()
            val mappingMsg = BrainMapping(message.brainId, surface.name, pixelCount, pixelVertices)
            link.sendUdp(address, Ports.BRAIN, mappingMsg)
        }
    }

    private fun unknownSurface(): Surface {
        return object : Surface {
            override val pixelCount = SparkleMotion.PIXEL_COUNT_UNKNOWN
        }
    }

    private fun foundBrain(remoteBrain: RemoteBrain) {
        brains.put(remoteBrain.address, remoteBrain)
        display.brainCount = brains.size

        brainsChanged = true
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
}

class RemoteBrain(val address: Network.Address, val brainId: String, val surface: Surface)
