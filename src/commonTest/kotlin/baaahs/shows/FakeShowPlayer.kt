package baaahs.shows

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.fixtures.Fixture
import baaahs.getBang
import baaahs.gl.GlContext
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.render.FixtureRenderPlan
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
    private val dataSourceGadgets: MutableMap<DataSource, Gadget> = mutableMapOf()

    override fun openShader(shader: Shader, addToCache: Boolean): OpenShader {
        return if (addToCache) {
            shaders.getOrPut(shader) { GlslAnalyzer(Plugins.safe()).openShader(shader) }
        } else {
            shaders[shader] ?: GlslAnalyzer(Plugins.safe()).openShader(shader)
        }
    }

    override fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed =
        dataFeeds.getOrPut(dataSource) { dataSource.createFeed(this, plugins, id) }

    override fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed =
        dataFeeds.getBang(dataSource, "datafeed")

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        controlledDataSource?.let { dataSourceGadgets[controlledDataSource] = gadget }
    }

    override fun releaseUnused() {
    }

//    override val allSurfaces: List<Surface> = emptyList()
//    override val allMovingHeads: List<MovingHead> = emptyList()
//    override val currentBeat: Float = 1f

    private val renderSurfaces = mutableMapOf<Fixture, FixtureRenderPlan>()

//    override fun getBeatSource(): BeatSource {
//        TODO("not implemented")
//    }

//    override fun getMovingHeadBuffer(movingHead: MovingHead): MovingHead.Buffer {
//        TODO("not implemented")
//    }

    override fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets[id] as T
    }

    override fun <T : Gadget> useGadget(dataSource: DataSource): T? {
        @Suppress("UNCHECKED_CAST")
        return dataSourceGadgets[dataSource] as T?
    }

    fun drawFrame() {
        TODO()
//        renderSurfaces.values.forEach { renderSurface ->
//            renderSurface.updateRenderSurface()
//        }
//        renderEngine.draw()
    }

    fun <T : Gadget> getGadget(name: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets[name] as T
    }
}