package baaahs.shows

import baaahs.*

class FakeShowContext : ShowContext {
    val gadgets: MutableMap<String, Gadget> = mutableMapOf()

    override val allSurfaces: List<Surface> = emptyList()
    override val allUnusedSurfaces: List<Surface> = emptyList()
    override val allMovingHeads: List<MovingHead> = emptyList()
    override val currentBeat: Float = 1f

    private val shaderBuffers = mutableMapOf<Surface, Shader.Buffer>()

    override fun getBeatSource(): BeatSource {
        TODO("not implemented")
    }

    override fun <B : Shader.Buffer> getShaderBuffer(surface: Surface, shader: Shader<B>): B {
        return shader.createBuffer(surface)
            .also { shaderBuffers[surface] = it }
    }

    override fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
        TODO("not implemented")
    }

    override fun <T : Gadget> getGadget(name: String, gadget: T): T {
        gadgets[name] = gadget
        return gadget
    }

    fun drawFrame() {
        shaderBuffers.forEach { (surface, buffer) ->
            val renderer: Shader.Renderer<Shader.Buffer> =
                buffer.shader.createRenderer(surface) as Shader.Renderer<Shader.Buffer>
            renderer.beginFrame(buffer, 1)
            renderer.draw(buffer, 0)
            renderer.endFrame()
        }
    }
}