package baaahs

import baaahs.fixtures.Fixture
import baaahs.fixtures.PixelArrayDevice
import baaahs.fixtures.anonymousFixture
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.model.ModelInfo
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import baaahs.util.Logger
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Brain(
    val id: String,
    private val network: Network,
    private val pixels: Pixels,
    private val modelInfo: ModelInfo
) : Network.UdpListener {
    val facade = Facade()

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private var lastInstructionsReceivedAtMs: Long = 0
    private var fixtureName : String? = null
    private var fixture : Fixture = anonymousFixture(BrainId(id), modelInfo)
        set(value) { field = value; facade.notifyChanged() }
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
        lastInstructionsReceivedAtMs = 0
        fixtureName = null
        fixture = anonymousFixture(BrainId(id), modelInfo)
        currentShaderDesc = null
        currentRenderTree = null

        for (i in pixels.indices) pixels[i] = Color.WHITE

        sendHello()
    }

    /**
     * So that the JVM standalone can boot up and have a fixture name without mapping
     */
    fun forcedFixtureName(name: String) {
        fixtureName = name
    }

    private suspend fun sendHello() {
        while (true) {
            val elapsedSinceMessageMs = getTimeMillis() - lastInstructionsReceivedAtMs
            if (elapsedSinceMessageMs > 10000) {
                if (lastInstructionsReceivedAtMs != 0L) {
                    logger.info { "$id: haven't heard from Pinky in ${elapsedSinceMessageMs}ms" }
                }
                udpSocket.broadcastUdp(Ports.PINKY, BrainHelloMessage(id, fixtureName))
            }

            delay(5000)
        }
    }

    override fun receive(fromAddress: Network.Address, fromPort: Int, bytes: ByteArray) {
        val now = getTimeMillis()
        lastInstructionsReceivedAtMs = now

        val reader = ByteArrayReader(bytes)

        try {
            // Inline message parsing here so we can optimize stuff.
            val type = Type.get(reader.readByte())
            // println("Got a message of type ${type}")
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
                            shader.createRenderer(fixture),
                            shader.createBuffer(fixture)
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
                    udpSocket.sendUdp(fromAddress, fromPort, BrainHelloMessage(id, fixtureName))
                }

                Type.BRAIN_MAPPING -> {
                    val message = BrainMappingMessage.parse(reader)
                    fixtureName = message.fixtureName
                    fixture = if (message.fixtureName != null) {
                        val fakeModelSurface = Model.Surface(
                            message.fixtureName, message.fixtureName, PixelArrayDevice,
                            null, emptyList(), emptyList()
                        )
                        Fixture(fakeModelSurface, message.pixelCount, message.pixelLocations, PixelArrayDevice)
                    } else {
                        anonymousFixture(BrainId(id), modelInfo)
                    }

                    // next frame we'll need to recreate everything...
                    currentShaderDesc = null
                    currentRenderTree = null

                    udpSocket.broadcastUdp(Ports.PINKY, BrainHelloMessage(id, fixtureName))
                }

                Type.PING -> {
                    val ping = PingMessage.parse(reader)
                    if (!ping.isPong) {
                        udpSocket.sendUdp(fromAddress, fromPort, PingMessage(ping.data, isPong = true))
                    }
                }

                // Other message types are ignored by Brains.
                else -> {
                    // no-op
                }
            }
        } catch (e: Exception) {
            logger.error(e) { "Brain $id failed to handle a packet." }
        }
    }

    class RenderTree<B : BrainShader.Buffer>(val brainShader: BrainShader<B>, val renderer: BrainShader.Renderer<B>, val buffer: B) {
        fun read(reader: ByteArrayReader) = buffer.read(reader)

        fun draw(pixels: Pixels) {
            renderer.beginFrame(buffer, pixels.size)
            for (i in pixels.indices) {
                pixels[i] = renderer.draw(buffer, i)
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
        val fixture: Fixture
            get() = this@Brain.fixture

        fun reset() {
            logger.info { "Resetting Brain $id!" }
            GlobalScope.launch { this@Brain.reset() }
        }
    }

    companion object {
        val logger = Logger("Brain")
    }
}
