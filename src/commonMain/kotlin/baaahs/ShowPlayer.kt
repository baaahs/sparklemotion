package baaahs

import baaahs.gl.GlContext
import baaahs.gl.glsl.AnalysisException
import baaahs.gl.glsl.GlslAnalyzer
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.shader.OpenShader
import baaahs.model.ModelInfo
import baaahs.plugin.Plugins
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.live.ShowOpener

interface ShowPlayer {
    val plugins: Plugins
    val glContext: GlContext
    val modelInfo: ModelInfo
    val dataSources: List<DataSource>

    fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource? = null)
    fun <T : Gadget> useGadget(id: String): T = error("override me?")
    fun <T : Gadget> useGadget(dataSource: DataSource): T?

    fun openShader(shader: Shader, addToCache: Boolean = false): OpenShader
    fun openShaderOrNull(shader: Shader, addToCache: Boolean = false): OpenShader? {
        return try {
            openShader(shader, addToCache)
        } catch (e: AnalysisException) {
            null
        }
    }
    fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed
    fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed
    fun openShow(show: Show, showState: ShowState? = null): OpenShow =
        ShowOpener(GlslAnalyzer(plugins), show, this).openShow(showState)

    fun releaseUnused()
}

abstract class BaseShowPlayer(
    final override val plugins: Plugins,
    final override val modelInfo: ModelInfo
) : ShowPlayer {
    private val glslAnalyzer = GlslAnalyzer(plugins)

    private val dataFeeds = mutableMapOf<DataSource, GlslProgram.DataFeed>()
    private val shaders = mutableMapOf<Shader, OpenShader>()

    override val dataSources: List<DataSource> get() = dataFeeds.keys.toList()
    private val dataSourceGadgets: MutableMap<DataSource, Gadget> = mutableMapOf()

    override fun openDataFeed(id: String, dataSource: DataSource): GlslProgram.DataFeed {
        return dataFeeds.getOrPut(dataSource) {
            dataSource.createFeed(this, plugins, id)
        }
    }

    override fun useDataFeed(dataSource: DataSource): GlslProgram.DataFeed {
        return dataFeeds[dataSource]!!
    }

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        controlledDataSource?.let { dataSourceGadgets[controlledDataSource] = gadget }
    }

    override fun <T : Gadget> useGadget(dataSource: DataSource): T? {
        @Suppress("UNCHECKED_CAST")
        return dataSourceGadgets[dataSource] as? T
    }

    override fun openShader(shader: Shader, addToCache: Boolean): OpenShader {
        return if (addToCache) {
            shaders.getOrPut(shader) { glslAnalyzer.openShader(shader) }
        } else {
            shaders[shader] ?: glslAnalyzer.openShader(shader)
        }
    }

    override fun releaseUnused() {
        ArrayList(dataFeeds.entries).forEach { (dataSource, dataFeed) ->
            if (!dataFeed.inUse()) dataFeeds.remove(dataSource)
        }

        ArrayList(shaders.entries).forEach { (shader, openShader) ->
            if (!openShader.inUse()) shaders.remove(shader)
        }
    }
}