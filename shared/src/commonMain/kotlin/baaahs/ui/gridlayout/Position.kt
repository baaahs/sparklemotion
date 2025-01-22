package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.ui.gridlayout.GridContainer.Quadrant

data class GridPosition(
    val x: Int,
    val y: Int,
    val quadrant: Quadrant
) {
    val cell: Vector2I get() = Vector2I(x, y)
}