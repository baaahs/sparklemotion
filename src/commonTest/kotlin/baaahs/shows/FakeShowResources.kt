package baaahs.shows

import baaahs.*
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.Plugins
import baaahs.glshaders.ShaderFragment
import baaahs.glsl.GlslContext
import baaahs.glsl.RenderSurface

class FakeShowResources(override val glslContext: GlslContext
) : ShowResources {
    override val plugins: Plugins
        get() = Plugins.safe()
    override val showWithStateTopic: PubSub.Topic<ShowWithState> by lazy { createShowWithStateTopic() }
    override val dataFeeds: Map<String, GlslProgram.DataFeed>
        get() = TODO("not implemented")
    override val shaders: Map<String, ShaderFragment>
        get() = TODO("not implemented")

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
    }

    val gadgets: MutableMap<String, Gadget> = mutableMapOf()

//    override val allSurfaces: List<Surface> = emptyList()
//    override val allMovingHeads: List<MovingHead> = emptyList()
//    override val currentBeat: Float = 1f

    private val renderSurfaces = mutableMapOf<Surface, RenderSurface>()

//    override fun getBeatSource(): BeatSource {
//        TODO("not implemented")
//    }

//    override fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
//        TODO("not implemented")
//    }

    override fun <T : Gadget> useGadget(id: String): T {
        return gadgets[id] as T
    }

    fun drawFrame() {
        TODO()
//        renderSurfaces.values.forEach { renderSurface ->
//            renderSurface.updateRenderSurface()
//        }
//        glslRenderer.draw()
    }

    fun <T : Gadget> getGadget(name: String): T {
        return gadgets[name] as T
    }
}