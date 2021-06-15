package baaahs

import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.Time
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Brain(
    val id: String,
    private val network: Network,
    private val pixels: Pixels,
    private val clock: Clock
) : Network.UdpListener {
    val facade = Facade()

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private var lastInstructionsReceivedAt: Time? = null
    private var modelElementName : String? = null
        set(value) { field = value; facade.notifyChanged() }
    private var pixelCount: Int = SparkleMotion.MAX_PIXEL_COUNT
    private var pixelLocations: List<Vector3F> = emptyList()
    private var currentShaderDesc: ByteArray? = null
    private var currentRenderTree: RenderTree<*>? = null
    private val state: State = State.Unknown

    enum class State { Unknown, Link, Online }

    suspend fun run() {
        link = FragmentingUdpLink(network.link("brain"))
        udpSocket = link.listenUdp(Ports.BRAIN, this)
        sendHello()
    }

    private suspend fun reset() {
        lastInstructionsReceivedAt = null
        modelElementName = null
        pixelCount = SparkleMotion.MAX_PIXEL_COUNT
        pixelLocations = emptyList()
        currentShaderDesc = null
        currentRenderTree = null

        for (i in pixels.indices) pixels[i] = Color.WHITE

        sendHello()
    }

    /**
     * So that the JVM standalone can boot up and have a fixture name without mapping
     */
    fun forcedFixtureName(name: String) {
        modelElementName = name
    }

    private suspend fun sendHello() {
        while (true) {
            val elapsedSinceMessage = clock.now() - (lastInstructionsReceivedAt ?: 0.0)
            if (elapsedSinceMessage > 100) {
                if (lastInstructionsReceivedAt != null) {
                    logger.info { "[$id] haven't heard from Pinky in ${elapsedSinceMessage}s" }
                }
                udpSocket.broadcastUdp(Ports.PINKY, BrainHelloMessage(id, modelElementName))
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
                    val pongData = if (reader.readBoolean()) {
                        reader.readBytes()
                    } else {
                        null
                    }
                    val shaderDesc = reader.readBytes()

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
                        udpSocket.sendUdp(fromAddress, fromPort, PingMessage(pongData, true))
                    }

                }

                Type.BRAIN_ID_REQUEST -> {
                    logger.debug { "[$id] $type: Hello($id, $modelElementName)" }
                    udpSocket.sendUdp(fromAddress, fromPort, BrainHelloMessage(id, modelElementName))
                }

                Type.BRAIN_MAPPING -> {
                    val message = BrainMappingMessage.parse(reader)
                    modelElementName = message.fixtureName
                    pixelCount = message.pixelCount
                    pixelLocations = message.pixelLocations

                    // next frame we'll need to recreate everything...
                    currentShaderDesc = null
                    currentRenderTree = null

                    logger.debug { "[$id] $type: Hello($id, $modelElementName)" }
                    udpSocket.broadcastUdp(Ports.PINKY, BrainHelloMessage(id, modelElementName))
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
            get() = this@Brain.id
        val state: State
            get() = this@Brain.state
        val modelElementName: String?
            get() = this@Brain.modelElementName

        fun reset() {
            logger.info { "Resetting Brain $id!" }
            GlobalScope.launch { this@Brain.reset() }
        }
    }

    companion object {
        val logger = Logger<Brain>()
    }
}
