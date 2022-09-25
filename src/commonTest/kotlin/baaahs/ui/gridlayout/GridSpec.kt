package baaahs.ui.gridlayout

import baaahs.geom.Vector2I
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe

@Suppress("unused")
object GridSpec : Spek({
    describe("test helpers") {
        it("convert from string to layout") {
            expect(
                """
                        AA.B
                        AA..
                        .CC.
                    """.trimIndent().toLayout()
            ).toEqual(
                Layout(
                    4, 3,
                    LayoutItem(0, 0, 2, 2, "A"),
                    LayoutItem(3, 0, 1, 1, "B"),
                    LayoutItem(1, 2, 2, 1, "C"),
                )
            )
        }

        it("convert from layout to string") {
            expect(
                Layout(
                    4, 3,
                    LayoutItem(0, 0, 2, 2, "A"),
                    LayoutItem(3, 0, 1, 1, "B"),
                    LayoutItem(1, 2, 2, 1, "C")
                ).stringify()
            ).toEqual(
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

    val gridWidth = items.maxOf { it.x + it.w }
    val gridHeight = items.maxOf { it.y + it.h }
    val cells = Array(gridHeight) { Array(gridWidth) { "." } }
    items.forEach {
        (0 until it.h).forEach { row ->
            (0 until it.w).forEach { col ->
                cells[it.y + row][it.x + col] = it.i
            }
        }
    }
    return cells.joinToString("\n", transform = { it.joinToString("") })
}