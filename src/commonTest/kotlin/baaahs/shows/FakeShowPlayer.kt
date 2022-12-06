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
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.live.OpenShow
import baaahs.show.live.ShowOpener

class FakeShowPlayer(
    @Deprecated("Get it some other way", level = DeprecationLevel.WARNING)
    override val sceneProvider: SceneProvider = SceneMonitor(ModelInfo.EmptyScene),
    override val toolchain: Toolchain = testToolchain
) : ShowPlayer {
    val feeds = mutableMapOf<DataSource, FeedContext>()
    val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    private val dataSourceGadgets: MutableMap<DataSource, Gadget> = mutableMapOf()

    override fun openFeed(id: String, dataSource: DataSource): FeedContext =
        feeds.getOrPut(dataSource) { dataSource.open(this, id) }

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledDataSource: DataSource?) {
        gadgets[id] = gadget
        controlledDataSource?.let { dataSourceGadgets[controlledDataSource] = gadget }
    }

    override fun releaseUnused() {
    }

    override fun <T : Gadget> useGadget(id: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets.getBang(id, "gadget") as T
    }

    override fun <T : Gadget> useGadget(dataSource: DataSource): T? {
        @Suppress("UNCHECKED_CAST")
        return dataSourceGadgets[dataSource] as T?
    }

    fun <T : Gadget> getGadget(name: String): T {
        @Suppress("UNCHECKED_CAST")
        return gadgets.getBang(name, "gadget") as T
    }

    fun openShow(show: Show, showState: ShowState? = null): OpenShow =
        ShowOpener(toolchain.withCache(show.title), show, this).openShow(showState)
}