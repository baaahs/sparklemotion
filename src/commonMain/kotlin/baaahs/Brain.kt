package baaahs

import baaahs.io.ByteArrayReader
import baaahs.net.FragmentingUdpLink
import baaahs.net.Network
import baaahs.proto.*
import kotlinx.coroutines.delay

class Brain(
    private val network: Network,
    private val display: BrainDisplay,
    private val pixels: Pixels,
    private val illicitPanelHint: SheepModel.Panel
) : Network.UdpListener {
    private lateinit var link: Network.Link
    private var receivingInstructions: Boolean = false
    private val surface = NotReallyASurface()
    private var currentShaderDesc: ByteArray? = null
    private var currentShaderBits: ShaderBits<*>? = null

    inner class NotReallyASurface : Surface {
        override val pixelCount: Int = SparkleMotion.DEFAULT_PIXEL_COUNT
    }

    suspend fun run() {
        link = FragmentingUdpLink(network.link())
        link.listenUdp(Ports.BRAIN, this)
        display.haveLink(link)

        sendHello()
    }

    private suspend fun sendHello() {
        while (true) {
            if (!receivingInstructions) {
                link.broadcastUdp(Ports.PINKY, BrainHelloMessage(illicitPanelHint.name))
            }

            delay(60000)
        }
    }

    class ShaderBits<B : Shader.Buffer>(val shader: Shader<B>, val renderer: Shader.Renderer<B>, val buffer: B) {
        fun read(reader: ByteArrayReader) = buffer.read(reader)
        fun draw() = renderer.draw(buffer)
    }

    override fun receive(fromAddress: Network.Address, bytes: ByteArray) {
        val reader = ByteArrayReader(bytes)

        // Inline message parsing here so we can optimize stuff.
        val type = Type.get(reader.readByte())
        when (type) {
            Type.BRAIN_PANEL_SHADE -> {
                val shaderDesc = reader.readBytes()

                // If possible, use the previously-built Shader stuff:
                val theCurrentShaderDesc = currentShaderDesc
                if (theCurrentShaderDesc == null || !theCurrentShaderDesc.contentEquals(shaderDesc)) {
                    currentShaderDesc = shaderDesc

                    @Suppress("UNCHECKED_CAST")
                    val shader = Shader.parse(ByteArrayReader(shaderDesc)) as Shader<Shader.Buffer>
                    currentShaderBits = ShaderBits(
                        shader,
                        shader.createRenderer(pixels),
                        shader.createBuffer(surface)
                    )
                }

                with(currentShaderBits!!) {
                    read(reader)
                    draw()
                }
            }

            Type.BRAIN_ID_REQUEST -> {
                val message = BrainIdRequest.parse(reader)
                link.sendUdp(fromAddress, message.port, BrainIdResponse(illicitPanelHint.name))
            }

            // Other message types are ignored by Brains.
            else -> {
                // no-op
            }
        }
    }
}
