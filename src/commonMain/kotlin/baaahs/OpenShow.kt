package baaahs

import baaahs.glshaders.OpenPatch
import baaahs.glsl.GlslContext
import baaahs.show.*
import baaahs.show.live.LiveShaderInstance

open class OpenPatchy(
    patchy: Patchy, val dataSources: Map<String, DataSource>
) {
    val controlLayout: Map<String, List<Control>> = patchy.controlLayout.mapValues { (_, controlRefs) ->
        controlRefs.map { it.dereference(dataSources) }
    }
}

class OpenShow(
    private val show: Show, private val showResources: ShowResources
) : RefCounted by RefCounter(), OpenPatchy(show, show.dataSources) {
    val id = randomId("show")
    val layouts get() = show.layouts
    val shaders = show.shaders.mapValues { (_, shader) ->
        showResources.openShader(shader, addToCache = true)
    }
    val shaderInstances = show.shaderInstances.mapValues { (_, shaderInstance) ->
        LiveShaderInstance.from(shaderInstance, shaders)
    }

    val dataFeeds = show.dataSources.entries.associate { (id, dataSource) ->
        val dataFeed = showResources.openDataFeed(id, dataSource)
        id to dataFeed
    }
    val scenes = show.scenes.map { OpenScene(it) }

    fun edit(showState: ShowState, block: ShowEditor.() -> Unit = {}): ShowEditor =
        ShowEditor(show, showState).apply(block)

    override fun onFullRelease() {
        shaders.values.forEach { it.release() }
        dataFeeds.values.forEach { it.release() }
    }

    inner class OpenScene(scene: Scene) : OpenPatchy(scene, show.dataSources) {
        val id = randomId("scene")
        val title = scene.title
        val patchSets = scene.patchSets.map { OpenPatchSet(it) }

        inner class OpenPatchSet(patchSet: PatchSet) : OpenPatchy(patchSet, show.dataSources) {
            val id = randomId("patchset")
            val title = patchSet.title
            val patches = patchSet.patches.map { OpenPatch(it, shaderInstances, show.dataSources) }

            fun createRenderPlan(glslContext: GlslContext): RenderPlan {
                val activeDataSources = mutableSetOf<String>()
                val programs = patches.map { patch ->
                    patch to patch.createProgram(glslContext, dataFeeds)
                }
                return RenderPlan(programs)
            }
        }
    }
}
