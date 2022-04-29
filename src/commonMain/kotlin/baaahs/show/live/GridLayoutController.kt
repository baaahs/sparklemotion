package baaahs.show.live

import baaahs.show.Show
import baaahs.show.ShowState
import baaahs.show.mutable.EditHandler
import baaahs.ui.DragNDrop
import baaahs.ui.Draggable
import baaahs.ui.DropTarget
import baaahs.ui.gridlayout.Layout

class GridLayoutController(
    internal val show: OpenShow,
    internal val editHandler: EditHandler<Show, ShowState>,
    internal val dragNDrop: DragNDrop<GridPosition>
) {
    val visibleControls: Set<OpenControl>
    val gridLayouts: Map<OpenIGridLayout, DropTarget<GridPosition>>

    init {
        val visibleControls = mutableSetOf<OpenControl>()
        val gridLayouts = mutableMapOf<OpenIGridLayout, DropTarget<GridPosition>>()

        fun OpenIGridLayout.visitItems(id: String) {
            val dropTarget = GridDropTarget(id, items)
            gridLayouts[this] = dropTarget
            dragNDrop.addDropTarget(dropTarget)

            items.forEach { gridItem ->
                visibleControls.add(gridItem.control)

                if (items.isNotEmpty())
                    visitItems(id + "::" + gridItem.control.id)
            }
        }

        val currentTab = show.openLayouts.currentFormat?.currentTab as? OpenGridTab
        currentTab?.visitItems("_tab_")
        this.visibleControls = visibleControls.toSet()
        this.gridLayouts = gridLayouts.toMap()
    }

    fun release() {
        gridLayouts.values.forEach {
            dragNDrop.removeDropTarget(it)
        }
    }
}

class GridDropTarget(
    override val dropTargetId: String,
    private val items: List<OpenGridItem>
) : DropTarget<GridPosition> {
    private val layout = Layout()

    override val type: String
        get() = "Grid"

    override fun removeDraggable(draggable: Draggable<GridPosition>) {
        TODO("not implemented")
    }

    override fun insertDraggable(draggable: Draggable<GridPosition>, position: GridPosition) {
        TODO("not implemented")
    }

    override fun getDraggable(position: GridPosition): Draggable<GridPosition> {
        TODO("not implemented")
    }

    override fun willAccept(draggable: Draggable<GridPosition>): Boolean {
        TODO("not implemented")
    }

    override fun moveDraggable(fromPosition: GridPosition, toPosition: GridPosition) {
        TODO("not implemented")
    }

}

data class GridPosition(
    val column: Int,
    val row: Int,
    val width: Int = 1,
    val height: Int = 1
)