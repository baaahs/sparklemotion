package baaahs.ui.gridlayout

import baaahs.geom.Vector2D
import baaahs.getBang
import external.react_resizable.ResizeHandleAxis
import external.react_resizable.Size
import external.react_resizable.position
import js.objects.Object
import js.objects.jso
import org.w3c.dom.events.MouseEvent
import react.ReactElement
import web.html.HTMLElement
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
    var handle: ResizeHandleAxis
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

/**
 * Helper functions to constrain dimensions of a GridItem
 */
private fun constrainWidth(left: Int, currentWidth: Int, newWidth: Int, containerWidth: Int): Int =
    if (left + newWidth > containerWidth) currentWidth else newWidth

private fun constrainHeight(top: Int, currentHeight: Int, newHeight: Int): Int =
    if (top < 0) currentHeight else newHeight

private fun constrainLeft(left: Int): Int =
    max(0, left)

private fun constrainTop(top: Int): Int =
    max(0, top)

private fun Position.resizeNorth(currentSize: Position, _containerWidth: Int): Position {
    val top = currentSize.top - (height - currentSize.height);

    return position(
        left, constrainTop(top),
        width, constrainHeight(top, currentSize.height, height)
    )
}

private fun Position.resizeEast(currentSize: Position, containerWidth: Int): Position =
    position(
        constrainLeft(left), top,
        constrainWidth(currentSize.left, currentSize.width, width, containerWidth), height
    )

private fun Position.resizeWest(currentSize: Position, containerWidth: Int): Position {
    val left = currentSize.left - (width - currentSize.width)

    return position(
        constrainLeft(left),
        constrainTop(top),
        if (left < 0) currentSize.width else constrainWidth(currentSize.left, currentSize.width, width, containerWidth),
        height
    )
}

private fun Position.resizeSouth(currentSize: Position, containerWidth: Int): Position =
    position(
        left, constrainTop(top),
        width, constrainHeight(top, currentSize.height, height)
    )

private fun Position.resizeNorthEast(currentSize: Position, containerWidth: Int) =
    resizeNorth(resizeEast(currentSize, containerWidth), containerWidth)

private fun Position.resizeNorthWest(currentSize: Position, containerWidth: Int) =
    resizeNorth(resizeWest(currentSize, containerWidth), containerWidth)

private fun Position.resizeSouthEast(currentSize: Position, containerWidth: Int) =
    resizeSouth(resizeEast(currentSize, containerWidth), containerWidth)

private fun Position.resizeSouthWest(currentSize: Position, containerWidth: Int) =
    resizeSouth(resizeWest(currentSize, containerWidth), containerWidth)


fun resizeItemInDirection(
    direction: ResizeHandleAxis,
    currentSize: Position,
    newSize: Position,
    containerWidth: Int
): Position {
    val ordinalResizeHandlerMap = mapOf<String, Position.(currentSize: Position, containerWidth: Int) -> Position> (
        "n" to Position::resizeNorth,
        "ne" to Position::resizeNorthEast,
        "e" to Position::resizeEast,
        "se" to Position::resizeSouthEast,
        "s" to Position::resizeSouth,
        "sw" to Position::resizeSouthWest,
        "w" to Position::resizeWest,
        "nw" to Position::resizeNorthWest
    )

    val ordinalHandler = ordinalResizeHandlerMap.getBang(direction, "direction")
    return ordinalHandler(Object.assign(jso(), currentSize, newSize), currentSize, containerWidth)
}