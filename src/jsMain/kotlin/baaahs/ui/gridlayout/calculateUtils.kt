package baaahs.ui.gridlayout

import kotlinx.js.jso
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/*:: export type PositionParams = {
  margin: [number, number],
  containerPadding: [number, number],
  containerWidth: number,
  cols: number,
  rowHeight: number,
  maxRows: number
};*/
// Helper for generating column width
fun calcGridColWidth(positionParams: PositionParams): Double {
    val margin = positionParams.margin
    val containerPadding = positionParams.containerPadding
    val containerWidth = positionParams.containerWidth
    val cols = positionParams.cols
    return (containerWidth - margin[0] * (cols - 1) - containerPadding[0] * 2).toDouble() / cols
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

/**
 * Return position on the page given an x, y, w, h.
 * left, top, width, height are all in pixels.
 * @param  {PositionParams} positionParams  Parameters of grid needed for coordinates calculations.
 * @param  {Number}  x                      X coordinate in grid units.
 * @param  {Number}  y                      Y coordinate in grid units.
 * @param  {Number}  w                      W coordinate in grid units.
 * @param  {Number}  h                      H coordinate in grid units.
 * @return {Position}                       Object containing coords.
 */
fun calcGridItemPosition(
    positionParams: PositionParams,
    x: Int, y: Int, w: Int, h: Int,
    state: GridItemState? = null
): Position {
    val margin = positionParams.margin
    val containerPadding = positionParams.containerPadding
    val rowHeight = positionParams.rowHeight
    val colWidth = calcGridColWidth(positionParams)
    val out = jso<Position>()

    // If resizing, use the exact width and height as returned from resizing callbacks.
    val resizing = state?.resizing
    if (resizing != null) {
        out.width = resizing.width
        out.height = resizing.height
    } else { // Otherwise, calculate from grid units.
        out.width = calcGridItemWHPx(w, colWidth, margin[0].toDouble()).roundToInt()
        out.height = calcGridItemWHPx(h, rowHeight, margin[1].toDouble()).roundToInt()
    }

    // If dragging, use the exact width and height as returned from dragging callbacks.
    val dragging = state?.dragging
    if (dragging != null) {
        out.top = dragging.top
        out.left = dragging.left
    } else { // Otherwise, calculate from grid units.
        out.top = ((rowHeight + margin[1]) * y + containerPadding[1]).roundToInt()
        out.left = ((colWidth + margin[0]) * x + containerPadding[0]).roundToInt()
    }

    return out
}
/**
 * Translate x and y coordinates from pixels to grid units.
 * @param  {PositionParams} positionParams  Parameters of grid needed for coordinates calculations.
 * @param  {Number} top                     Top position (relative to parent) in pixels.
 * @param  {Number} left                    Left position (relative to parent) in pixels.
 * @param  {Number} w                       W coordinate in grid units.
 * @param  {Number} h                       H coordinate in grid units.
 * @return {Object}                         x and y in grid units.
 */
fun calcGridPosition(
    positionParams: PositionParams,
    top: Int, left: Int, w: Int, h: Int
): LayoutItemPosition {
    val margin = positionParams.margin
    val cols = positionParams.cols
    val rowHeight = positionParams.rowHeight
    val maxRows = positionParams.maxRows
    val colWidth = calcGridColWidth(positionParams); // left = colWidth * x + margin * (x + 1)
    // l = cx + m(x+1)
    // l = cx + mx + m
    // l - m = cx + mx
    // l - m = x(c + m)
    // (l - m) / (c + m) = x
    // x = (left - margin) / (coldWidth + margin)

    var x = ((left - margin[0]) / (colWidth + margin[0])).roundToInt()
    var y = ((top - margin[1]) / (rowHeight + margin[1])).roundToInt() // Capping

    x = clamp(x, 0, cols - w)
    y = clamp(y, 0, maxRows - h)
    return jso {
            this.x = x
            this.y = y
    }
}

/**
 * Given a height and width in pixel values, calculate grid units.
 * @param  {PositionParams} positionParams  Parameters of grid needed for coordinates calcluations.
 * @param  {Number} height                  Height in pixels.
 * @param  {Number} width                   Width in pixels.
 * @param  {Number} x                       X coordinate in grid units.
 * @param  {Number} y                       Y coordinate in grid units.
 * @return {Object}                         w, h as grid units.
 */
fun calcWH(
    positionParams: PositionParams,
    width: Int, height: Int,
    x: Int, y: Int
): LayoutItemSize
{
    val margin = positionParams.margin
    val maxRows = positionParams.maxRows
    val cols = positionParams.cols
    val rowHeight = positionParams.rowHeight
    val colWidth = calcGridColWidth(positionParams) // width = colWidth * w - (margin * (w - 1))
    // ...
    // w = (width + margin) / (colWidth + margin)

    var w = ((width + margin[0]) / (colWidth + margin[0])).roundToInt()
    var h = ((height + margin[1]) / (rowHeight + margin[1])).roundToInt() // Capping

    w = clamp(w, 0, cols - x)
    h = clamp(h, 0, maxRows - y)
    return jso {
            this.w = w
            this.h = h
    }
}

// Similar to _.clamp

fun clamp(num: Double, lowerBound: Double, upperBound: Double): Double =
    max(min(num, upperBound), lowerBound)

fun clamp(num: Int, lowerBound: Int, upperBound: Int): Int =
    max(min(num, upperBound), lowerBound)