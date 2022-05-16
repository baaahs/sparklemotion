package baaahs.ui.gridlayout

import baaahs.clamp
import baaahs.geom.Vector2I
import baaahs.x
import baaahs.y
import kotlin.math.max
import kotlin.math.roundToInt

data class PositionParams(
    val margin: Pair<Int, Int>,
    val containerPadding: Pair<Int, Int>,
    val containerWidth: Int,
    val cols: Int,
    val rowHeight: Double,
    val maxRows: Int
) {
    private val colWidth = (containerWidth - margin.first * (cols - 1) - containerPadding.first * 2).toDouble() / cols

    /**
     * Given a height and width in pixel values, calculate grid units.
     * @param  {Number} height                  Height in pixels.
     * @param  {Number} width                   Width in pixels.
     * @param  {Number} xGridUnits              X coordinate in grid units.
     * @param  {Number} yGridUnits              Y coordinate in grid units.
     * @return {Object}                         w, h as grid units.
     */
    fun calcWidthAndHeightInGridUnits(widthPx: Int, heightPx: Int, xGridUnits: Int, yGridUnits: Int): LayoutItemSize {
        // width = colWidth * w - (margin * (w - 1))
        // ...
        // w = (width + margin) / (colWidth + margin)
        var w = ((widthPx + margin.x) / (colWidth + margin.x)).roundToInt()
        var h = ((heightPx + margin.y) / (rowHeight + margin.y)).roundToInt() // Capping

        w = w.clamp(0, cols - xGridUnits)
        h = h.clamp(0, maxRows - yGridUnits)
        return LayoutItemSize(w, h)
    }

    /**
     * Translate x and y coordinates from pixels to grid units.
     * @param  {Number} leftPx                  Left position (relative to parent) in pixels.
     * @param  {Number} topPx                   Top position (relative to parent) in pixels.
     * @param  {Number} widthGridUnits          W coordinate in grid units.
     * @param  {Number} heightGridUnits         H coordinate in grid units.
     * @return {Vector2I}                       x and y in grid units.
     */
    fun calcGridPosition(leftPx: Int, topPx: Int, widthGridUnits: Int, heightGridUnits: Int): Vector2I {
        // left = colWidth * x + margin * (x + 1)
        // l = cx + m(x+1)
        // l = cx + mx + m
        // l - m = cx + mx
        // l - m = x(c + m)
        // (l - m) / (c + m) = x
        // x = (left - margin) / (coldWidth + margin)
        var x = ((leftPx - margin.x) / (colWidth + margin.x)).roundToInt()
        var y = ((topPx - margin.y) / (rowHeight + margin.y)).roundToInt() // Capping

        x = x.clamp(0, cols - widthGridUnits)
        y = y.clamp(0, maxRows - heightGridUnits)
        return Vector2I(x, y)
    }

    /**
     * Return position on the page given an x, y, w, h.
     * left, top, width, height are all in pixels.
     * @param  {PositionParams} positionParams  Parameters of grid needed for coordinates calculations.
     * @param  {Number}  xGridUnits             X coordinate in grid units.
     * @param  {Number}  yGridUnits             Y coordinate in grid units.
     * @param  {Number}  widthGridUnits         W coordinate in grid units.
     * @param  {Number}  heightGridUnits        H coordinate in grid units.
     * @return {Position}                       Object containing coords.
     */
    fun calcGridItemPosition(
        xGridUnits: Int, yGridUnits: Int, widthGridUnits: Int, heightGridUnits: Int,
        state: GridItemStateCommon? = null
    ): Position {
        // If resizing, use the exact width and height as returned from resizing callbacks.
        val resizing = state?.resizing
        val width: Int
        val height: Int
        if (resizing != null) {
            width = resizing.x
            height = resizing.y
        } else { // Otherwise, calculate from grid units.
            width = calcGridItemWidthPx(widthGridUnits).roundToInt()
            height = calcGridItemHeightPx(heightGridUnits).roundToInt()
        }

        // If dragging, use the exact width and height as returned from dragging callbacks.
        val dragging = state?.dragging
        val top: Int
        val left: Int
        if (dragging != null) {
            top = dragging.y.roundToInt()
            left = dragging.x.roundToInt()
        } else { // Otherwise, calculate from grid units.
            top = ((rowHeight + margin.y) * yGridUnits + containerPadding.y).roundToInt()
            left = ((colWidth + margin.x) * xGridUnits + containerPadding.x).roundToInt()
        }

        return Position(left, top, width, height)
    }

    fun calcGridItemWidthPx(widthGridUnits: Int): Double =
        calcGridItemWHPx(widthGridUnits, colWidth, margin.x.toDouble())

    fun calcGridItemHeightPx(heightGridUnits: Int): Double =
        calcGridItemWHPx(heightGridUnits, rowHeight, margin.y.toDouble())

    // This can either be called:
    // calcGridItemWHPx(w, colWidth, margin[0])
    // or
    // calcGridItemWHPx(h, rowHeight, margin[1])
    private fun calcGridItemWHPx(gridUnits: Int, colOrRowSize: Double, marginPx: Double): Double {
        // 0 * Infinity === NaN, which causes problems with resize contraints
        if (gridUnits == Int.MAX_VALUE) return gridUnits.toDouble()
        return colOrRowSize * gridUnits + max(0, gridUnits - 1) * marginPx
    }
}