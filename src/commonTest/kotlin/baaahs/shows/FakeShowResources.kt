package baaahs.shows

import baaahs.*
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.OpenShader
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.glsl.RenderSurface
import baaahs.show.DataSource
import baaahs.show.Shader

class FakeShowResources(
    override val glslContext: GlslContext,
    val shaders: Map<Shader, OpenShader> = emptyMap(),
    val dataFeeds: Map<DataSource, GlslProgram.DataFeed> = emptyMap()
) : ShowResources {
    override val plugins: Plugins
        get() = Plugins.safe()

    val gadgets: MutableMap<String, Gadget> = mutableMapOf()

    override val showWithStateTopic: PubSub.Topic<ShowWithState> by lazy { createShowWithStateTopic() }

    override fun openShader(shader: Shader): OpenShader =
        shaders.getBang(shader, "shader")

    override fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed =
        dataFeeds.getBang(dataSource, "datafeed")

    override fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed =
        dataFeeds.getBang(dataSource, "datafeed")

    override fun <T : Gadget> createdGadget(id: String, gadget: T) {
    }

    override fun releaseUnused() {
    }

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