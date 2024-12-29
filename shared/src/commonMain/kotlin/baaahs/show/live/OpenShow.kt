package baaahs.show.live

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.Editor
import baaahs.app.ui.editor.GridLayoutEditorPanel
import baaahs.client.document.OpenDocument
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.geom.Vector2I
import baaahs.getBang
import baaahs.randomId
import baaahs.show.*
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableILayout
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.sm.webapi.Problem
import baaahs.sm.webapi.Severity
import baaahs.ui.Observable
import baaahs.ui.View
import baaahs.ui.addObserver
import baaahs.ui.gridlayout.Rect
import baaahs.ui.gridlayout.GridContainer
import baaahs.ui.gridlayout.GridCoords
import baaahs.ui.gridlayout.ViewRoot
import baaahs.ui.gridlayout.Viewable
import baaahs.util.Logger
import baaahs.util.RefCounted
import baaahs.util.RefCounter
import kotlinx.serialization.json.JsonElement

interface OpenContext : GadgetProvider {
    val allControls: List<OpenControl>
    val allPatchModFeeds: List<Feed>

    fun findControl(id: String): OpenControl?
    fun getControl(id: String): OpenControl
    fun getFeed(id: String): Feed
    fun getPanel(id: String): Panel
    fun getPatch(it: String): OpenPatch
    fun release()
}

object EmptyOpenContext : OpenContext {
    override val allControls: List<OpenControl> get() = emptyList()

    override val allPatchModFeeds: List<Feed> get() = emptyList()

    override fun findControl(id: String): OpenControl? = null

    override fun getControl(id: String): OpenControl = FakeOpenControl(id)

    override fun getFeed(id: String): Feed = error("not really an open context")

    override fun getPanel(id: String): Panel = error("not really an open context")

    override fun getPatch(it: String): OpenPatch = error("not really an open context")

    override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed?) =
        error("not really an open context")

    override fun release() {}
}

class FakeOpenControl(override val id: String) : OpenControl {
    override fun getState(): Map<String, JsonElement>? = TODO("not implemented")
    override fun applyState(state: Map<String, JsonElement>) = TODO("not implemented")
    override fun toNewMutable(mutableShow: MutableShow): MutableControl = TODO("not implemented")
    override fun getView(controlProps: ControlProps): View = TODO("not implemented")

}

