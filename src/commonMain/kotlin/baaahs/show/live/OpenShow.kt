package baaahs.show.live

import baaahs.*
import baaahs.show.DataSource
import baaahs.show.Show
import baaahs.show.ShowContext
import baaahs.show.mutable.MutableShow

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
    private val openContext: OpenContext
) : OpenPatchHolder(show, openContext), RefCounted by RefCounter() {
    val id = randomId("show")
    val layouts get() = show.layouts
    val showContext: ShowContext
        get() = show

    val allDataSources = show.dataSources
    val allControls: List<OpenControl> = openContext.allControls

    val dataFeeds = show.dataSources.entries.associate { (id, dataSource) ->
        val dataFeed = showPlayer.openDataFeed(id, dataSource)
        dataSource to dataFeed
    }

    fun edit(block: MutableShow.() -> Unit = {}): MutableShow =
        MutableShow(show).apply(block)

    fun activeSet(): ActiveSet {
        val builder = ActiveSetBuilder()
        addTo(builder, 0)
        return builder.build()
    }

    override fun onFullRelease() {
        openContext.release()
        dataFeeds.values.forEach { it.release() }
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

data class ActiveSet(val items: List<ActiveSetBuilder.Item>) {
    fun getPatchHolders(): List<OpenPatchHolder> {
        return items.map { it.patchHolder }
    }

    fun getActivePatches() : List<OpenPatch> {
        return getPatchHolders().flatMap { it.patches }
    }
}

class ActiveSetBuilder {
    val items = arrayListOf<Item>()
    var nextSerial = 0

    fun add(patchHolder: OpenPatchHolder, depth: Int, panelId: String = "") {
        items.add(Item(patchHolder, depth, panelId, nextSerial++))
    }

    fun build(): ActiveSet {
        return ActiveSet(
            items.sortedWith(
                compareBy<Item> { it.depth }
                    .thenBy { it.panelId }
                    .thenBy { it.serial }
            )
        )
    }

    data class Item(val patchHolder: OpenPatchHolder, val depth: Int, val panelId: String, val serial: Int)
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
        if (openControl is OpenPatchHolder) {
            visitPatchHolder(openControl)
        }

        if (openControl is ControlContainer) {
            visitControlContainer(openControl)
        }
    }

    open fun visitControlContainer(openControl: ControlContainer) {
        openControl.containedControls().forEach { containedControl ->
            if (containedControl.isActive() && containedControl is OpenPatchHolder) {
                visitPatchHolder(containedControl)
            }
        }
    }

    open fun visitPatch(openPatch: OpenPatch) {
    }
}