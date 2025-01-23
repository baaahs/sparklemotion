package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.getBang
import baaahs.only
import baaahs.show.GridItem
import baaahs.show.GridLayout
import baaahs.show.GridTab
import baaahs.show.IGridLayout

fun String.toGridTab(name: String): GridTab =
    toLayout().let {
        GridTab(name, it.columns, it.rows, it.items)
    }

fun String.toLayout(): GridLayout {
    val grids = mutableMapOf<String, GridLayout>()

    val gridStrings = trim().split("\n\n")
    gridStrings.reversed().forEachIndexed { index, gridString ->
        val gridIndex = gridStrings.lastIndex - index
        val isRoot = gridIndex == 0

        var gridName = "ROOT"
        val lines = gridString.trim().split("\n").let {
            if (isRoot) it else {
                val firstLine = it.first().trim()
                gridName = Regex("# *(\\w+):?").matchEntire(firstLine)?.groupValues[1]?.trim()
                    ?: error("No name for grid $gridIndex.\n${trim()}")
                it.subList(1, it.size)
            }
        }

        val items = mutableMapOf<Char, MutableList<Vector2I>>()
        var cols = 0
        var rows = 0

        lines.forEachIndexed { row, line ->
            rows = maxOf(rows, row)
            line.toCharArray().forEachIndexed { col, id ->
                cols = maxOf(cols, col)

                if (!(id == '.' || id == ' ')) {
                    items.getOrPut(id) { arrayListOf() }
                        .add(Vector2I(col, row))
                }
            }
        }
        grids.put(
            gridName,
            GridLayout(
                cols + 1, rows + 1,
                matchParent = false,
                items.map { (id, coords) ->
                    val gridId = id.toString()
                    val x = coords.minOf { it.x }
                    val y = coords.minOf { it.y }
                    val width = coords.maxOf { it.x } - x + 1
                    val height = coords.maxOf { it.y } - y + 1
                    GridItem(gridId, x, y, width, height, grids[gridId])
                }
            )
        )
    }
    return grids["ROOT"] ?: error("No root grid?")
}

fun IGridLayout.stringify(): String {
    if (items.isEmpty()) return "[Empty]"

    val gridWidth = if (columns == Int.MAX_VALUE) items.maxOf { it.column + it.width } else columns
    val gridHeight = if (rows == Int.MAX_VALUE) items.maxOf { it.row + it.height } else rows
    val cells = Array(gridHeight) { Array(gridWidth) { "." } }
    val nestedGrids = mutableMapOf<String, GridLayout>()
    items.forEach { item ->
        (0 until item.height).forEach { row ->
            (0 until item.width).forEach { col ->
                val iCol = item.column + col
                val iRow = item.row + row
                if (iCol + 1 > gridWidth || iRow + 1 > gridHeight) {
                    error("Item ${item.id} $iCol,$iRow > $gridWidth,$gridHeight\n$item\n$this")
                }

                if (cells[iRow][iCol] != ".") {
                    cells[iRow][iCol] = "!"
                } else {
                    cells[iRow][iCol] = item.controlId
                    if (item.layout != null) {
                        nestedGrids[item.controlId] = item.layout
                    }
                }
            }
        }
    }
    return buildString {
        append(cells.joinToString("\n", transform = { it.joinToString("") }))
        nestedGrids.forEach { (gridName, nestedGrid) ->
            append("\n\n# $gridName:\n")
            append(nestedGrid.stringify())
        }
    }
}

class TestGridManager(
    model: GridModel,
    onChange: (GridModel) -> Unit
) : GridManager(model, onChange) {
    override val placeholder: Placeholder = TestPlaceholder()

    override fun createNodeWrapper(node: Node): NodeWrapper =
        TestNodeWrapper(node)

    fun move(movingNode: String, intoNode: String, column: Int, row: Int, vararg directions: Direction) {
        move(
            nodeWrappers[movingNode]!!.node,
            nodeWrappers[intoNode]!!.node,
            Vector2I(column, row),
            nodeWrappers[movingNode]!!.node.size,
            directions as Array<Direction>
        )
    }

    override fun debug(s: String) {
    }

    inner class TestNodeWrapper(node: Node) : NodeWrapper(node) {
        override fun updateEditable() {}
        override fun applyStyle() {
            // Automatically lay out children if we're a container.
            val layoutBounds = layoutBounds
            val layout = node.layout
            if (layoutBounds != null && layout != null) {
                layoutContainer(GridContainer(layout.columns, layout.rows, layoutBounds, 0))
            }
        }
    }

    inner class TestPlaceholder : Placeholder() {
        override fun applyStyle() {}
    }
}

class Gesture(
    val gridManager: GridManager,
    val gridChanges: MutableList<GridModel>
) {
    lateinit var nodeId: String
    lateinit var nodeWrapper: GridManager.NodeWrapper
    val onlyChange get() = gridChanges.only("change").stringify()
    val changes get() = gridChanges
    lateinit var pointerDownPx: Vector2I
    lateinit var pointerCurrentPx: Vector2I
    val effectiveBounds get() = nodeWrapper.effectiveBounds

    fun onNode(id: String): Gesture {
        nodeId = id
        nodeWrapper = gridManager.nodeWrappers.getBang(id, "node wrapper")
        return this
    }

    fun pointerDownOnBottomRightResizeHandle(): Gesture =
        pointerDown(
            nodeWrapper.layoutBounds!!.bottomRight,
            DraggingState.ResizeFromBottomRight
        )

    fun pointerDown(
        position: Vector2I = nodeWrapper.layoutBounds!!.center,
        draggingState: DraggingState = DraggingState.Move
    ): Gesture {
        pointerDownPx = position
        pointerCurrentPx = position
        nodeWrapper.onPointerDown(position, draggingState)
        return this
    }

    /** Drag and drop within the same container node. */
    fun dragAndDropAt(xPx: Int, yPx: Int): Gesture {
        dragBy(xPx, yPx)
        dropThere()
        return this
    }

    fun dragBy(xPx: Int, yPx: Int): Gesture {
        pointerCurrentPx += Vector2I(xPx, yPx)
        nodeWrapper.onPointerMove(pointerCurrentPx)
        return this
    }

    fun dropAt(xPx: Int, yPx: Int): Gesture {
        pointerCurrentPx += Vector2I(xPx, yPx)
        return dropThere()
    }

    fun dropThere(): Gesture {
        nodeWrapper.onPointerUp(pointerCurrentPx)
        pointerDownPx = Vector2I(-1, -1)
        pointerCurrentPx = Vector2I(-1, -1)
        return this
    }
}
