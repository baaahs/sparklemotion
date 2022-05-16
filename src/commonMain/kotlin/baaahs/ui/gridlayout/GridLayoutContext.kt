package baaahs.ui.gridlayout

import baaahs.geom.Vector2D
import baaahs.geom.Vector2I

class GridLayoutCommon {
}

class GridItemCommon {
}

data class GridItemStateCommon(
//    var parentContainer: GridLayout
    var dragging: Vector2D?,
//    var draggingFromContainer: GridLayout?
    var resizing: Vector2I?
)

data class Size(
    var width: Int,
    var height: Int
)