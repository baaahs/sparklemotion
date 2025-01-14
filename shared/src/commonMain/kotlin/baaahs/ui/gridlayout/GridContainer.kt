package baaahs.ui.gridlayout

import baaahs.clamp
import kotlin.math.roundToInt

data class CellBoundaries(
    val start: Int,
    val end: Int,
    val gapStart: Int,
    val gapEnd: Int
) {
    val size: Int get() = end - start
    val center: Int get() = (end - start) / 2 + start
}

data class GridContainer(
    val columns: Int,
    val rows: Int,
    val bounds: Rect,
    val gap: Int
) {
    val columnsBoundaries: Array<CellBoundaries>
    val rowBoundaries: Array<CellBoundaries>

    init {
//        println("bounds = ${bounds}")
        val widthSansGaps = bounds.width - (columns - 1) * gap
        val columnWidth = widthSansGaps / columns.toDouble()
        val heightSansGaps = bounds.height - (rows - 1) * gap
        val rowHeight = heightSansGaps / rows.toDouble()
        val halfGap = gap / 2.0

        columnsBoundaries = (0 until columns).map { column ->
            val start = bounds.left + column * columnWidth + column * gap
            val end = start + columnWidth
            CellBoundaries(
                start.roundToInt(),
                end.roundToInt(),
                (if (column == 0) start else start - halfGap).roundToInt(),
                (if (column == columns - 1) end else end + halfGap).roundToInt()
            )
        }.toTypedArray()

        rowBoundaries = (0 until rows).map { row ->
            val start = bounds.top + row * rowHeight + row * gap
            val end = start + rowHeight
            CellBoundaries(
                start.roundToInt(),
                end.roundToInt(),
                (if (row == 0) start else start - halfGap).roundToInt(),
                (if (row == rows - 1) end else end + halfGap).roundToInt()
            )

        }.toTypedArray()
    }

//    fun calculateRegionBounds(gridRegion: GridCoords): Rect =
//        calculateRegionBounds(gridRegion.left, gridRegion.top, gridRegion.width, gridRegion.height)

    fun calculateRegionBounds(column: Int, row: Int, width: Int, height: Int): Rect {
        if (column < 0 || column >= columns)
            error("Column $columns not in range [0, $columns].")
        val widthClamped = width.clamp(0, columns - column)
        val columnStart = columnsBoundaries[column]
        val columnEnd = columnsBoundaries[column + widthClamped - 1]

        if (row < 0 || row >= rows)
            error("Row $rows not in range [0, $rows].")
        val heightClamped = height.clamp(0, rows - row)
        val rowStart = rowBoundaries[row]
        val rowEnd = rowBoundaries[row + heightClamped - 1]
        return Rect(
            columnStart.start, rowStart.start,
            columnEnd.end - columnStart.start, rowEnd.end - rowStart.start
        )
    }

    fun findCell(x: Int, y: Int): GridPosition {
        val column = (0 until columns).indexOfFirst { column ->
            x < columnsBoundaries[column].end
        }.let { if (it == -1) columns - 1 else it }
        val row = (0 until rows).indexOfFirst { row ->
            y < rowBoundaries[row].end
        }.let { if (it == -1) rows - 1 else it }
//        if (column == -1 || row == -1) error("No cell found for pixel $x $y")
        val isLeft = x <= columnsBoundaries[column].center
        val isTop = y <= rowBoundaries[row].center
        val quadrant = Quadrant.from(isLeft, isTop)
        return GridPosition(column, row, quadrant)
    }

    fun originCellBounds() =
        Rect(
            columnsBoundaries[0].start, rowBoundaries[0].start,
            columnsBoundaries[0].end, rowBoundaries[0].end
        )

    enum class Quadrant {
        TopLeft, TopRight, BottomLeft, BottomRight;

        companion object {
            fun from(isLeft: Boolean, isTop: Boolean): Quadrant =
                if (isLeft) {
                    if (isTop) TopLeft else BottomLeft
                } else {
                    if (isTop) TopRight else BottomRight
                }
        }
    }
}
