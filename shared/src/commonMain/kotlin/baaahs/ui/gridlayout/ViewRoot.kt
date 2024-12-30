package baaahs.ui.gridlayout

import baaahs.Gadget
import baaahs.app.settings.Provider
import baaahs.app.ui.editor.Editor
import baaahs.geom.Vector2I
import baaahs.show.Feed
import baaahs.show.GridItem
import baaahs.show.IGridLayout
import baaahs.show.Panel
import baaahs.show.live.ControlContainer
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenGridItem
import baaahs.show.live.OpenGridItem.GridItemViewable
import baaahs.show.live.OpenGridTab
import baaahs.show.live.OpenPatch
import baaahs.show.live.OpenShow
import baaahs.show.mutable.MutableIGridLayout
import baaahs.ui.Observable
import kotlin.collections.component1
import kotlin.collections.component2

class ViewRoot(
    private var gridTab: OpenGridTab,
    gap: Int = 0,
    margins: Int = 0,
    private val openShow: Provider<OpenShow>,
    val editor: Editor<MutableIGridLayout>?,
    private val onLayoutChange: (IGridLayout, Boolean) -> Unit = { _, _ -> }
) : Observable() {
    internal var rootViewable = createViewable(this)

    val classes: Set<String> = emptySet()
    var bounds: Rect? = null
    val children: List<Viewable> = listOf(rootViewable)

    var gap: Int = gap
        set(value) { field = value; notifyChanged() }
    var margins: Int = margins
        set(value) { field = value; notifyChanged() }

    fun findViewable(id: String): Viewable =
        findOrNull(id) ?: error("No viewable found with id $id.")

    fun findOrNull(id: String): Viewable? =
        rootViewable.find(id)

    fun visit(callback: (Viewable) -> Unit) {
        rootViewable.visit(callback)
    }

    fun layout(bounds: Rect) {
        this.bounds = bounds
        rootViewable.layout(bounds)
        notifyChanged()
    }

//    fun editLayout(newLayout: GridLayout) {
//        onLayoutChange(newLayout, false)
//    }
    //        // Move the element to the dragged location.
//                        layout!!.moveElement
//        val newLayout = try {
//            oldLayout.moveElement(l, x, y)
//        } catch (e: ImpossibleLayoutException) {
//            setState {
//                this.notDroppableHere = true
//            }
//            return
//        }

    fun moveElement(movingId: String, toLayoutId: String, toPosition: Vector2I) {
        val movingItem = gridTab.gridTab.find(movingId)
            ?: error("No such item $movingId.")
        if (movingItem.column == toPosition.x && movingItem.row == toPosition.y)
            return
        val newGridTab = gridTab.moveElement(movingId, toLayoutId, toPosition)
        gridTab = newGridTab.open(ReopenContext())
        val movedItem = gridTab.gridTab.find(movingId)
        if (movedItem == null) {
            error("Moving item $movingId not found.")
        }
        println("Moved $movingId from ${movingItem.column},${movingItem.row} to ${movedItem.column},${movedItem.row}")
        rootViewable = createViewable(this)
        rootViewable.layout(bounds!!)
        println("moveElement -> layout() finished")
        notifyChanged()
//            .also { onLayoutChange(it, true) }
    }

    fun createViewable(viewRoot: ViewRoot): RootViewable = RootViewable(viewRoot)

    inner class RootViewable(viewRoot: ViewRoot) : Observable(), Viewable {
        override val viewRoot: ViewRoot = viewRoot
        override val id: String
            get() = "##VIEWROOT##"
        override val serial: Int
            get() = -1
        override val classes: Set<String> = setOf("open-grid-layout")
        override var bounds: Rect? = null
            private set
        override val layer: Int = 0
        override val parent: Viewable?
            get() = null
        private val childViewables: Map<OpenGridItem, GridItemViewable> =
            gridTab.items.associateWith { gridItem ->
                gridItem.createViewable(viewRoot, this)
            }
        override val children: List<GridItemViewable> =
            gridTab.items.map { it.createViewable(viewRoot, this) }
        val childrenByCell: Map<Vector2I, GridItemViewable> = buildMap {
            childViewables.forEach { (item, viewable) ->
                println("childViewables: ${item.gridItem.id} -> ${item.gridCells}")
                item.gridCells.forEach { cell -> put(cell, viewable) }
            }
        }
        override val isContainer: Boolean
            get() = true
        override var gridContainer: GridContainer? = null

        override fun layout(bounds: Rect) {
            println("$id: bounds? = $bounds")
            if (this.bounds == bounds) return
            this.bounds = bounds

            println("$id: bounds = $bounds")
            gridContainer = GridContainer(
                gridTab.columns, gridTab.rows, bounds.inset(viewRoot.margins), viewRoot.gap
            ).apply {
                children.forEach {
                    it.layout(calculateRegionBounds(it.gridRegion))
                }
            }
            notifyChanged()
        }

        override fun draggedBy(point: Vector2I?) {
            // No-op.
        }

        override fun findChildAt(cell: Vector2I): OpenGridItem.GridItemViewable? =
            childrenByCell[cell]
    }

    inner class ReopenContext : OpenContext {
        val controls = openShow.get().allControls.associateBy { it.id }
        override val allControls: List<OpenControl>
            get() = TODO("not implemented")
        override val allPatchModFeeds: List<Feed>
            get() = TODO("not implemented")

        override fun findControl(id: String): OpenControl? = controls[id]
        override fun getControl(id: String): OpenControl =
            findControl(id) ?: error("No control found with id $id.")

        override fun getFeed(id: String): Feed = TODO("not implemented")
        override fun getPanel(id: String): Panel = TODO("not implemented")
        override fun getPatch(it: String): OpenPatch = TODO("not implemented")
        override fun release() = TODO("not implemented")

        override fun <T : Gadget> registerGadget(id: String, gadget: T, controlledFeed: Feed?) =
            TODO("not implemented")
    }
}