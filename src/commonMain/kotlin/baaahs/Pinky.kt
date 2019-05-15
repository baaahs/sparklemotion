package baaahs

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
        set(value) { field = value; display.selectedShow = value }
    private lateinit var showRunner: ShowRunner
    private val surfacesByName = sheepModel.allPanels.associateBy { it.name }

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
                foundBrain(RemoteBrain(fromAddress, surfacesByName[message.panelName]!!))
            }

            is MapperHelloMessage -> {
                mapperIsRunning = message.isRunning
            }
        }

    }

    private fun foundBrain(remoteBrain: RemoteBrain) {
        brains.put(remoteBrain.address, remoteBrain)
        display.brainCount = brains.size

        brainsChanged = true
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

class RemoteBrain(val address: Network.Address, val surface: Surface)
