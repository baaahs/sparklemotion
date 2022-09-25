package baaahs.ui.gridlayout

import baaahs.describe
import baaahs.toEqual
import baaahs.ui.gridlayout.CompactType.Companion.determineFrom
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
                val compactType = item.determineFrom(x, y)
                layout.moveElement(item, x, y, false, compactType)
            }
        }

        it("moving A one space down shifts D down") {
            expect(move("A", 0, 1).stringify()).toEqual("""
                    .BC.
                    AEFG
                    DHI.
                """.trimIndent())
        }

        it("moving A two spaces down leaves the rest undisturbed") {
            expect(move("A", 0, 2).stringify()).toEqual("""
                    .BC.
                    DEFG
                    AHI.
                """.trimIndent())
        }

        it("moving A one space right shifts B and C over, because there's room to the right") {
            expect(move("A", 1, 0).stringify()).toEqual("""
                    .ABC
                    DEFG
                    .HI.
                """.trimIndent())
        }

        it("moving A two spaces right shifts C over") {
            expect(move("A", 2, 0).stringify()).toEqual("""
                    .BAC
                    DEFG
                    .HI.
                """.trimIndent())
        }

        it("moving D one space right shifts E into its place") {
            expect(move("D", 1, 1).stringify()).toEqual("""
                    ABC.
                    EDFG
                    .HI.
                """.trimIndent())
        }
    }
})