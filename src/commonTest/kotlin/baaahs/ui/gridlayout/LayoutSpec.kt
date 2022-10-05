package baaahs.ui.gridlayout

import baaahs.describe
import baaahs.gl.override
import baaahs.toEqual
import ch.tutteli.atrium.api.fluent.en_GB.toThrow
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object LayoutSpec : Spek({
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
            expect(move("A", 0, 1)).toEqual("""
                    DBC.
                    AEFG
                    .HI.
                """.trimIndent())
        }

        it("moving C to E's spot fails") {
            expect { (move("C", -1, -1)) }.toThrow<ImpossibleLayoutException>()
        }

        it("moving A two spaces down leaves the rest undisturbed") {
            expect(move("A", 0, 2)).toEqual("""
                    .BC.
                    DEFG
                    AHI.
                """.trimIndent())
        }

        it("moving A one space right swaps A and B") {
            expect(move("A", 1, 0)).toEqual("""
                    BAC.
                    DEFG
                    .HI.
                """.trimIndent())
        }

        it("moving A two spaces right shifts C over") {
            expect(move("A", 2, 0)).toEqual("""
                    BCA.
                    DEFG
                    .HI.
                """.trimIndent())
        }

        it("moving D one space right shifts E into its place") {
            expect(move("D", 1, 0)).toEqual("""
                    ABC.
                    EDFG
                    .HI.
                """.trimIndent())
        }

        it("moving B one space down and left shifts E down") {
            expect(move("B", -1, 1)).toEqual("""
                    A.C.
                    BEFG
                    DHI.
                """.trimIndent())
        }

        context("with ABCDEF in one row") {
            override(layout) { "ABCDEF".toLayout() }
            it("moving B two spaces over") {
                expect(move("B", 2, 0)).toEqual("ACDBEF")
            }
        }

        context("with a full grid (ABCD/EFGH/IJKL/MNOP)") {
            override(layout) { """
                ABCD
                EFGH
                IJKL
                MNOP
            """.trimIndent().toLayout() }

            it("items can be moved only horizontally OR vertically, not both") {
                expect { (move("A", 2, 2)) }.toThrow<ImpossibleLayoutException>()
            }

            xit("TODO: items can be moved both horizontally and vertically") {
                expect(move("A", 2, 2)).toEqual("""
                    BCGD
                    EFKH
                    IJAL
                    MNOP
                """.trimIndent())
            }
        }

        context("with .ABBC.") {
            override(layout) { ".ABBC.".toLayout() }

            xit("moving A one space right should swap A and B") {
                expect(move("A", 1, 0)).toEqual(".BBAC.")
            }

            it("moving A two spaces right should swap A and B") {
                expect(move("A", 2, 0)).toEqual(".BBAC.")
            }

            xit("moving C one space left should swap B and C") {
                expect(move("C", -1, 0)).toEqual(".ACBB.")
            }

            it("moving C two spaces left should swap B and C") {
                expect(move("C", -2, 0)).toEqual(".ACBB.")
            }
        }
    }
})