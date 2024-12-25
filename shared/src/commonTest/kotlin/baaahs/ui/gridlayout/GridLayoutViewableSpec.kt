package baaahs.ui.gridlayout

import baaahs.describe
import baaahs.geom.Vector2I
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.show.GridLayout
import baaahs.show.live.EmptyOpenContext
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*
import io.kotest.matchers.maps.shouldContainExactly

class GridLayoutViewableSpec : DescribeSpec({
    describe<GridLayout> {
        val layout by value {
            """
                ABB.
                DBBG
                .HI.
                ....
            """.trimIndent().toLayout()
        }
        val viewRoot by value {
            ViewRoot(layout.open(EmptyOpenContext))
        }

        val allViews by value {
            buildMap { viewRoot.visit { put(it.id, it) } }
        }

        context("with no margin or gap") {
            it("lays out grid") {
                viewRoot.layout(Rect(100, 100, 400, 400))
                allViews.mapValues { (_, v) -> v.bounds }
                    .shouldContainExactly(
                        mapOf(
                            "##VIEWROOT##" to null,
                            "ROOT" to Rect(100, 100, 400, 400),
                            "A" to Rect(100, 100, 100, 100),
                            "B" to Rect(200, 100, 200, 200),
                            "D" to Rect(100, 200, 100, 100),
                            "G" to Rect(400, 200, 100, 100),
                            "H" to Rect(200, 300, 100, 100),
                            "I" to Rect(300, 300, 100, 100)
                        )
                    )
            }
        }

        context("with margin and gap") {
            beforeEach {
                viewRoot.gap = 10
                viewRoot.margins = 10
            }

            it("lays out grid") {
                viewRoot.layout(Rect(0, 0, 400, 400))
                allViews.mapValues { (_, v) -> v.bounds }
                    .shouldContainExactly(
                        mapOf("##VIEWROOT##" to null,
                            "ROOT" to Rect(10, 10, 380, 380),
                            "A" to Rect(10, 10, 87, 87),
                            "B" to Rect(107, 10, 175, 175),
                            "D" to Rect(10, 107, 87, 87),
                            "G" to Rect(302, 107, 87, 87),
                            "H" to Rect(107, 205, 87, 87),
                            "I" to Rect(205, 205, 87, 87)
                        )
                    )
            }
        }

        val find by value {
            { id: String -> viewRoot.find(id) ?: error("Couldn't find $id.") }
        }
        val move by value {
            { id: String, x: Int, y: Int ->
                val view = find(id)
                view.draggedBy(Vector2I(x, y))
                // TODO viewRoot.gridLayout.stringify()
                ""
            }
        }

        context("rearranging") {
            beforeEach {
                viewRoot.layout(Rect(100, 100, 400, 400))
            }

            it("can move H down two cells") {
                move("H", 25, 99)
                    .shouldBe(
                        """
                            ABB.
                            DBBG
                            .HI.
                            ....
                        """.trimIndent()
                )

                shouldThrow<ImpossibleLayoutException> { (move("C", -1, -1)) }
            }

//            it("moving C to E's spot fails") {
//                val c = find("C")
//                c.draggedBy(c.bounds.left + 1)
//                shouldThrow<ImpossibleLayoutException> { (move("C", -1, -1)) }
//            }
        }

        it("moving A two spaces down leaves the rest undisturbed") {
            move("A", 0, 2).shouldBe(
                """
                    .BC.
                    DEFG
                    AHI.
                """.trimIndent()
            )
        }

        it("moving A one space right swaps A and B") {
            move("A", 1, 0).shouldBe(
                """
                    BAC.
                    DEFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving A two spaces right shifts C over") {
            move("A", 2, 0).shouldBe(
                """
                    BCA.
                    DEFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving D one space right shifts E into its place") {
            move("D", 1, 0).shouldBe(
                """
                    ABC.
                    EDFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving B one space down and left shifts E down") {
            move("B", -1, 1).shouldBe(
                """
                    A.C.
                    BEFG
                    DHI.
                """.trimIndent()
            )
        }

        context("with ABCDEF in one row") {
            override(layout) { "ABCDEF".toLayout() }
            it("moving B two spaces over") {
                move("B", 2, 0).shouldBe("ACDBEF")
            }
        }

        context("with .ABBC.") {
            override(layout) { ".ABBC.".toLayout() }

            it("moving A one space right should make no change") {
                move("A", 1, 0).shouldBe(".ABBC.")
            }

            it("moving A two spaces right should swap A and B") {
                move("A", 2, 0).shouldBe(".BBAC.")
            }

            it("moving C one space left should make no change") {
                move("C", -1, 0).shouldBe(".ABBC.")
            }

            it("moving C two spaces left should swap B and C") {
                move("C", -2, 0).shouldBe(".ACBB.")
            }
        }
    }
})