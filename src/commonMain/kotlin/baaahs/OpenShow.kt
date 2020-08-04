package baaahs

import baaahs.show.*
import baaahs.show.live.LiveShaderInstance

open class OpenPatchy(
    patchy: Patchy,
    allShaderInstances: Map<String, LiveShaderInstance>,
    allDataSources: Map<String, DataSource>
) {
    val title = patchy.title
    val patches = patchy.patches.map { OpenPatch(it, allShaderInstances) }

    val controlLayout: Map<String, List<Control>> = patchy.controlLayout.mapValues { (_, controlRefs) ->
        controlRefs.map { it.dereference(allDataSources) }
    }
}

class OpenPatch(
    val shaderInstances: List<LiveShaderInstance>,
    val surfaces: Surfaces
) {
    constructor(patch: Patch, allShaderInstances: Map<String, LiveShaderInstance>): this(
        patch.shaderInstanceIds.map {
            allShaderInstances.getBang(it, "shader instance")
        },
        patch.surfaces
    )
}

class OpenShow(
    private val show: Show,
    private val showPlayer: ShowPlayer,
    private val allShaderInstances: Map<String, LiveShaderInstance>
) : RefCounted by RefCounter(), OpenPatchy(show, allShaderInstances, show.dataSources) {
    val id = randomId("show")
    val layouts get() = show.layouts

    val allDataSources = show.dataSources

    val dataFeeds = show.dataSources.entries.associate { (id, dataSource) ->
        val dataFeed = showPlayer.openDataFeed(id, dataSource)
        id to dataFeed
    }
    val scenes = show.scenes.map { OpenScene(it) }

    fun edit(showState: ShowState, block: ShowEditor.() -> Unit = {}): ShowEditor =
        ShowEditor(show, showState).apply(block)

    override fun onFullRelease() {
        allShaderInstances.values.forEach { it.release() }
        dataFeeds.values.forEach { it.release() }
    }

    inner class OpenScene(scene: Scene) : OpenPatchy(scene, allShaderInstances, show.dataSources) {
        val id = randomId("scene")
        val patchSets = scene.patchSets.map { OpenPatchSet(it) }

        inner class OpenPatchSet(patchSet: PatchSet) : OpenPatchy(patchSet, allShaderInstances, show.dataSources) {
            val id = randomId("patchset")

            fun activePatches(): List<OpenPatchy> {
                return listOf(this@OpenShow, this@OpenScene, this)
            }
        }
    }
}
