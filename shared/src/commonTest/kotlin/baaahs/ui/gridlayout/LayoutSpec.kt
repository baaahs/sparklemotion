package baaahs.ui.gridlayout

import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class LayoutSpec : DescribeSpec({
    describe<Layout> {
        val layout by value {
            """
                ABC.
                DEFG
                .HI.
            """.trimIndent().toLayout()
        }

        val move by value {
            { id: String, x: Int, y: Int ->
                val item = layout.find(id)!!
                layout.moveElement(item, item.x + x, item.y + y)
                    .stringify()
            }
        }

        it("moving A one space down swaps A and D") {
            move("A", 0, 1).shouldBe(
                """
                    DBC.
                    AEFG
                    .HI.
                """.trimIndent()
            )
        }

        it("moving C to E's spot fails") {
            shouldThrow<ImpossibleLayoutException> { (move("C", -1, -1)) }
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

            xit("moving A one space right should swap A and B") {
                move("A", 1, 0).shouldBe(".BBAC.")
            }

            it("moving A two spaces right should swap A and B") {
                move("A", 2, 0).shouldBe(".BBAC.")
            }

            xit("moving C one space left should swap B and C") {
                move("C", -1, 0).shouldBe(".ACBB.")
            }

            it("moving C two spaces left should swap B and C") {
                move("C", -2, 0).shouldBe(".ACBB.")
            }
        }
    }
})