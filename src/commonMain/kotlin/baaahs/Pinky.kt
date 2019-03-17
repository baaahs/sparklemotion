package baaahs

import baaahs.SheepModel.Panel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

class Pinky(
    val sheepModel: SheepModel,
    val showMetas: List<ShowMeta>,
    val network: Network,
    val dmxUniverse: Dmx.Universe,
    val display: PinkyDisplay
) : Network.Listener {
    private lateinit var link: Network.Link
    private val brains: MutableMap<Network.Address, RemoteBrain> = mutableMapOf()
    private val beatProvider = BeatProvider(120.0f)
    private var mapperIsRunning = false

    fun run() {
        link = network.link()
        link.listen(Ports.PINKY, this)
    }

    private var brainsChanged: Boolean = true

    fun start() {
        GlobalScope.launch {
            run()
        }

        GlobalScope.launch {
            beatProvider.run()
        }

        GlobalScope.launch {
            display.listShows(showMetas)

            var showRunner = ShowRunner(display, brains.values.toList(), dmxUniverse)
            val prevSelectedShow = display.selectedShow
            var currentShowMeta = prevSelectedShow ?: showMetas.random()!!
            val buildShow = { currentShowMeta.createShow(sheepModel, showRunner) }
            var show = buildShow()

            while (true) {
                if (!mapperIsRunning) {
                    if (brainsChanged || display.selectedShow != currentShowMeta) {
                        currentShowMeta = prevSelectedShow ?: showMetas.random()!!
                        showRunner = ShowRunner(display, brains.values.toList(), dmxUniverse)
                        show = buildShow()
                        brainsChanged = false
                    }

                    show.nextFrame()

                    // send shader buffers out to brains
                    showRunner.send(link)

//                    show!!.nextFrame(display.color, beatProvider.beat, brains, link)
                }
                delay(50)
            }
        }
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val message = parse(bytes)
        when (message) {
            is BrainHelloMessage -> {
                foundBrain(RemoteBrain(fromAddress, message.panelName))
            }

            is MapperHelloMessage -> {
                mapperIsRunning = message.isRunning
            }
        }

    }

    @Synchronized
    private fun foundBrain(remoteBrain: RemoteBrain) {
        brains.put(remoteBrain.address, remoteBrain)
        display.brainCount = brains.size

        brainsChanged = true
    }

    inner class BeatProvider(val bpm: Float) {
        var startTimeMillis = 0L
        var beat = 0
        var beatsPerMeasure = 4

        suspend fun run() {
            startTimeMillis = getTimeMillis()

            while (true) {
                display.beat = beat

                val offsetMillis = getTimeMillis() - startTimeMillis
                val millisPerBeat = (1000 / (bpm / 60)).toLong()
                val delayTimeMillis = millisPerBeat - offsetMillis % millisPerBeat
                delay(delayTimeMillis)
                beat = (beat + 1) % beatsPerMeasure
            }
        }
    }
}

class ShowRunner(
    private val pinkyDisplay: PinkyDisplay,
    private val brains: List<RemoteBrain>,
    private val dmxUniverse: Dmx.Universe
) {
    private val brainBuffers: MutableList<Pair<RemoteBrain?, ShaderBuffer>> = mutableListOf()

    fun getColorPicker(): ColorPicker = ColorPicker(pinkyDisplay)

    fun getSolidShaderBuffer(panel: Panel): SolidShaderBuffer {
        val remoteBrain = brains.find { it.panelName == panel.name }
        val buffer = SolidShaderBuffer()
        brainBuffers.add(Pair(remoteBrain, buffer))
        return buffer
    }

    fun getPixelShaderBuffer(panel: Panel): PixelShaderBuffer {
        val remoteBrain = brains.find { it.panelName == panel.name }
        val buffer = PixelShaderBuffer()
        brainBuffers.add(Pair(remoteBrain, buffer))
        return buffer
    }

    fun getDmxBuffer(baseChannel: Int, channelCount: Int) =
        dmxUniverse.writer(baseChannel, channelCount)

    fun getMovingHeadBuffer(movingHead: SheepModel.MovingHead): Shenzarpy {
        val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
        return Shenzarpy(getDmxBuffer(baseChannel, 16))
    }

    fun send(link: Network.Link) {
        brainBuffers.forEach { brainBuffer ->
            val remoteBrain = brainBuffer.first
            val shaderBuffer = brainBuffer.second
//            println("sending color = ${shaderBuffer.color} to ${remoteBrain}")
            if (remoteBrain != null) {
                link.send(remoteBrain.address, Ports.BRAIN, BrainShaderMessage(shaderBuffer))
            }
        }

        dmxUniverse.sendFrame()
    }
}

class ColorPicker(private val pinkyDisplay: PinkyDisplay) {
    val color: Color get() = pinkyDisplay.color ?: Color.WHITE
}

class RemoteBrain(val address: Network.Address, val panelName: String)
