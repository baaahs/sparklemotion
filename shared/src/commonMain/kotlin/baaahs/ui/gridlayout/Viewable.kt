package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.show.GridItem
import baaahs.show.IGridLayout
import baaahs.show.live.OpenGridItem
import baaahs.ui.IObservable

interface Viewable : IObservable {
    val viewRoot: ViewRoot
    val id: String
    val serial: Int
    val classes: Set<String>
    val bounds: Rect?
    val layer: Int
    val parent: Viewable?
    val children: List<Viewable>
    val isDragging: Boolean get() = false
    val isContainer: Boolean
    var gridContainer: GridContainer?

    fun find(id: String): Viewable? =
        if (this.id == id) this else children.firstNotNullOfOrNull { it.find(id) }

    fun visit(callback: (Viewable) -> Unit) {
        callback(this)
        children.forEach { it.visit(callback) }
    }

    fun layout(bounds: Rect)
    fun draggedBy(point: Vector2I?)

    fun dragging(viewable: Viewable, center: Vector2I?, nestLevel: Int = 0) {
        if (nestLevel > 10) {
            error("dragging(): stack overflow imminent")
        }
        println("${this.id}($serial): dragging ${viewable.id}(${viewable.serial}) over $bounds.contains($center)? ${bounds?.contains(center ?: Vector2I(0,0))}")
        if (center == null) {
            // No longer dragging.
        } else if (bounds?.contains(center) == true) {
            // Dragging item over this Viewable.
            val overCell = gridContainer?.findCell(center.x, center.y)?.cell
            if (overCell == null) return

//            val overItem = findChildAt(overCell)
//            println("Over cell: $overCell; over item: $overItem")

            val overChild = findChildAt(overCell)
            if (overChild != null && overChild != viewable && overChild.isContainer) {
                overChild.dragging(viewable, center, nestLevel + 1)
            } else {
                viewRoot.moveElement(viewable.id, this.id, overCell)
            }
        } else {
            println("${this.id}($serial): dragging ${viewable.id}, not in bounds ($bounds), calling parent[${parent?.id}].dragging()")
            parent?.dragging(viewable, center, nestLevel + 1)
        }
    }

    fun findChildAt(cell: Vector2I): OpenGridItem.GridItemViewable? = null
}

interface ViewableConstraints {
    val gap: Int
}
class Constraints(override val gap: Int) : ViewableConstraints

