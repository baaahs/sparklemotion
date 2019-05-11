package baaahs

import baaahs.net.Network
import baaahs.proto.BrainShaderMessage
import baaahs.proto.Ports
import baaahs.shaders.CompositingMode
import baaahs.shaders.CompositorShader

class ShowRunner(
    private val gadgetProvider: GadgetProvider,
    brains: List<RemoteBrain>,
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

    fun send(link: Network.Link, stats: Stats? = null) {
        shaderBuffers.forEach { (surface, shaderBuffers) ->
            if (shaderBuffers.size != 1) {
                throw IllegalStateException("Too many shader buffers for $surface: $shaderBuffers")
            }

            val shaderBuffer = shaderBuffers.first()
            val remoteBrains = brainsBySurface[surface]
            if (remoteBrains != null && remoteBrains.isNotEmpty()) {
                val messageBytes = BrainShaderMessage(shaderBuffer.shader, shaderBuffer).toBytes()
                remoteBrains.forEach { remoteBrain ->
                    link.sendUdp(
                        remoteBrain.address,
                        Ports.BRAIN,
                        messageBytes
                    )
                }
                stats?.apply {
                    bytesSent += messageBytes.size
                    packetsSent += 1
                }
            }
        }

        dmxUniverse.sendFrame()
    }

    fun <T : Gadget> getGadget(gadget: T) = gadgetProvider.getGadget(gadget)

    fun shutDown() {
        gadgetProvider.clear()
    }

    class Stats(var bytesSent: Int = 0, var packetsSent: Int = 0)
}