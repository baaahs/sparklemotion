package baaahs.ui.gridlayout

import baaahs.describe
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

class GridModelSpec : DescribeSpec({
    fun node(id: String, column: Int, row: Int, vararg children: Node) =
        Node(
            id, column, row, 1, 1,
            layout = if (children.isNotEmpty())
                Layout(
                    children.maxOf { it.left } + 1,
                    children.maxOf { it.top } + 1,
                    listOf(*children)
                )
            else null
        )

    describe<GridModel> {
        context("canonicalization") {
            it("makes equivalent graphs identical by sorting by columns, then rows") {
                val grid = GridModel(
                    node("root", 0, 0,
                        node("A", 1, 1),
                        node("B", 0, 1),
                        node("C", 0, 0),
                        node("D", 1, 0,
                            node("y", 1, 0),
                            node("z", 0, 0)
                        )
                    )
                )
                grid.canonicalize() shouldBe
                        GridModel(
                            node("root", 0, 0,
                                node("C", 0, 0),
                                node("D", 1, 0,
                                    node("z", 0, 0),
                                    node("y", 1, 0)
                                ),
                                node("B", 0, 1),
                                node("A", 1, 1)
                            )
                        )
            }
        }
    }
})