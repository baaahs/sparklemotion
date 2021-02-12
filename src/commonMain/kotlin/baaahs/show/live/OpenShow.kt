package baaahs.show.live

import baaahs.*
import baaahs.fixtures.Fixture
import baaahs.fixtures.RenderPlan
import baaahs.gl.data.Feed
import baaahs.gl.patch.PatchResolver
import baaahs.gl.render.FixtureRenderTarget
import baaahs.gl.render.RenderManager
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.mutable.MutableShow
import baaahs.util.Logger

interface OpenContext {
    val allControls: List<OpenControl>

    fun findControl(id: String): OpenControl?
    fun getControl(id: String): OpenControl
    fun getDataSource(id: String): DataSource
    fun getShaderInstance(it: String): LiveShaderInstance
    fun release()
}

class OpenShow(
    private val show: Show,
    private val showPlayer: ShowPlayer,
    private val openContext: OpenContext,
) : OpenPatchHolder(show, openContext), RefCounted by RefCounter() {
    val id = randomId("show")
    val allProblems: List<ShowProblem>
        get() = run {
            arrayListOf<ShowProblem>().apply {
                object : OpenShowVisitor() {
                    override fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
                        addAll(openPatchHolder.problems)
                        super.visitPatchHolder(openPatchHolder)
                    }
                }.apply { visitShow(this@OpenShow) }
            }
        }
    val layouts get() = show.layouts
    val allDataSources = show.dataSources
    val allControls: List<OpenControl> = openContext.allControls

    val feeds = show.dataSources.entries.associate { (id, dataSource) ->
        val feed = showPlayer.openFeed(id, dataSource)
        dataSource to feed
    }

    /**
     * Don't hold on to MutableShows; create them, apply changes, and commit or abandon them promptly!
     */
    fun edit(block: MutableShow.() -> Unit = {}): MutableShow =
        MutableShow(show).apply(block)

    fun activePatchSet(): ActivePatchSet {
        val builder = ActivePatchSetBuilder(this)
        addTo(builder, 0)
        return builder.build()
    }

    override fun onFullRelease() {
        openContext.release()
        feeds.values.forEach { it.release() }
    }

    fun applyConstraints() {
        allControls.forEach { it.applyConstraints() }
    }

    fun getEnabledSwitchState(): Set<String> {
        return allControls
            .filterIsInstance<OpenButtonControl>()
            .mapNotNull { if (it.isPressed) it.id else null }
            .toSet()
    }

    fun getShowState(): ShowState {
        return ShowState(
            allControls.mapNotNull { control ->
                control.getState()?.let { control.id to it }
            }.associate { it }
        )
    }

    fun applyState(showState: ShowState) {
        showState.controls.forEach { (id, state) ->
            val control = openContext.findControl(id)
            if (control != null) {
                control.applyState(state)
            } else {
                logger.debug { "Can't apply state to unknown control \"$id\"" }
            }
        }
    }

    companion object {
        private val logger = Logger("OpenShow")
    }
}

data class ActivePatchSet(
    internal val activePatches: List<OpenPatch>,
    private val allDataSources: Map<String, DataSource>,
    private val feeds: Map<DataSource, Feed>
) {
    fun createRenderPlan(
        renderManager: RenderManager,
        renderTargets: MutableCollection<FixtureRenderTarget>
    ): RenderPlan {
        val patchResolution = PatchResolver(renderTargets, this, renderManager)
        return patchResolution.createRenderPlan(allDataSources) { _, dataSource ->
            feeds.getBang(dataSource, "data feed")
        }
    }

    fun forFixture(fixture: Fixture): List<OpenPatch> =
        activePatches.filter { patch -> patch.matches(fixture) }

    companion object {
        val Empty = ActivePatchSet(emptyList(), emptyMap(), emptyMap())
    }
}

class ActivePatchSetBuilder(private val openShow: OpenShow) {
    private val items = arrayListOf<Item>()
    private var nextSerial = 0

    fun add(patchHolder: OpenPatchHolder, depth: Int, layoutContainerId: String = "") {
        items.add(Item(patchHolder, depth, layoutContainerId, nextSerial++))
    }

    fun build(): ActivePatchSet {
        return ActivePatchSet(
            items.sortedWith(
                compareBy<Item> { it.depth }
                    .thenBy { it.layoutContainerId }
                    .thenBy { it.serial }
            ).map { it.patchHolder }
                .flatMap { it.patches },
            openShow.allDataSources,
            openShow.feeds
        )
    }

    private data class Item(
        val patchHolder: OpenPatchHolder,
        val depth: Int,
        val layoutContainerId: String,
        val serial: Int
    )
}

abstract class OpenShowVisitor {
    open fun visitShow(openShow: OpenShow) {
        visitPatchHolder(openShow)
    }

    open fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
        openPatchHolder.patches.forEach {
            visitPatch(it)
        }

        openPatchHolder.controlLayout.forEach { (panelName, openControls) ->
            openControls.forEach { openControl ->
                visitPlacedControl(panelName, openControl)
            }
        }
    }

    open fun visitPlacedControl(panelName: String, openControl: OpenControl) {
        visitControl(openControl)
    }

    open fun visitButtonGroupButton(controlContainer: ControlContainer, openControl: OpenControl) {
        visitControl(openControl)
    }

    open fun visitControl(openControl: OpenControl) {
        if (openControl is OpenPatchHolder) {
            visitPatchHolder(openControl)
        }

        if (openControl is ControlContainer) {
            visitControlContainer(openControl)
        }
    }

    open fun visitControlContainer(controlContainer: ControlContainer) {
        controlContainer.containedControls().forEach { containedControl ->
            visitButtonGroupButton(controlContainer, containedControl)
        }
    }

    open fun visitPatch(openPatch: OpenPatch) {
    }
}