package baaahs.shows

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.Surface
import baaahs.getBang
import baaahs.glshaders.GlslAnalyzer
import baaahs.glshaders.GlslProgram
import baaahs.glshaders.OpenShader
import baaahs.glshaders.Plugins
import baaahs.glsl.GlslContext
import baaahs.glsl.RenderSurface
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.Shader

class FakeShowPlayer(
    override val glslContext: GlslContext,
    override val modelInfo: ModelInfo = ModelInfo.Empty
) : ShowPlayer {
    override val plugins: Plugins
        get() = Plugins.safe()

    private val shaders = mutableMapOf<Shader, OpenShader>()
    private val dataFeeds = mutableMapOf<DataSource, GlslProgram.DataFeed>()
    val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    override val dataSources: List<DataSource> get() = dataFeeds.keys.toList()

    override fun openShader(shader: Shader, addToCache: Boolean): OpenShader {
        return if (addToCache) {
            shaders.getOrPut(shader) { GlslAnalyzer().asShader(shader) }
        } else {
            shaders[shader] ?: GlslAnalyzer().asShader(shader)
        }
    }

    override fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed =
        dataFeeds.getOrPut(dataSource) { dataSource.createFeed(this, id) }

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