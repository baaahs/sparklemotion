package baaahs.shows

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.ShowState
import baaahs.getBang
import baaahs.gl.Toolchain
import baaahs.gl.data.Feed
import baaahs.gl.shader.OpenShader
import baaahs.gl.testToolchain
import baaahs.gl.withCache
import baaahs.model.ModelInfo
import baaahs.show.DataSource
import baaahs.show.Shader
import baaahs.show.Show
import baaahs.show.live.OpenShow
import baaahs.show.live.ShowOpener

class FakeShowPlayer(
    override val modelInfo: ModelInfo = ModelInfo.Empty,
    override val toolchain: Toolchain = testToolchain
) : ShowPlayer {
    private val shaders = mutableMapOf<Shader, OpenShader>()
    private val feeds = mutableMapOf<DataSource, Feed>()
    val gadgets: MutableMap<String, Gadget> = mutableMapOf()
    private val dataSourceGadgets: MutableMap<DataSource, Gadget> = mutableMapOf()

    override fun openFeed(id: String, dataSource: DataSource): Feed =
        feeds.getOrPut(dataSource) { dataSource.createFeed(this, id) }

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