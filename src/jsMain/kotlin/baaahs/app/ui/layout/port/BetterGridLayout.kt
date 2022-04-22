package baaahs.app.ui.layout.port

import external.react_grid_layout.ReactGridLayoutClass
import external.react_grid_layout.GridLayoutProps

class BetterGridLayout(props: GridLayoutProps) : ReactGridLayoutClass(props) {
    override fun onDrag(i: String, x: Number, y: Number, event: GridDragEvent) {
        console.log("onDrag", i, x, y, event)
        super.onDrag(i, x, y, event)
    }
}