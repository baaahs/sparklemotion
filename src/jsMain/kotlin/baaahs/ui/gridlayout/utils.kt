package baaahs.ui.gridlayout

import baaahs.geom.Vector2D
import dom.html.HTMLElement
import external.react_resizable.Size
import org.w3c.dom.events.MouseEvent
import react.ReactElement
import kotlin.math.max

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


/**
 * See `fastRGLPropsEqual.js`.
 * We want this to run as fast as possible - it is called often - and to be
 * resilient to new props that we add. So rather than call lodash.isEqual,
 * which isn't suited to comparing props very well, we use this specialized
 * function in conjunction with preval to generate the fastest possible comparison
 * function, tuned for exactly our props.
 */
//typealias FastRGLPropsEqual = (Any, Any, Function<*>) -> Boolean
//val fastRGLPropsEqual: FastRGLPropsEqual = require("./fastRGLPropsEqual")

// Like the above, but a lot simpler.
fun fastPositionEqual(a: Position, b: Position): Boolean {
    return (
            a.left == b.left &&
                    a.top == b.top &&
                    a.width == b.width &&
                    a.height == b.height
            )
}


// This can either be called:
// calcGridItemWHPx(w, colWidth, margin[0])
// or
// calcGridItemWHPx(h, rowHeight, margin[1])
fun calcGridItemWHPx(gridUnits: Int, colOrRowSize: Double, marginPx: Double): Double {
    // 0 * Infinity === NaN, which causes problems with resize contraints
    if (gridUnits == Int.MAX_VALUE) return gridUnits.toDouble()
    return colOrRowSize * gridUnits + max(0, gridUnits - 1) * marginPx
}

fun HTMLElement.getPosition(): Vector2D =
    getBoundingClientRect().let {
        Vector2D(it.left, it.top)
    }

fun HTMLElement.getPositionMinusScroll(): Vector2D =
    getPosition() - Vector2D(scrollLeft, scrollTop)