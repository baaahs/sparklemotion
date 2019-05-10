package baaahs

import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.serializer

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

        val pubSub = PubSub.Server(link, Ports.PINKY_UI_TCP)
        pubSub.install(gadgetModule)

        pubSub.publish(Topics.availableShows, showMetas.map { showMeta -> showMeta.name }) {
        }
        pubSub.publish(Topics.selectedShow, showMetas[0].name) { selectedShow ->
            this.selectedShow = showMetas.find { it.name == selectedShow }!!
        }

        val color = display.color
        if (color != null) {
            val primaryColorChannel = pubSub.publish(Topics.primaryColor, color) {
                display.color = it
                println("display.color = $it")
            }

            display.onPrimaryColorChange = { primaryColorChannel.onChange(display.color!!) }
        }

        val gadgetProvider = GadgetProvider(pubSub)

        val buildShowRunner = {
            ShowRunner(gadgetProvider, display, brains.values.toList(), beatProvider, dmxUniverse)
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

            show.nextFrame()

            // send shader buffers out to brains
            //                println("Send frame from ${currentShowMetaData.name}…")
            showRunner.send(link)

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

class GadgetProvider(private val pubSub: PubSub.Server) {
    val jsonParser = Json(JsonConfiguration.Stable)
    private val activeGadgets = mutableListOf<GadgetData>()
    private val activeGadgetChannel = pubSub.publish(Topics.activeGadgets, activeGadgets) {  }

    private val gadgets = mutableMapOf<Gadget, GadgetChannel>()
    private var nextGadgetId = 1

    fun <G : Gadget> getGadget(gadget: G): G {
        val gadgetId = nextGadgetId++

        val topic =
            PubSub.Topic("/gadgets/${gadget::class.simpleName}/$gadgetId", String.serializer())

        val channel = pubSub.publish(topic, gadget.toJson().toString()) { updated ->
            gadget.setFromJson(jsonParser.parseJson(updated))
        }
        gadgets[gadget] = GadgetChannel(topic, channel)

        activeGadgets.add(GadgetData(gadget, topic.name))
        activeGadgetChannel.onChange(activeGadgets)

        return gadget
    }

    fun clear() {
        gadgets.values.forEach { gadgetChannel -> gadgetChannel.channel.unsubscribe() }
        gadgets.clear()
        activeGadgets.clear()
    }

    class GadgetChannel(val topic: PubSub.Topic<String>, val channel: PubSub.Observer<String>)
}

class ShowRunner(
    private val gadgetProvider: GadgetProvider,
    private val pinkyDisplay: PinkyDisplay,
    private val brains: List<RemoteBrain>,
    private val beatProvider: Pinky.BeatProvider,
    private val dmxUniverse: Dmx.Universe
) {
    private val brainsBySurface = brains.groupBy { it.surface }
    private val shaderBuffers: MutableMap<Surface, MutableList<ShaderBuffer>> = hashMapOf()

    fun getBeatProvider(): Pinky.BeatProvider = beatProvider

    private fun recordShader(surface: Surface, shaderBuffer: ShaderBuffer) {
        val buffersForSurface = shaderBuffers.getOrPut(surface) { mutableListOf() }

        if (shaderBuffer is CompositorShader.Buffer) {
            if (!buffersForSurface.remove(shaderBuffer.aShaderBuffer)
                || !buffersForSurface.remove(shaderBuffer.bShaderBuffer)
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

    fun <T : Gadget> getGadget(gadget: T) = gadgetProvider.getGadget(gadget)

    fun shutDown() {
        gadgetProvider.clear()
    }
}

class RemoteBrain(val address: Network.Address, val surface: Surface)
