package baaahs.shows

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.getBang
import baaahs.gl.Toolchain
import baaahs.gl.data.FeedContext
import baaahs.gl.testToolchain
import baaahs.gl.withCache
import baaahs.model.ModelInfo
import baaahs.scene.SceneMonitor
import baaahs.scene.SceneProvider
import baaahs.show.Feed
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.live.ShowOpener

class FakeShowPlayer(
    @Deprecated("Get it some other way", level = DeprecationLevel.WARNING)
    override val sceneProvider: SceneProvider = SceneMonitor(ModelInfo.EmptyScene),
    override val toolchain: Toolchain = testToolchain
) : ShowPlayer {
    val feeds = mutableMapOf<Feed, FeedContext>()
    val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    private val feedGadgets: MutableMap<Feed, Gadget> = mutableMapOf()

    override fun openFeed(id: String, feed: Feed): FeedContext =
        feeds.getOrPut(feed) { feed.open(this, id) }

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed?) {
        gadgets[id] = gadget
        controlledFeed?.let { feedGadgets[controlledFeed] = gadget }
    }

    override fun releaseUnused() {
    }

    override fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets.getBang(id, "gadget") as T
    }

    override fun <T : Gadget> useGadget(feed: Feed): T? {
        @Suppress("UNCHECKED_CAST")
        return feedGadgets[feed] as T?
    }

    fun <T : Gadget> getGadget(name: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets.getBang(name, "gadget") as T
    }

    fun openShow(show: Show, showState: ShowState? = null): OpenShow =
        ShowOpener(toolchain.withCache(show.title), show, this).openShow(showState)
}