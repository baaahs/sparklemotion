package baaahs

import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
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
    private val slider = Slider()
    private val link = FragmentingUdpLink(network.link())
    private val brains: MutableMap<Network.Address, RemoteBrain> = mutableMapOf()
    private val beatProvider = BeatProvider(120.0f)
    private var mapperIsRunning = false
    private var brainsChanged: Boolean = true
    private var showRunner: ShowRunner = ShowRunner(slider, display, brains.values.toList(), beatProvider, dmxUniverse)
    private val surfacesByName = sheepModel.allPanels.associateBy { it.name }

    val address: Network.Address get() = link.myAddress

    suspend fun run() {
        GlobalScope.launch { beatProvider.run() }

        link.listenUdp(Ports.PINKY, this)

        display.listShows(showMetas)

        val pubSub = PubSub.Server(link, Ports.PINKY_UI_TCP)
        pubSub.publish(Topics.availableShows, showMetas.map { showMeta -> showMeta.name }) {
        }
        pubSub.publish(Topics.selectedShow, showMetas[0].name) { selectedShow ->
            display.selectedShow = showMetas.find { it.name == selectedShow }
        }

        pubSub.publish(Topics.sliderInput, slider.value) { message ->
            slider.value = message
        }

        val color = display.color
        if (color != null) {
            val primaryColorChannel = pubSub.publish(Topics.primaryColor, color) {
                display.color = it
                println("display.color = $it")
            }

            display.onPrimaryColorChange = { primaryColorChannel.onChange(display.color!!) }
        }

        showRunner = ShowRunner(slider, display, brains.values.toList(), beatProvider, dmxUniverse)
        val prevSelectedShow = display.selectedShow
        var currentShowMetaData = prevSelectedShow ?: showMetas.random()!!
        val buildShow = { currentShowMetaData.createShow(sheepModel, showRunner) }
        var show = buildShow()

        while (true) {
            if (!mapperIsRunning) {
                if (brainsChanged || display.selectedShow != currentShowMetaData) {
                    currentShowMetaData = prevSelectedShow ?: showMetas.random()!!
                    showRunner = ShowRunner(slider, display, brains.values.toList(), beatProvider, dmxUniverse)
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
    private val slider: Slider,
    private val pinkyDisplay: PinkyDisplay,
    private val brains: List<RemoteBrain>,
    private val beatProvider: Pinky.BeatProvider,
    private val dmxUniverse: Dmx.Universe
) {
    private val brainsBySurface = brains.groupBy { it.surface }
    private val shaderBuffers: MutableMap<Surface, MutableList<ShaderBuffer>> = hashMapOf()

    fun getColorPicker(): ColorPicker = ColorPicker(pinkyDisplay)

    fun getBeatProvider(): Pinky.BeatProvider = beatProvider

    private fun recordShader(surface: Surface, shaderBuffer: ShaderBuffer) {
        val buffersForSurface = shaderBuffers.getOrPut(surface) { mutableListOf() }

        if (shaderBuffer is CompositorShader.Buffer) {
            if (!buffersForSurface.remove(shaderBuffer.bufferA)
                || !buffersForSurface.remove(shaderBuffer.bufferB)
            ) {
                throw IllegalStateException("Composite of unknown shader buffers!")
            }
        }

        buffersForSurface += shaderBuffer
    }

    /**
     * Obtain a shader buffer which can be used to control the illumination of a surface.
     *
     * @param surface The surface we're shading.
     * @param shader The type of shader.
     * @return A shader buffer of the appropriate type.
     */
    fun <B : ShaderBuffer> getShaderBuffer(surface: Surface, shader: Shader<B>): B {
        val buffer = shader.createBuffer(surface)
        recordShader(surface, buffer)
        return buffer
    }

    /**
     * Obtain a compositing shader buffer which can be used to blend two other shaders together.
     *
     * The shaders must already have been obtained using [getShaderBuffer].
     */
    fun getCompositorBuffer(
        surface: Surface,
        shaderBufferA: ShaderBuffer,
        shaderBufferB: ShaderBuffer,
        mode: CompositingMode = CompositingMode.OVERLAY,
        fade: Float = 0.5f
    ): CompositorShader.Buffer {
        return CompositorShader(shaderBufferA.shader, shaderBufferB.shader)
            .createBuffer(shaderBufferA, shaderBufferB)
            .also {
                it.mode = mode
                it.fade = fade
                recordShader(surface, it)
            }
    }

    fun getDmxBuffer(baseChannel: Int, channelCount: Int) =
        dmxUniverse.writer(baseChannel, channelCount)

    fun getMovingHead(movingHead: SheepModel.MovingHead): Shenzarpy {
        val baseChannel = Config.DMX_DEVICES[movingHead.name]!!
        return Shenzarpy(getDmxBuffer(baseChannel, 16))
    }

    fun send(link: Network.Link) {
        shaderBuffers.forEach { (surface, shaderBuffers) ->
            if (shaderBuffers.size != 1) {
                throw IllegalStateException("Too many shader buffers for $surface: $shaderBuffers")
            }

            val shaderBuffer = shaderBuffers.first()
            val remoteBrains = brainsBySurface[surface]
            remoteBrains?.forEach { remoteBrain ->
                link.sendUdp(remoteBrain.address, Ports.BRAIN, BrainShaderMessage(shaderBuffer.shader, shaderBuffer))
            }
        }

        dmxUniverse.sendFrame()
    }

    fun getSlider(): Slider = slider
}

class Slider(var value: Float = 0f) {
}

class ColorPicker(private val pinkyDisplay: PinkyDisplay) {
    val color: Color get() = pinkyDisplay.color ?: Color.WHITE
}

class RemoteBrain(val address: Network.Address, val surface: Surface)
