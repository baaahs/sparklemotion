package baaahs.shows

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.Surface
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.RenderSurface
import baaahs.gl.shader.OpenShader
import baaahs.model.ModelInfo
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import baaahs.show.Shader

class FakeShowPlayer(
    override val glContext: GlContext,
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
            shaders.getOrPut(shader) { GlslAnalyzer(Plugins.safe()).openShader(shader) }
        } else {
            shaders[shader] ?: GlslAnalyzer(Plugins.safe()).openShader(shader)
        }
    }

    override fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed =
        dataFeeds.getOrPut(dataSource) { dataSource.createFeed(this, plugins.find(dataSource.pluginPackage), id) }

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