class OpenShow(
    internal val show: Show,
    private val showPlayer: ShowPlayer,
    private val openContext: OpenContext,
    internal val implicitControls: List<OpenControl>,
    upstreamActivePatchSetMonitor: Observable
) : OpenPatchHolder(show, openContext), RefCounted by RefCounter(), OpenDocument<Show> {
    val id = randomId("show")
    val layouts get() = show.layouts
    val openLayouts = show.layouts.open(openContext)
    val allFeeds = run {
        ShowBuilder().apply {
            val map = show.feeds.values.associateBy { idFor(it) }.toMutableMap()
            openContext.allPatchModFeeds.forEach { feed ->
                map[idFor(feed)] = feed
            }
        }.getFeeds()
    }
    val allControls: List<OpenControl> get() = openContext.allControls
    val feeds = allFeeds.entries.associate { (id, feed) ->
        val feedContext = showPlayer.openFeed(id, feed)
        feedContext.use()
        feed to feedContext
    }

    val missingPlugins: Map<PluginDesc, List<String>>
        get() = run {
            val unknownFeeds = allFeeds.values
                .filterIsInstance<UnknownFeed>()
            unknownFeeds
                .asSequence()
                .map { ds -> ds.pluginRef }
                .groupBy { it.pluginId }
                .map { (pluginId, pluginRefs) ->
                    PluginDesc(pluginId, pluginId, null, null, null) to
                            pluginRefs.map { it.resourceName }.sorted()
                }
                .sortedBy { (desc, _) -> desc.title }
                .associate { (desc, resources) -> desc to resources.distinct().sorted() }
        }

    val allProblems: List<Problem>
        get() = buildList {
            addAll(missingPlugins.map { (desc, resources) ->
                Problem(
                    "Missing plugin \"${desc.title}\".",
                    "Some things may not work properly. (${resources.joinToString()})",
                    severity = Severity.WARN
                )
            })

            object : OpenShowVisitor() {
                override fun visitPatchHolder(openPatchHolder: OpenPatchHolder) {
                    addAll(openPatchHolder.problems)
                    super.visitPatchHolder(openPatchHolder)
                }
            }.apply { visitShow(this@OpenShow) }
        }

    val activePatchSetMonitor = Observable()
    init {
        upstreamActivePatchSetMonitor.addObserver {
            invalidateSnapshotCache()
            activePatchSetMonitor.notifyChanged()
        }
    }
    fun getPanel(id: String) = show.layouts.panels.getBang(id, "panel")

    fun findGridItem(
        layoutId: String,
        tabTitle: String,
        vararg controlTitles: String
    ): GridItem {
        var layout = layouts
            .findLayout(layoutId)
            .findTab(tabTitle) as IGridLayout?
        var item: GridItem? = null
        for (title in controlTitles) {
            if (layout == null) error("layout not found")
            item = layout.items.find { openContext.getControl(it.controlId).gadget?.title == title }
                ?: error("No item \"$title\".")
            layout = item.layout
        }
        return item!!
    }

    /**
     * Don't hold on to MutableShows; create them, apply changes, and commit or abandon them promptly!
     */
    fun edit(block: MutableShow.() -> Unit = {}): MutableShow =
        MutableShow(show).apply(block)

    private var snapshotCache: ShowStateSnapshot? = null

    fun getSnapshot(): ShowStateSnapshot =
        snapshotCache ?: buildSnapshot().also { snapshotCache = it }

    fun invalidateSnapshotCache() {
        println("Snapshot cache invalidated.")
        snapshotCache?.controlsInfo?.release()
        snapshotCache = null
    }

    private fun buildSnapshot(): ShowStateSnapshot {
        println("Snapshot built.")
        val activePatchSet = buildActivePatchSet()
        return ShowStateSnapshot(activePatchSet, GridLayoutControlsInfo(this, activePatchSet))
    }

    fun buildActivePatchSet(): ActivePatchSet {
        val items = arrayListOf<ActivePatchSet.Companion.Item>()
        var nextSerial = 0

        val builder = object : ActivePatchSet.Builder {
            override val show: OpenShow
                get() = this@OpenShow

            override fun add(patchHolder: OpenPatchHolder, depth: Int, layoutContainerId: String) {
                items.add(ActivePatchSet.Companion.Item(patchHolder, depth, layoutContainerId, nextSerial++))
            }
        }
        addTo(builder, 0)

        return ActivePatchSet(
            ActivePatchSet.sort(items),
            allFeeds,
            feeds
        )
    }

    override fun onRelease() {
        openContext.release()
        feeds.values.forEach { it.disuse() }
    }

    fun applyConstraints() {
        allControls.forEach { it.applyConstraints() }
    }

    override fun addTo(builder: ActivePatchSet.Builder, depth: Int) {
        super.addTo(builder, depth)

        openLayouts.currentFormat?.addTo(builder, depth + 1)
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

    fun visitTabs(callback: (layout: OpenIGridLayout, isOnScreen: Boolean) -> Unit) {
        val currentTab = openLayouts.currentFormat?.currentTab as? OpenGridTab
        openLayouts.currentFormat?.tabs?.forEach { tab ->
            if (tab is OpenGridTab) {
                val isOnScreen = tab === currentTab
                callback(tab, isOnScreen)
            }
        }
    }

    companion object {
        private val logger = Logger("OpenShow")
    }
}

class ShowStateSnapshot(
    val activePatchSet: ActivePatchSet,
    val controlsInfo: ControlsInfo
)

data class OpenLayouts(
    val panels: Map<String, Panel>,
    val formats: Map<String, OpenLayout>
) {
    val currentFormatId = formats.keys.firstOrNull()
    val currentFormat = formats[currentFormatId]
}

class OpenLayout(
    val mediaQuery: String?,
    val tabs: List<OpenTab>
): Observable() {
    var currentTabIndex = if (tabs.isEmpty()) null else 0
        set(value) {
            field = value
            notifyChanged()
        }

    val currentTab get() = currentTabIndex?.let { tabs[it] }

    fun addTo(builder: ActivePatchSet.Builder, depth: Int) {
        currentTab?.addTo(builder, depth + 1)
    }
}

interface OpenTab {
    val title: String

    fun addTo(builder: ActivePatchSet.Builder, depth: Int)
}

class OpenGridTab(
    override val gridLayout: GridTab,
    override val title: String,
    override var columns: Int,
    override var rows: Int,
    override val items: List<OpenGridItem>
) : OpenTab, OpenIGridLayout {
    override fun addTo(builder: ActivePatchSet.Builder, depth: Int) {
        items.forEach { item ->
            item.control.addTo(builder, depth + 1, item.layout)
        }
    }
}

class OpenGridLayout(
    override val gridLayout: GridLayout,
    override val columns: Int,
    override val rows: Int,
    val matchParent: Boolean,
    override val items: List<OpenGridItem>
) : OpenIGridLayout

interface OpenILayout {
    fun getEditorPanel(
        editableManager: EditableManager<*>,
        layoutEditor: Editor<MutableILayout>
    ): DialogPanel?
}

interface OpenIGridLayout : OpenILayout {
    val gridLayout: IGridLayout
    val columns: Int
    val rows: Int
    val items: List<OpenGridItem>
    val gridDimens get() = GridDimens(columns, rows)

    override fun getEditorPanel(editableManager: EditableManager<*>, layoutEditor: Editor<MutableILayout>) =
        GridLayoutEditorPanel(editableManager, layoutEditor as Editor<MutableIGridLayout>)

    fun createViewable(viewRoot: ViewRoot): Viewable =
        object : Observable(), Viewable {
            override val viewRoot: ViewRoot = viewRoot
            override val id: String
                get() = "##VIEWROOT##"
            override val classes: Set<String> = setOf("open-grid-layout")
            override var bounds: Rect? = null
                private set
            override val layer: Int = 0
            override val parent: Viewable?
                get() = null
            override val children: List<OpenGridItem.GridItemViewable> =
                items.map { it.createViewable(viewRoot, this) }
            override var gridContainer: GridContainer? = null

            override fun layout(bounds: Rect) {
                if (this.bounds == bounds) return
                this.bounds = bounds

//                println("$id bounds = ${bounds}")
                gridContainer = GridContainer(columns, rows, bounds.inset(viewRoot.margins), viewRoot.gap).apply {
                    children.forEach {
                        it.layout(calculateRegionBounds(it.gridRegion))
                    }
                }

                notifyChanged()
            }

            override fun draggedBy(point: Vector2I?) {
                // No-op.
            }

//            override fun dragging(
//                viewable: OpenGridItem.GridItemViewable,
//                center: Vector2I?
//            ) {
//                println("ViewRoot dragging ${viewable.id}... TBD")
////                super.dragging(viewable, center)
//            }

            override fun moveElement(item: GridItem, x: Int, y: Int): IGridLayout =
                this@OpenIGridLayout.gridLayout.moveElement(item, x, y)

            override fun removeElement(item: GridItem): IGridLayout =
                this@OpenIGridLayout.gridLayout.removeElement(item.controlId)
        }
}

data class GridDimens(
    val columns: Int,
    val rows: Int
)

class OpenGridItem(
    val gridItem: GridItem,
    val control: OpenControl,
    val column: Int,
    val row: Int,
    val width: Int,
    val height: Int,
    val layout: OpenGridLayout?
) {
    val gridCells: List<Vector2I> =
        (row until row + height - 1).flatMap { y ->
            (column until column + width - 1).map { x -> Vector2I(x, y) }
        }

    fun createViewable(viewRoot: ViewRoot, parent: Viewable): GridItemViewable =
        GridItemViewable(viewRoot, parent)

    inner class GridItemViewable(
        override val viewRoot: ViewRoot,
        override val parent: Viewable
    ) : Observable(), Viewable {
        private var layoutBounds: Rect? = null
        private var draggedBy: Vector2I? = null

        override val id: String get() = control.id
        val gridItem: GridItem get() = this@OpenGridItem.gridItem
        override val classes: Set<String> get() = emptySet()
        override val bounds: Rect? get() =
            layoutBounds?.let { bounds ->
                draggedBy?.let { drag -> Rect(bounds.left + drag.x, bounds.top + drag.y, bounds.width, bounds.height) }
                    ?: bounds
            }
        override val layer: Int get() = parent.layer + 1
        private val childViewables: Map<OpenGridItem, GridItemViewable> =
            if (layout != null && control is ControlContainer) {
                layout.items.associateWith { gridItem ->
                    gridItem.createViewable(viewRoot, this)
                }
            } else emptyMap()
        override val children: List<GridItemViewable> =
            childViewables.values.toList()
        val childrenByCell: Map<Vector2I, GridItemViewable> = buildMap {
            childViewables.forEach { (item, viewable) ->
                item.gridCells.forEach { cell -> put(cell, viewable) }
            }
        }
        override val isDragging: Boolean
            get() = draggedBy != null
        override var gridContainer: GridContainer? = null
        val gridRegion get() = GridCoords(column, row, width, height)
        val gridTopLeft get() = Vector2I(column, row)

        private fun calculateGridContainer(): GridContainer? {
            val bounds = layoutBounds
            val layout = layout
            if (bounds == null || layout == null) return null
            // TODO: we should calculate inner bounds properly somehow...
            val topInset = if (control is OpenButtonGroupControl && control.showTitle) {
                20
            } else 0
            val innerBounds = Rect(bounds.left, bounds.top + topInset, bounds.width, bounds.height - topInset)
            val insetBounds = innerBounds.inset(viewRoot.margins)
//            println("$id insetBounds = $insetBounds")
            return GridContainer(layout.columns, layout.rows, insetBounds, viewRoot.gap)
        }

        override fun layout(bounds: Rect) {
//            println("$id: layout($bounds)")
            if (this.bounds == bounds) return
            layoutBounds = bounds
            gridContainer = calculateGridContainer()

            if (layout != null) {
                calculateGridContainer()?.apply {
                    children.forEach {
                        it.layout(calculateRegionBounds(it.gridRegion))
                    }
                }
            }
            notifyChanged()
        }

        protected fun draggedByInternal(point: Vector2I?) {
            draggedBy = point
            children.forEach {
                it.draggedByInternal(point)
            }
            notifyChanged()
        }

        override fun draggedBy(point: Vector2I?) {
            if (draggedBy == point) return

            draggedByInternal(point)
            parent.dragging(this, bounds?.center)
            notifyChanged()
        }

        override fun moveElement(item: GridItem, x: Int, y: Int): IGridLayout =
            gridItem.layout!!.moveElement(item, x, y)

        override fun removeElement(item: GridItem): IGridLayout =
            gridItem.layout!!.removeElement(item.controlId)

        override fun findChildAt(cell: Vector2I): GridItemViewable? =
            childrenByCell[cell]

        fun resizeToMatch(viewable: Viewable) {
            viewable.bounds?.let { bounds ->
                val destGridSize = gridContainer?.calculateRegionBounds(0, 0, 1, 1)
                    ?: return
                val newBounds = bounds.resizeFromCenter(destGridSize)
                println("Resizing ${viewable.id} from ${viewable.bounds} to $newBounds")
                viewable.layout(newBounds)
            }
        }
    }
}