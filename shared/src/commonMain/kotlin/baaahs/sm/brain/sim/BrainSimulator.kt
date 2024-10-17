package baaahs.sm.brain.sim

import baaahs.Color
import baaahs.SparkleMotion
import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.net.Network
import baaahs.net.listenFragmentingUdp
import baaahs.sm.brain.proto.*
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.globalLaunch
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.datetime.Instant
import kotlin.random.Random
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

class BrainSimulator(
    val id: String,
    private val network: Network,
    private val pixels: Pixels,
    private val clock: Clock,
    private val coroutineScope: CoroutineScope
) : Network.UdpListener {
    val facade = Facade()

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private var lastInstructionsReceivedAt: Instant? = null
    private var modelEntityName : String? = null
        set(value) { field = value; facade.notifyChanged() }
    private var pixelCount: Int = SparkleMotion.MAX_PIXEL_COUNT
    private var pixelLocations: List<Vector3F> = emptyList()
    private var currentShaderDesc: ByteArray? = null
    private var currentRenderTree: RenderTree<*>? = null
    private var state: State = State.Booting
    private var keepRunning = true
    private var job: Job? = null

    private val frameChannel = Channel<IncomingFrame>(Channel.CONFLATED) {
        logger.warn { "[$id]: Skipped frame!" }
    }

    enum class State { Booting, Linked, Hello, Mapped, Shading, Rebooting, Stopped }

    fun start() {
        state = State.Booting
        link = network.link("brain")
        udpSocket = link.listenFragmentingUdp(Ports.BRAIN, this)
        state = State.Linked
        facade.notifyChanged()

        keepRunning = true
        job = coroutineScope.launch {
            coroutineScope.launch { handleFrames() }
            coroutineScope.launch { sendHello() }
        }
    }

    fun stop() {
        keepRunning = false
        udpSocket.close()
        state = State.Stopped
        job?.cancel()
        job = null
    }

    private suspend fun reset() {
        lastInstructionsReceivedAt = null
        modelEntityName = null
        pixelCount = SparkleMotion.MAX_PIXEL_COUNT
        pixelLocations = emptyList()
        currentShaderDesc = null
        currentRenderTree = null

        stop()

        globalLaunch {
            delay(Random.nextInt(100).milliseconds)
            state = State.Rebooting
            facade.notifyChanged()
            for (i in pixels.indices) pixels[i] = Color.WHITE

            globalLaunch {
                delay(Random.nextInt(200).milliseconds)
                start()
            }
        }
    }

    /**
     * So that the JVM standalone can boot up and have a fixture name without mapping
     */
    fun forcedFixtureName(name: String) {
        modelEntityName = name
    }

    private suspend fun sendHello() {
        while (keepRunning) {
            val elapsedSinceMessage = lastInstructionsReceivedAt?.let { clock.now() - it }
            if (elapsedSinceMessage == null || elapsedSinceMessage > 100.seconds) {
                if (elapsedSinceMessage != null) {
                    logger.info { "[$id] haven't heard from Pinky in ${elapsedSinceMessage}s" }
                }
                udpSocket.broadcastUdp(
                    Ports.PINKY,
                    BrainHelloMessage(id, modelEntityName, "simulator-firmware")
                )
                state = State.Hello
                facade.notifyChanged()
            }

            delay(5000)
        }
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        lastInstructionsReceivedAt = clock.now()

        val reader = ByteArrayReader(bytes)

        try {
            // Inline message parsing here so we can optimize stuff.
            val type = Type.get(reader.readByte())
//            logger.debug { "Got a message of type ${type}" }
            when (type) {
                Type.BRAIN_PANEL_SHADE -> {
                    GlobalScope.launch { frameChannel.send(IncomingFrame(reader, fromAddress, fromPort)) }
                    if (state != State.Shading) {
                        state = State.Shading
                        facade.notifyChanged()
                    }
                }

                Type.BRAIN_ID_REQUEST -> {
                    logger.debug { "[$id] $type: Hello($id, $modelEntityName)" }
                    udpSocket.sendUdp(fromAddress, fromPort, BrainHelloMessage(id, modelEntityName))
                }

                Type.BRAIN_MAPPING -> {
                    val message = BrainMappingMessage.parse(reader)
                    modelEntityName = message.fixtureName
                    pixelCount = message.pixelCount
                    pixelLocations = message.pixelLocations

                    // next frame we'll need to recreate everything...
                    currentShaderDesc = null
                    currentRenderTree = null
                    state = State.Mapped
                    facade.notifyChanged()

                    logger.debug { "[$id] $type: Hello($id, $modelEntityName)" }
                    udpSocket.broadcastUdp(Ports.PINKY, BrainHelloMessage(id, modelEntityName))
                }

                Type.PING -> {
                    val ping = PingMessage.parse(reader)
                    logger.debug { "[$id] $type: isPong=${ping.isPong}" }
                    if (!ping.isPong) {
                        udpSocket.sendUdp(fromAddress, fromPort, PingMessage(ping.data, isPong = true))
                    }
                }

                // Other message types are ignored by Brains.
                else -> {
                    // no-op
                    logger.debug { "[$id] Unknown message $type" }
                }
            }
        } catch (e: Throwable) {
            logger.error(e) { "[$id] failed to handle a packet." }
        }
    }

    private suspend fun handleFrames() {
        while (keepRunning) {
            val incomingFrame = frameChannel.receive()
            val reader = incomingFrame.reader

            val pongData = if (reader.readBoolean()) reader.readBytesWithSize() else null
            val shaderDesc = reader.readBytesWithSize()

            // If possible, use the previously-built Shader stuff:
            val theCurrentShaderDesc = currentShaderDesc
            if (theCurrentShaderDesc == null || !theCurrentShaderDesc.contentEquals(shaderDesc)) {
                currentShaderDesc = shaderDesc

                @Suppress("UNCHECKED_CAST")
                val shader = BrainShader.parse(ByteArrayReader(shaderDesc)) as BrainShader<BrainShader.Buffer>
                val newRenderTree = RenderTree(
                    shader,
                    shader.createRenderer(),
                    shader.createBuffer(pixelCount)
                )
                currentRenderTree?.release()
                currentRenderTree = newRenderTree
            }

            with(currentRenderTree!!) {
                read(reader)
                draw(pixels)
            }

            if (pongData != null) {
                udpSocket.sendUdp(
                    incomingFrame.fromAddress,
                    incomingFrame.fromPort,
                    PingMessage(pongData, true)
                )
            }
        }
    }

    private class IncomingFrame(
        val reader: ByteArrayReader,
        val fromAddress: Network.Address,
        val fromPort: Int
    )

    class RenderTree<B : BrainShader.Buffer>(val brainShader: BrainShader<B>, val renderer: BrainShader.Renderer<B>, val buffer: B) {
        fun read(reader: ByteArrayReader) = buffer.read(reader)

        fun draw(pixels: Pixels) {
            renderer.beginFrame(buffer, pixels.size)
            for (i in pixels.indices) {
                pixels[i] = renderer.draw(buffer, i) ?: Color.BLACK
            }
            renderer.endFrame()
            pixels.finishedFrame()
        }

        fun release() {
            renderer.release()
        }
    }

    inner class Facade : baaahs.ui.Facade() {
        val id: String
            get() = this@BrainSimulator.id
        val link: Network.Link?
            get() = if (this@BrainSimulator::link.isInitialized) {
                this@BrainSimulator.link
            } else null
        val state: State
            get() = this@BrainSimulator.state
        val modelEntityName: String?
            get() = this@BrainSimulator.modelEntityName

        fun reset() {
            logger.info { "Resetting Brain $id!" }
            globalLaunch { this@BrainSimulator.reset() }
        }
    }

    companion object {
        val logger = Logger<BrainSimulator>()
    }
}