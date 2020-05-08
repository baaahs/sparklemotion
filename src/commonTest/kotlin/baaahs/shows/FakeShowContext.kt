package baaahs.shows

import baaahs.*
import baaahs.glsl.GlslRenderer
import baaahs.glsl.GlslRendererTest
import baaahs.shaders.GlslShader
import baaahs.shaders.IGlslShader

class FakeShowContext : ShowContext {
    val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    val glslRenderer = GlslRenderer(FakeGlslContext(), GlslRendererTest.UvTranslatorForTest)

    override val allSurfaces: List<Surface> = emptyList()
    override val allUnusedSurfaces: List<Surface> = emptyList()
    override val allMovingHeads: List<MovingHead> = emptyList()
    override val currentBeat: Float = 1f

    private val surfaceBinders = mutableMapOf<Surface, SurfaceBinder>()

    override fun getBeatSource(): BeatSource {
        TODO("not implemented")
    }

    override fun getShaderBuffer(surface: Surface, shader: IGlslShader): GlslShader.Buffer {
        return shader.createBuffer(surface)
            .also {
                val surfaceBinder = SurfaceBinder(surface, glslRenderer.addSurface(surface))
                surfaceBinder.setBuffer(it)
                surfaceBinders[surface] = surfaceBinder
            }
    }

    override fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
        TODO("not implemented")
    }

    override fun <T : Gadget> getGadget(name: String, gadget: T): T {
        gadgets[name] = gadget
        return gadget
    }

    fun drawFrame() {
        surfaceBinders.values.forEach { surfaceBinder ->
            surfaceBinder.updateRenderSurface()
        }
        glslRenderer.draw()
    }
}