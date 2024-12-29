package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.show.GridItem
import baaahs.show.IGridLayout
import baaahs.show.live.OpenGridItem
import baaahs.ui.IObservable

interface Viewable : IObservable {
    val viewRoot: ViewRoot
    val id: String
    val classes: Set<String>
    val bounds: Rect?
    val layer: Int
    val parent: Viewable?
    val children: List<Viewable>
    val isDragging: Boolean get() = false
    var gridContainer: GridContainer?

    fun find(id: String): Viewable? =
        if (this.id == id) this else children.firstNotNullOfOrNull { it.find(id) }

    fun visit(callback: (Viewable) -> Unit) {
        callback(this)
        children.forEach { it.visit(callback) }
    }

    fun layout(bounds: Rect)
    fun draggedBy(point: Vector2I?)

    fun dragging(viewable: OpenGridItem.GridItemViewable, center: Vector2I?) {
        if (center == null) {
            // No longer dragging.
        } else if (bounds?.contains(center) == true) {
            // Dragging item over this Viewable.
            val overCell = gridContainer?.findCell(center.x, center.y)?.cell
            if (overCell == null) return

            val overItem = findChildAt(overCell)
//            println("Over cell: $overCell; over item: $overItem")

            viewRoot.moveElement(viewable.gridItem.controlId, this.id, overCell)
        } else {
//            println("dragging $viewable, outside $id")
            parent?.dragging(viewable, center)
        }

    }

    fun findChildAt(cell: Vector2I): OpenGridItem.GridItemViewable? = null
    fun moveElement(item: GridItem, x: Int, y: Int): IGridLayout
    fun removeElement(item: GridItem): IGridLayout
}

interface ViewableConstraints {
    val gap: Int
}
class Constraints(override val gap: Int) : ViewableConstraints

