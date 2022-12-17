package baaahs.show.live

import baaahs.ui.Draggable
import baaahs.ui.DropTarget
import baaahs.ui.gridlayout.Layout

class GridLayoutControlDisplay(override val show: OpenShow) : ControlDisplay {
    private val placedControls: Set<OpenControl>
    private val onScreenControls: Set<OpenControl>
    override val unplacedControls: Set<OpenControl>
    override val relevantUnplacedControls: List<OpenControl>

    override val unplacedControlsDropTarget: LegacyControlDisplay.UnplacedControlsDropTarget
        get() = TODO("not implemented")
    override val unplacedControlsDropTargetId: String
        get() = TODO("not implemented")

    init {
        val placedControls = mutableSetOf<OpenControl>()
        val onScreenControls = mutableSetOf<OpenControl>()
        val gridLayouts = mutableMapOf<OpenIGridLayout, DropTarget<GridPosition>>()

        fun OpenIGridLayout.visitItems(id: String, isOnScreen: Boolean) {
            val dropTarget = GridDropTarget(id, items)
            gridLayouts[this] = dropTarget

            items.forEach { gridItem ->
                placedControls.add(gridItem.control)
                if (isOnScreen)
                    onScreenControls.add(gridItem.control)

                gridItem.layout?.visitItems(id + "::" + gridItem.control.id, isOnScreen)
            }
        }

        val currentTab = show.openLayouts.currentFormat?.currentTab as? OpenGridTab
        show.openLayouts.currentFormat?.tabs?.forEach { tab ->
            if (tab is OpenGridTab) {
                val isOnScreen = tab === currentTab


                currentTab?.visitItems("_tab_", isOnScreen)
            }
        }
        this.placedControls = placedControls.toSet()
        this.onScreenControls = onScreenControls.toSet()

        val activePatchSet = show.buildActivePatchSet()
        val activeDataSources = activePatchSet.dataSources

        val offScreenControls = show.implicitControls.toSet() - onScreenControls
        this.relevantUnplacedControls = offScreenControls.filter { control ->
            activeDataSources.containsAll(control.controlledDataSources())
        }.sortedBy { control ->
            (control as? FeedOpenControl)?.inUse = true
            control.controlledDataSources().firstOrNull()?.title
                ?: "zzzzz"
        }
        println("relevantUnplacedControls = ${relevantUnplacedControls}")

        placedControls.forEach { control ->
            (control as? FeedOpenControl)?.inUse =
                activeDataSources.containsAll(control.controlledDataSources())
        }

        unplacedControls = show.allControls.toSet() - placedControls
    }

    override fun release() {
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