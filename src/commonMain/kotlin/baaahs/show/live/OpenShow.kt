package baaahs.show.live

import baaahs.*
import baaahs.show.PatchSet
import baaahs.show.Scene
import baaahs.show.Show
import baaahs.show.mutable.MutableShow

class OpenShow(
    private val show: Show,
    private val showPlayer: ShowPlayer,
    private val allShaderInstances: Map<String, LiveShaderInstance>
) : RefCounted by RefCounter(), OpenPatchHolder(show, allShaderInstances, show.dataSources) {
    val id = randomId("show")
    val layouts get() = show.layouts

    val allDataSources = show.dataSources

    val dataFeeds = show.dataSources.entries.associate { (id, dataSource) ->
        val dataFeed = showPlayer.openDataFeed(id, dataSource)
        id to dataFeed
    }
    val scenes = show.scenes.map { OpenScene(it) }

    fun edit(showState: ShowState, block: MutableShow.() -> Unit = {}): MutableShow =
        MutableShow(show, showState).apply(block)

    override fun onFullRelease() {
        allShaderInstances.values.forEach { it.release() }
        dataFeeds.values.forEach { it.release() }
    }

    inner class OpenScene(scene: Scene) : OpenPatchHolder(scene, allShaderInstances, show.dataSources) {
        val id = randomId("scene")
        val patchSets = scene.patchSets.map { OpenPatchSet(it) }

        inner class OpenPatchSet(patchSet: PatchSet) : OpenPatchHolder(patchSet, allShaderInstances, show.dataSources) {
            val id = randomId("patchset")

            fun activePatches(): List<OpenPatchHolder> {
                return listOf(this@OpenShow, this@OpenScene, this)
            }
        }
    }
}
