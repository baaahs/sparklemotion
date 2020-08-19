package baaahs.show.live

import baaahs.*
import baaahs.show.DataSource
import baaahs.show.PatchSet
import baaahs.show.Scene
import baaahs.show.Show
import baaahs.show.mutable.MutableShow

interface OpenContext {
    val allControls: List<OpenControl>

    fun getControl(it: String): OpenControl
    fun getDataSource(id: String): DataSource
    fun getShaderInstance(it: String): LiveShaderInstance
    fun release()
}

class OpenShow(
    private val show: Show,
    private val showPlayer: ShowPlayer,
    private val openContext: OpenContext
) : OpenPatchHolder(show, openContext), RefCounted by RefCounter() {
    val id = randomId("show")
    val layouts get() = show.layouts

    val allDataSources = show.dataSources
    val allControls: List<OpenControl> = openContext.allControls

    val dataFeeds = show.dataSources.entries.associate { (id, dataSource) ->
        val dataFeed = showPlayer.openDataFeed(id, dataSource)
        dataSource to dataFeed
    }
    val scenes = show.scenes.map { OpenScene(it) }

    fun edit(showState: ShowState, block: MutableShow.() -> Unit = {}): MutableShow =
        MutableShow(show, showState).apply(block)

    override fun onFullRelease() {
        openContext.release()
        dataFeeds.values.forEach { it.release() }
    }

    inner class OpenScene(scene: Scene) : OpenPatchHolder(scene, openContext) {
        val id = randomId("scene")
        val patchSets = scene.patchSets.map { OpenPatchSet(it) }

        inner class OpenPatchSet(patchSet: PatchSet) : OpenPatchHolder(patchSet, openContext) {
            val id = randomId("patchset")

            fun activePatches(): List<OpenPatchHolder> {
                return listOf(this@OpenShow, this@OpenScene, this)
            }
        }
    }
}
