package baaahs

import baaahs.geom.Vector2F
import baaahs.io.ByteArrayReader
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import kotlinx.coroutines.delay

class Brain(
    val id: String,
    private val network: Network,
    private val display: BrainDisplay,
    private val pixels: Pixels
) : Network.UdpListener {
    private lateinit var link: Network.Link
    private lateinit var udpSocket: Network.UdpSocket
    private var lastInstructionsReceivedAtMs: Long = 0
    private var surfaceName : String? = null
    private var surface : Surface = UnmappedSurface()
        set(value) { field = value; display.surface = value }
    private var currentShaderDesc: ByteArray? = null
    private var currentRenderTree: RenderTree<*>? = null

    suspend fun run() {
        link = FragmentingUdpLink(network.link())
        udpSocket = link.listenUdp(Ports.BRAIN, this)

        display.id = id
        display.haveLink(link)
        display.onReset = {
            println("Resetting Brain $id!")
            reset()
        }

        sendHello()
    }

    private suspend fun reset() {
        lastInstructionsReceivedAtMs = 0
        surfaceName = null
        surface = UnmappedSurface()
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
                    logger.info("$id: haven't heard from Pinky in ${elapsedSinceMessageMs}ms")
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

        // Inline message parsing here so we can optimize stuff.
        val type = Type.get(reader.readByte())
        // println("Got a message of type ${type}")
        when (type) {
            Type.BRAIN_PANEL_SHADE -> {
                val pongData = if (reader.readBoolean()) { reader.readBytes() } else { null }
                val shaderDesc = reader.readBytes()

                // If possible, use the previously-built Shader stuff:
                val theCurrentShaderDesc = currentShaderDesc
                if (theCurrentShaderDesc == null || !theCurrentShaderDesc.contentEquals(shaderDesc)) {
                    currentShaderDesc = shaderDesc

                    @Suppress("UNCHECKED_CAST")
                    val shader = Shader.parse(ByteArrayReader(shaderDesc)) as Shader<Shader.Buffer>
                    currentRenderTree = RenderTree(
                        shader,
                        shader.createRenderer(surface),
                        shader.createBuffer(surface)
                    )
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
                    MappedSurface(message.pixelCount, message.pixelVertices, message.surfaceName)
                } else {
                    UnmappedSurface()
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
    }

    inner class UnmappedSurface : Surface {
        override val pixelCount: Int = SparkleMotion.MAX_PIXEL_COUNT

        override fun describe(): String = "unmapped"
    }

    inner class MappedSurface(
        override val pixelCount: Int,
        var pixelVertices: List<Vector2F>? = null,
        private val name: String
    ) : Surface {
        override fun describe(): String = name
    }
}
