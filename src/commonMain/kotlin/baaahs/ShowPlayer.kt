package baaahs

import baaahs.gl.Toolchain
import baaahs.gl.data.Feed
import baaahs.gl.glsl.AnalysisException
import baaahs.gl.shader.OpenShader
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.live.ShowOpener
import baaahs.util.Logger

interface ShowPlayer {
    val toolchain: Toolchain
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
            logger.debug(e) { "Failed to analyze shader \"${shader.title}\"" }
            null
        }
    }
    fun openFeed(id: String, dataSource: DataSource): Feed
    fun useFeed(dataSource: DataSource): Feed
    fun openShow(show: Show, showState: ShowState? = null): OpenShow =
        ShowOpener(toolchain, show, this).openShow(showState)

    fun releaseUnused()

    companion object {
        private val logger = Logger("ShowPlayer")
    }
}

abstract class BaseShowPlayer(
    final override val toolchain: Toolchain,
    final override val modelInfo: ModelInfo
) : ShowPlayer {
    private val feeds = mutableMapOf<DataSource, Feed>()
    private val shaders = mutableMapOf<Shader, OpenShader>()

    override val dataSources: List<DataSource> get() = feeds.keys.toList()
    private val dataSourceGadgets: MutableMap<DataSource, Gadget> = mutableMapOf()

    override fun openFeed(id: String, dataSource: DataSource): Feed {
        return feeds.getOrPut(dataSource) {
            dataSource.createFeed(this, id)
        }
    }

    override fun useFeed(dataSource: DataSource): Feed {
        return feeds[dataSource]!!
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
            shaders.getOrPut(shader) { toolchain.openShader(shader) }
        } else {
            shaders[shader] ?: toolchain.openShader(shader)
        }
    }

    override fun releaseUnused() {
        ArrayList(feeds.entries).forEach { (dataSource, feed) ->
            if (!feed.inUse()) feeds.remove(dataSource)
        }

        ArrayList(shaders.entries).forEach { (shader, openShader) ->
            if (!openShader.inUse()) shaders.remove(shader)
        }
    }
}