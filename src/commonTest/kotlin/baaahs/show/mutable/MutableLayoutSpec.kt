package baaahs.show.mutable

import baaahs.describe
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object MutableLayoutSpec : Spek({
    describe<MutableTab> {
        val columns by value { mutableListOf(1.fr, 2.fr) }
        val rows by value { mutableListOf(3.fr, 4.fr) }
        val areas by value { mutableListOf(
            "a".panel, "b".panel,
            "c".panel, "d".panel,
        ) }
        val tab by value { MutableTab("main", columns, rows, areas) }

        context("appending a column") {
            beforeEachTest {
                tab.appendColumn()
            }

            it("should duplicate the last column's dimension and panels") {
                expect(columns).containsExactly(1.fr, 2.fr, 2.fr)
                expect(rows).containsExactly(3.fr, 4.fr)
                expect(areas.map { it.title }).containsExactly(
                    "a", "b", "b",
                    "c", "d", "d",
                )
            }
        }

        context("appending a row") {
            beforeEachTest {
                tab.appendRow()
            }

            it("should duplicate the last row's dimension and panels") {
                expect(columns).containsExactly(1.fr, 2.fr)
                expect(rows).containsExactly(3.fr, 4.fr, 4.fr)
                expect(areas.map { it.title }).containsExactly(
                    "a", "b",
                    "c", "d",
                    "c", "d",
                )
            }
        }

    }
})

val Number.fr get() = MutableLayoutDimen(this, "fr")
val String.panel get() = MutablePanel(this)