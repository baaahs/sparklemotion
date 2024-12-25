package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.show.live.OpenGridItem
import baaahs.ui.IObservable
import baaahs.ui.View

interface Viewable : IObservable {
    val id: String
    val classes: Set<String>
    val bounds: Rect?
    val layer: Int
    val parent: Viewable?
    val children: List<Viewable>
    val view: View
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
        if (center != null && bounds?.contains(center) == true) {
            val overCell = gridContainer?.findCell(center.x, center.y)
            println("Over cell: $overCell")
            val over = children.find { it.bounds?.contains(center) == true }
            if (over != null && over.parent != viewable.parent) {
                over.dragging(viewable, center)
            } else {
                println("dragging $viewable, within $id, over ${over?.id}")
//                resizeToMatch(viewable)
            }
        } else {
            println("dragging $viewable, outside $id")
            parent?.dragging(viewable, center)
        }
    }

    fun resizeToMatch(viewable: Viewable)
}

interface ViewableConstraints {
    val gap: Int
}
class Constraints(override val gap: Int) : ViewableConstraints

