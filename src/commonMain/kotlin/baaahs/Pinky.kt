package baaahs

import baaahs.SheepModel.Panel
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.CompositorShader
import baaahs.shaders.PixelShader
import baaahs.shaders.SineWaveShader
import baaahs.shaders.SolidShader
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
    private val link = Network.MoreReliableUdpLink(network.link())
    private val brains: MutableMap<Network.Address, RemoteBrain> = mutableMapOf()
    private val beatProvider = BeatProvider(120.0f)
    private var mapperIsRunning = false
    private var brainsChanged: Boolean = true
    private var showRunner: ShowRunner = ShowRunner(display, brains.values.toList(), beatProvider, dmxUniverse)

    val address: Network.Address get() = link.myAddress

    suspend fun run() {
        GlobalScope.launch { beatProvider.run() }

        link.listenUdp(Ports.PINKY, this)

        display.listShows(showMetas)

        val pubSub = PubSub.Server(link, Ports.PINKY_UI_TCP)
        pubSub.publish(Topics.availableShows, showMetas.map { showMeta -> showMeta.name }.joinToString(",")) {
        }
        pubSub.publish(Topics.selectedShow, showMetas[0].name) { selectedShow ->
            display.selectedShow = showMetas.find { it.name == selectedShow }
        }

        val color = display.color
        if (color != null) {
            val primaryColorChannel = pubSub.publish(Topics.primaryColor, color) {
                display.color = it
                println("display.color = $it")
            }

            display.onPrimaryColorChange = { primaryColorChannel.onChange(display.color!!) }
        }

        showRunner = ShowRunner(display, brains.values.toList(), beatProvider, dmxUniverse)
        val prevSelectedShow = display.selectedShow
        var currentShowMetaData = prevSelectedShow ?: showMetas.random()!!
        val buildShow = { currentShowMetaData.createShow(sheepModel, showRunner) }
        var show = buildShow()

        while (true) {
            if (!mapperIsRunning) {
                if (brainsChanged || display.selectedShow != currentShowMetaData) {
                    currentShowMetaData = prevSelectedShow ?: showMetas.random()!!
                    showRunner = ShowRunner(display, brains.values.toList(), beatProvider, dmxUniverse)
                    show = buildShow()
                    brainsChanged = false
                }

                show.nextFrame()

                // send shader buffers out to brains
//                println("Send frame from ${currentShowMetaData.name}…")
                showRunner.send(link)

//                    show!!.nextFrame(display.color, beatProvider.beat, brains, link)
            } else {
                disableDmx()
            }
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
                foundBrain(RemoteBrain(fromAddress, message.panelName))
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

    inner class BeatProvider(var bpm: Float) {
        private var startTimeMillis = 0L
        private var beatsPerMeasure = 4

        private val millisPerBeat = 1000 / (bpm / 60)

        public val beat: Float
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

class ShowRunner(
    private val pinkyDisplay: PinkyDisplay,
    private val brains: List<RemoteBrain>,
    private val beatProvider: Pinky.BeatProvider,
    private val dmxUniverse: Dmx.Universe
) {
    private val shaders: MutableMap<Shader, MutableList<RemoteBrain>> = hashMapOf()

    fun getColorPicker(): ColorPicker = ColorPicker(pinkyDisplay)

    fun getBeatProvider(): Pinky.BeatProvider = beatProvider

    private fun recordShader(panel: Panel, shader: Shader) {
        shaders[shader] = brains.filter { it.panelName == panel.name }.toMutableList()
    }

    fun getSolidShader(panel: Panel): SolidShader = SolidShader().also { recordShader(panel, it) }

    fun getPixelShader(panel: Panel): PixelShader = PixelShader().also { recordShader(panel, it) }

    fun getSineWaveShader(panel: Panel): SineWaveShader = SineWaveShader().also { recordShader(panel, it) }

    fun getCompositorShader(panel: Panel, shaderA: Shader, shaderB: Shader): CompositorShader {
        val shaderABrains = shaders[shaderA]!!
        val shaderBBrains = shaders[shaderB]!!
        shaders.remove(shaderA)
        shaders.remove(shaderB)
        return CompositorShader(shaderA, shaderB).also { recordShader(panel, it) }
    }

    fun getDmxBuffer(baseChannel: Int, channelCount: Int) =
        dmxUniverse.writer(baseChannel, channelCount)

    fun getMovingHead(movingHead: SheepModel.MovingHead): Shenzarpy {
        val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
        return Shenzarpy(getDmxBuffer(baseChannel, 16))
    }

    fun send(link: Network.Link) {
        shaders.forEach { (shader, remoteBrains) ->
            remoteBrains.forEach { remoteBrain ->
                link.sendUdp(remoteBrain.address, Ports.BRAIN, BrainShaderMessage(shader))
            }
        }

        dmxUniverse.sendFrame()
    }
}

class ColorPicker(private val pinkyDisplay: PinkyDisplay) {
    val color: Color get() = pinkyDisplay.color ?: Color.WHITE
}

class RemoteBrain(val address: Network.Address, val panelName: String)
