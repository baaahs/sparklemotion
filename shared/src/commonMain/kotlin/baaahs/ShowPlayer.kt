package baaahs

import baaahs.gl.GlContext
import baaahs.gl.Toolchain
import baaahs.gl.data.EngineFeedContext
import baaahs.gl.data.FeedContext
import baaahs.gl.data.ProgramFeedContext
import baaahs.gl.glsl.GlslProgram
import baaahs.gl.shader.OpenShader
import baaahs.gl.withCache
import baaahs.plugin.Plugins
import baaahs.scene.SceneProvider
import baaahs.show.*
import baaahs.show.live.OpenShow
import baaahs.show.live.ShowOpener
import baaahs.ui.addObserver
import baaahs.util.Clock
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter

interface ShowPlayer {
    fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed? = null)

    fun openFeed(id: String, feed: Feed): FeedContext

    fun releaseUnused()
}

abstract class BaseShowPlayer(
    val toolchain: Toolchain,
    @Deprecated("Get it some other way", level = DeprecationLevel.WARNING)
    final override val sceneProvider: SceneProvider
) : ShowPlayer, FeedOpenContext {
    override val clock: Clock
        get() = toolchain.plugins.pluginContext.clock
    override val plugins: Plugins
        get() = toolchain.plugins

    private val feeds = mutableMapOf<Feed, FeedContext>()
    private val shaders = mutableMapOf<Shader, OpenShader>()

    private val feedGadgets: MutableMap<Feed, Gadget> = mutableMapOf()

    private val cachingToolchain = toolchain.withCache(this::class.simpleName ?: "BaseShowPlayer")

    open fun onActivePatchSetMayHaveChanged() {}

    override fun openFeed(id: String, feed: Feed): FeedContext {
        // TODO: This is another reference to feeds, so we should .use() it... but then we'll never release them!
        // TODO: Also, it could conceivably be handed out after it's had onRelease() called. How should we handle this?
        return feeds.getOrPut(feed) {
            try {
                feed.open(this, id)
            } catch (e: Error) {
                logger.error(e) { "Can't open feed $id" }

                return object : FeedContext, RefCounted by RefCounter() {
                    override fun bind(gl: GlContext): EngineFeedContext = object : EngineFeedContext {
                        override fun bind(glslProgram: GlslProgram): ProgramFeedContext {
                            return object : ProgramFeedContext {}
                        }
                    }
                }
            }
        }
    }

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed?) {
        controlledFeed?.let { feedGadgets[controlledFeed] = gadget }
    }

    override fun <T : Gadget> useGadget(feed: Feed): T? {
        @Suppress("UNCHECKED_CAST")
        return feedGadgets[feed] as? T
    }

    open fun openShow(show: Show, showState: ShowState? = null): OpenShow =
        cachingToolchain.pruneUnused {
            ShowOpener(cachingToolchain, show, this)
                .openShow(showState)
                .apply {
                    activePatchSetMonitor.addObserver {
                        onActivePatchSetMayHaveChanged()
                    }
                }
        }

    override fun releaseUnused() {
        ArrayList(feeds.entries).forEach { (feed, feedContext) ->
            if (!feedContext.inUse()) feeds.remove(feed)
        }

        ArrayList(shaders.entries).forEach { (shader, openShader) ->
            if (!openShader.inUse()) shaders.remove(shader)
        }
    }

    companion object {
        private val logger = Logger<BaseShowPlayer>()
    }
}