package baaahs.ui.gridlayout

import baaahs.geom.Vector2D
import external.react_resizable.Size
import org.w3c.dom.HTMLElement
import org.w3c.dom.events.MouseEvent
import react.ReactElement

class DroppingPosition(
    val left: Int,
    val top: Int,
    val e: MouseEvent
)

external interface GridDragEvent {
    var e: MouseEvent
    var node: HTMLElement
    var newPosition: Vector2D
}

external interface GridResizeEvent {
    var e: MouseEvent
    var node: HTMLElement
    var size: Size
}

typealias ReactChildren = Array<ReactElement<*>>


fun HTMLElement.getPosition(): Vector2D =
    getBoundingClientRect().let {
        Vector2D(it.left, it.top)
    }

fun HTMLElement.getPositionMinusScroll(): Vector2D =
    getPosition() - Vector2D(scrollLeft, scrollTop)