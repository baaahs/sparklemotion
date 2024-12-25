package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.show.GridItem
import baaahs.show.GridLayout
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class GridHelperSpec : DescribeSpec({
    describe("test helpers") {
        it("convert from string to layout") {
            """
                AA.B
                AA..
                .CC.
                
                # A:
                XY
                XZ
            """.trimIndent().toLayout()
                .shouldBe(
                    GridLayout(
                        4, 3, matchParent = false,
                        listOf(
                            GridItem("A", 0, 0, 2, 2, layout =
                                GridLayout(2, 2, matchParent = false,
                                    listOf(
                                        GridItem("X", 0, 0, 1, 2),
                                        GridItem("Y", 1, 0, 1, 1),
                                        GridItem("Z", 1, 1, 1, 1),
                                    )
                                ),
                            ),
                            GridItem("B", 3, 0, 1, 1),
                            GridItem("C", 1, 2, 2, 1),
                        )
                    )
                )
        }

        it("convert from layout to string") {
            GridLayout(
                4, 3, matchParent = false,
                listOf(
                    GridItem("A", 0, 0, 2, 2, layout =
                        GridLayout(2, 2, matchParent = false,
                            listOf(
                                GridItem("X", 0, 0, 1, 2),
                                GridItem("Y", 1, 0, 1, 1),
                                GridItem("Z", 1, 1, 1, 1),
                            )
                        )
                    ),
                    GridItem("B", 3, 0, 1, 1),
                    GridItem("C", 1, 2, 2, 1)
                )
            ).stringify().shouldBe(
                """
                    AA.B
                    AA..
                    .CC.
                    
                    # A:
                    XY
                    XZ
                """.trimIndent()
            )
        }
    }
})

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
                gridName = Regex("# *(\\w+):").matchEntire(firstLine)?.groupValues[1]?.trim()
                    ?: error("No name for grid $gridIndex.")
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

fun GridLayout.stringify(): String {
    if (items.isEmpty()) return "[Empty]"

    val gridWidth = if (columns == Int.MAX_VALUE) items.maxOf { it.column + it.width } else columns
    val gridHeight = if (rows == Int.MAX_VALUE) items.maxOf { it.row + it.height } else rows
    val cells = Array(gridHeight) { Array(gridWidth) { "." } }
    val nestedGrids = mutableMapOf<String, GridLayout>()
    items.forEach {
        (0 until it.height).forEach { row ->
            (0 until it.width).forEach { col ->
                if (cells[it.row + row][it.column + col] != ".") {
                    cells[it.row + row][it.column + col] = "!"
                } else {
                    cells[it.row + row][it.column + col] = it.controlId
                    if (it.layout != null) {
                        nestedGrids[it.controlId] = it.layout
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