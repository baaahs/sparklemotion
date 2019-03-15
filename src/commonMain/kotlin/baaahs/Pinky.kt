package baaahs

import baaahs.SheepModel.Panel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.jvm.Synchronized

class Pinky(val sheepModel: SheepModel, val network: Network, val dmx: Dmx, val display: PinkyDisplay) : Network.Listener {
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
            var showContext = ShowRunner(display, brains.values.toList(), dmx)
            var show = SomeDumbShow(sheepModel, showContext)

            while (true) {
                if (!mapperIsRunning) {
                    if (brainsChanged) {
                        showContext = ShowRunner(display, brains.values.toList(), dmx)
                        show = SomeDumbShow(sheepModel, showContext)
                        brainsChanged = false
                    }

                    show.nextFrame()

                    // send shader buffers out to brains
                    showContext.send(link)

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
    private val dmx: Dmx
) {
    private val brainBuffers: MutableList<Pair<RemoteBrain?, ShaderBuffer>> = mutableListOf()
    private val dmxBuffers: MutableList<Pair<String, MovingHeadBuffer>> = mutableListOf()

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

    fun getMovingHeadBuffer(movingHead: SheepModel.MovingHead): MovingHeadBuffer {
        val dmxAddresses = dmx.get(movingHead.name)
        val buf = MovingHeadBuffer(ByteArray(dmxAddresses.size), Color.WHITE)
        dmxBuffers.add(Pair(movingHead.name, buf))
        return buf
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

        dmxBuffers.forEach { dmxBuffer ->
            setMovingHeadColor(dmxBuffer.first, dmxBuffer.second.colorIllicitDontUse)
        }
    }
}

class ColorPicker(private val pinkyDisplay: PinkyDisplay) {
    val color: Color get() = pinkyDisplay.color ?: Color.WHITE
}

class RemoteBrain(val address: Network.Address, val panelName: String)
