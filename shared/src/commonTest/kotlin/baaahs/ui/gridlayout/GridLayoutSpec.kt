package baaahs.ui.gridlayout

import baaahs.capitalize
import baaahs.describe
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.show.GridLayout
import baaahs.show.GridTab
import baaahs.show.ImpossibleLayoutException
import baaahs.show.Show
import baaahs.show.mutable.DummyControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableDummyControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class GridLayoutSpec : DescribeSpec({
    describe<GridLayout> {
        val layout by value {
            """
            ABC.
            DEFG
            .HI.
            """
        }
        val tab by value { layout.trimIndent().toGridTab("Tab") }
        val updatedModel by value { Array<GridModel?>(1) { null } }
        val manager by value { TestGridManager(tab.createModel()) { updatedModel[0] = it } }
        val move by value {
            { id: String, into: String, x: Int, y: Int ->
                manager.move(id, into, x, y)
                val controls = buildMap {
                    tab.visit(null) { item, parent ->
                        put(item.controlId, DummyControl(item.controlId, item.controlId))
                    }
                }
                val mutableShow = MutableShow(
                    Show("Dummy Show", controls = controls))
                val mutableTab = tab.edit(emptyMap(), mutableShow)
                updatedModel[0]?.let {
                    mutableTab.applyChanges(it)
                }
                (mutableTab.build(ShowBuilder()) as GridTab)
                    .stringify().uppercase()
            }
        }

        it("moving A one space down swaps A and D") {
            move("A", "_ROOT_", 0, 1).shouldBe(
                """
                DBC.
                AEFG
                .HI.
                """.trimIndent()
            )
        }

        it("moving C to E's spot fails") {
            shouldThrow<ImpossibleLayoutException> { (move("C", "_ROOT_", -1, -1)) }
        }

        it("moving A two spaces down leaves the rest undisturbed") {
            move("A", "_ROOT_", 0, 2).shouldBe(
                """
                .BC.
                DEFG
                AHI.
                """.trimIndent()
            )
        }

        it("moving A one space right swaps A and B") {
            move("A", "_ROOT_", 1, 0).shouldBe(
                """
                BAC.
                DEFG
                .HI.
                """.trimIndent()
            )
        }

        it("moving A two spaces right shifts C over") {
            move("A", "_ROOT_", 2, 0).shouldBe(
                """
                BCA.
                DEFG
                .HI.
                """.trimIndent()
            )
        }

        it("moving D one space right shifts E into its place") {
            move("D", "_ROOT_", 1, 1).shouldBe(
                """
                ABC.
                EDFG
                .HI.
                """.trimIndent()
            )
        }

        it("moving B one space down and left shifts E down") {
            move("B", "_ROOT_", 0, 1).shouldBe(
                """
                A.C.
                BEFG
                DHI.
                """.trimIndent()
            )
        }

        context("with ABCDEF in one row") {
            override(layout) { "ABCDEF" }
            it("moving B two spaces over") {
                move("B", "_ROOT_", 2, 0).shouldBe("ACDBEF")
            }
        }

        context("with .ABBC.") {
            override(layout) { ".ABBC." }

            it("moving A one space right should make no change") {
                move("A", "_ROOT_", 2, 0).shouldBe(".ABBC.")
            }

            it("moving A two spaces right should swap A and B") {
                move("A", "_ROOT_", 3, 0).shouldBe(".BBAC.")
            }

            it("moving C one space left should make no change") {
                move("C", "_ROOT_", 3, 0).shouldBe(".ABBC.")
            }

            it("moving C two spaces left should swap B and C") {
                move("C", "_ROOT_", 2, 0).shouldBe(".ACBB.")
            }
        }
    }
})