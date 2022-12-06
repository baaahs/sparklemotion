package baaahs

import baaahs.gl.Toolchain
import baaahs.gl.data.FeedContext
import baaahs.gl.shader.OpenShader
import baaahs.gl.withCache
import baaahs.scene.SceneProvider
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.live.ShowOpener

interface ShowPlayer {
    val toolchain: Toolchain

    /**
     * This is for [baaahs.plugin.core.datasource.ModelInfoDataSource], but we should probably find
     * a better way to get it. Don't add more uses.
     */
    @Deprecated("Get it some other way", level = DeprecationLevel.WARNING)
    val sceneProvider: SceneProvider

    fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource? = null)
    fun <T : Gadget> useGadget(id: String): T = error("override me?")
    fun <T : Gadget> useGadget(dataSource: DataSource): T?

    fun openFeed(id: String, dataSource: DataSource): FeedContext

    fun releaseUnused()
}

abstract class BaseShowPlayer(
    final override val toolchain: Toolchain,
    @Deprecated("Get it some other way", level = DeprecationLevel.WARNING)
    final override val sceneProvider: SceneProvider
) : ShowPlayer {
    private val feeds = mutableMapOf<DataSource, FeedContext>()
    private val shaders = mutableMapOf<Shader, OpenShader>()

    private val dataSourceGadgets: MutableMap<DataSource, Gadget> = mutableMapOf()

    private val cachingToolchain = toolchain.withCache(this::class.simpleName ?: "BaseShowPlayer")

    override fun openFeed(id: String, dataSource: DataSource): FeedContext {
        // TODO: This is another reference to feeds, so we should .use() it... but then we'll never release them!
        // TODO: Also, it could conceivably be handed out after it's had onRelease() called. How should we handle this?
        return feeds.getOrPut(dataSource) {
            dataSource.open(this, id)
        }
    }

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        controlledDataSource?.let { dataSourceGadgets[controlledDataSource] = gadget }
    }

    override fun <T : Gadget> useGadget(dataSource: DataSource): T? {
        @Suppress("UNCHECKED_CAST")
        return dataSourceGadgets[dataSource] as? T
    }

    open fun openShow(show: Show, showState: ShowState? = null): OpenShow =
        cachingToolchain.pruneUnused {
            ShowOpener(cachingToolchain, show, this).openShow(showState)
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