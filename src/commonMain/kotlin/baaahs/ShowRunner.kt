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
    private val shaderBuffers: MutableMap<Surface, MutableList<Shader.Buffer>> = hashMapOf()

    fun getBeatProvider(): Pinky.BeatProvider = beatProvider

    private fun recordShader(surface: Surface, shaderBuffer: Shader.Buffer) {
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
    fun <B : Shader.Buffer> getShaderBuffer(surface: Surface, shader: Shader<B>): B {
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
        bufferA: Shader.Buffer,
        bufferB: Shader.Buffer,
        mode: CompositingMode = CompositingMode.OVERLAY,
        fade: Float = 0.5f
    ): CompositorShader.Buffer {
        return CompositorShader(bufferA.shader, bufferB.shader)
            .createBuffer(bufferA, bufferB)
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
                link.sendUdp(remoteBrain.address,
                    Ports.BRAIN,
                    BrainShaderMessage(shaderBuffer.shader, shaderBuffer)
                )
            }
        }

        dmxUniverse.sendFrame()
    }

    fun <T : Gadget> getGadget(gadget: T) = gadgetProvider.getGadget(gadget)

    fun shutDown() {
        gadgetProvider.clear()
    }
}