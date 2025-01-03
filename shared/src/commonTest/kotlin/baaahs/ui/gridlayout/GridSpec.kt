package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

@Suppress("unused")
class GridSpec : DescribeSpec({
    describe("test helpers") {
        it("convert from string to layout") {
            """
                AA.B
                AA..
                .CC.
            """.trimIndent().toLayout().shouldBe(
                Layout(
                    4, 3,
                    LayoutItem(0, 0, 2, 2, "A"),
                    LayoutItem(3, 0, 1, 1, "B"),
                    LayoutItem(1, 2, 2, 1, "C"),
                )
            )
        }

        it("convert from layout to string") {
            Layout(
                4, 3,
                LayoutItem(0, 0, 2, 2, "A"),
                LayoutItem(3, 0, 1, 1, "B"),
                LayoutItem(1, 2, 2, 1, "C")
            ).stringify().shouldBe(
                """
                    AA.B
                    AA..
                    .CC.
                """.trimIndent()
            )
        }
    }
})

fun String.toLayout(): Layout {
    val items = mutableMapOf<Char, MutableList<Vector2I>>()
    var cols = 0
    var rows = 0
    trim().split("\n").forEachIndexed { row, line ->
        rows = maxOf(rows, row)
        line.toCharArray().forEachIndexed { col, id ->
            cols = maxOf(cols, col)

            if (!(id == '.' || id == ' ')) {
                items.getOrPut(id) { arrayListOf() }
                    .add(Vector2I(col, row))
            }
        }
    }
    return Layout(
        items.map { (id, coords) ->
            val x = coords.minOf { it.x }
            val y = coords.minOf { it.y }
            val width = coords.maxOf { it.x } - x + 1
            val height = coords.maxOf { it.y } - y + 1
            LayoutItem(x, y, width, height, id.toString())
        }, cols + 1, rows + 1
    )
}

fun Layout.stringify(): String {
    if (items.isEmpty()) return "[Empty]"

    val gridWidth = if (cols == Int.MAX_VALUE) items.maxOf { it.x + it.w } else cols
    val gridHeight = if (rows == Int.MAX_VALUE) items.maxOf { it.y + it.h } else rows
    val cells = Array(gridHeight) { Array(gridWidth) { "." } }
    items.forEach {
        (0 until it.h).forEach { row ->
            (0 until it.w).forEach { col ->
                if (cells[it.y + row][it.x + col] != ".") {
                    cells[it.y + row][it.x + col] = "!"
                } else {
                    cells[it.y + row][it.x + col] = it.i
                }
            }
        }
    }
    return cells.joinToString("\n", transform = { it.joinToString("") })
}