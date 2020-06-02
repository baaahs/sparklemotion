package baaahs

import baaahs.geom.Vector3F
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Brain(
    val id: String,
    private val network: Network,
    private val pixels: Pixels
) : Network.UdpListener {
    val facade = Facade()

    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private var lastInstructionsReceivedAtMs: Long = 0
    private var surfaceName : String? = null
    private var surface : Surface = AnonymousSurface(BrainId(id))
        set(value) { field = value; facade.notifyChanged() }
    private var currentShaderDesc: ByteArray? = null
    private var currentRenderTree: RenderTree<*>? = null
    private val state: State = State.Unknown

    enum class State { Unknown, Link, Online }

    suspend fun run() {
        link = FragmentingUdpLink(network.link())
        udpSocket = link.listenUdp(Ports.BRAIN, this)
        sendHello()
    }

    private suspend fun reset() {
        lastInstructionsReceivedAtMs = 0
        surfaceName = null
        surface = AnonymousSurface(BrainId(id))
        currentShaderDesc = null
        currentRenderTree = null

        for (i in pixels.indices) pixels[i] = Color.WHITE

        sendHello()
    }

    /**
     * So that the JVM standalone can boot up and have a surface name without mapping
     */
    fun forcedSurfaceName(name: String) {
        surfaceName = name
    }

    private suspend fun sendHello() {
        while (true) {
            val elapsedSinceMessageMs = getTimeMillis() - lastInstructionsReceivedAtMs
            if (elapsedSinceMessageMs > 10000) {
                if (lastInstructionsReceivedAtMs != 0L) {
                    logger.info { "$id: haven't heard from Pinky in ${elapsedSinceMessageMs}ms" }
                }
                udpSocket.broadcastUdp(Ports.PINKY, BrainHelloMessage(id, surfaceName))
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
                        val shader = Shader.parse(ByteArrayReader(shaderDesc)) as Shader<Shader.Buffer>
                        val newRenderTree = RenderTree(
                            shader,
                            shader.createRenderer(surface),
                            shader.createBuffer(surface)
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
                    udpSocket.sendUdp(fromAddress, fromPort, BrainHelloMessage(id, surfaceName))
                }

                Type.BRAIN_MAPPING -> {
                    val message = BrainMappingMessage.parse(reader)
                    surfaceName = message.surfaceName
                    surface = if (message.surfaceName != null) {
                        val fakeModelSurface = FakeModelSurface(message.surfaceName)
                        IdentifiedSurface(fakeModelSurface, message.pixelCount, message.pixelLocations)
                    } else {
                        AnonymousSurface(BrainId(id))
                    }

                    // next frame we'll need to recreate everything...
                    currentShaderDesc = null
                    currentRenderTree = null

                    udpSocket.broadcastUdp(Ports.PINKY, BrainHelloMessage(id, surfaceName))
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

    class RenderTree<B : Shader.Buffer>(val shader: Shader<B>, val renderer: Shader.Renderer<B>, val buffer: B) {
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

    class FakeModelSurface(override val name: String, override val description: String = name) : Model.Surface {
        override val expectedPixelCount: Int? = null

        override fun allVertices(): Collection<Vector3F> = emptyList()

        override val faces: List<Model.Face> = emptyList()
        override val lines: List<Model.Line> = emptyList()
    }

    inner class Facade : baaahs.ui.Facade() {
        val id: String
            get() = this@Brain.id
        val state: State
            get() = this@Brain.state
        val surface: Surface
            get() = this@Brain.surface

        fun reset() {
            logger.info { "Resetting Brain $id!" }
            GlobalScope.launch { this@Brain.reset() }
        }
    }

    companion object {
        val logger = Logger("Brain")
    }
}
