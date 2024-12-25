package baaahs.ui.gridlayout

import baaahs.ui.gridlayout.GridContainer.Quadrant

data class GridCoords(
    val left: Int,
    val top: Int,
    val width: Int,
    val height: Int
) {
    val right get() = left + width - 1
    val bottom get() = top + height - 1
    val size: GridSize get() = GridSize(width, height)
}

data class GridPosition(
    val x: Int,
    val y: Int,
    val quadrant: Quadrant
)

data class GridSize(
    val width: Int,
    val height: Int
)
