package baaahs.show.live

import baaahs.fixtures.Fixture
import baaahs.fixtures.RenderPlan
import baaahs.getBang
import baaahs.gl.data.FeedContext
import baaahs.gl.patch.ProgramResolver
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderManager
import baaahs.show.DataSource

data class ActivePatchSet(
    internal val activePatches: List<OpenPatch>,
    private val allDataSources: Map<String, DataSource>,
    private val feeds: Map<DataSource, FeedContext>
) {
    val dataSources by lazy {
        buildSet {
            activePatches.forEach { activePatch ->
                activePatch.dataSources.forEach { add(it) }
            }
        }
    }

    fun createRenderPlan(
        renderManager: RenderManager,
        renderTargets: Collection<FixtureRenderTarget>
    ): RenderPlan {
        val patchResolution = ProgramResolver(renderTargets, this, renderManager)
        return patchResolution.createRenderPlan(allDataSources) { _, dataSource ->
            feeds.getBang(dataSource, "data feed")
        }
    }

    fun forFixture(fixture: Fixture): List<OpenPatch> =
        activePatches.filter { patch -> patch.matches(fixture) }

    interface Builder {
        val show: OpenShow

        fun add(patchHolder: OpenPatchHolder, depth: Int, layoutContainerId: String = "")
    }

    companion object {
        val Empty = ActivePatchSet(emptyList(), emptyMap(), emptyMap())

        internal fun sort(items: List<Item>) =
            items.sortedWith(
                compareBy<Item> { it.depth }
                    .thenBy { it.layoutContainerId }
                    .thenBy { it.patchHolder.title }
                    .thenBy { it.serial }
            ).map { it.patchHolder }
                .flatMap { it.patches }

        internal data class Item(
            val patchHolder: OpenPatchHolder,
            val depth: Int,
            val layoutContainerId: String,
            val serial: Int
        )
    }
}