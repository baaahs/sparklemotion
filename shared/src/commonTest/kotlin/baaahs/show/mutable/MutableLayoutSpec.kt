package baaahs.show.mutable

import baaahs.describe
import baaahs.kotest.value
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec

object MutableLayoutSpec : DescribeSpec({
    describe<MutableLegacyTab> {
        val columns by value { mutableListOf(1.fr, 2.fr) }
        val rows by value { mutableListOf(3.fr, 4.fr) }
        val areas by value { mutableListOf(
            "a".panel, "b".panel,
            "c".panel, "d".panel,
        ) }
        val tab by value { MutableLegacyTab("main", columns, rows, areas) }

        context("appending a column") {
            beforeEach { tab.appendColumn() }

            it("should duplicate the last column's dimension and panels") {
                expect(columns).containsExactly(1.fr, 2.fr, 2.fr)
                expect(rows).containsExactly(3.fr, 4.fr)
                expect(areas.map { it.title }).containsExactly(
                    "a", "b", "b",
                    "c", "d", "d",
                )
            }
        }

        context("duplicating a column") {
            beforeEach { tab.duplicateColumn(0) }

            it("should duplicate the specified column's dimension and panels") {
                expect(columns).containsExactly(1.fr, 1.fr, 2.fr)
                expect(rows).containsExactly(3.fr, 4.fr)
                expect(areas.map { it.title }).containsExactly(
                    "a", "a", "b",
                    "c", "c", "d",
                )
            }
        }

        context("deleting a column") {
            beforeEach { tab.deleteColumn(0) }

            it("should delete the specified column's dimension and panels") {
                expect(columns).containsExactly(2.fr)
                expect(rows).containsExactly(3.fr, 4.fr)
                expect(areas.map { it.title }).containsExactly(
                    "b",
                    "d",
                )
            }
        }

        context("appending a row") {
            beforeEach { tab.appendRow() }

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

        context("duplicating a row") {
            beforeEach { tab.duplicateRow(0) }

            it("should duplicate the specified row's dimension and panels") {
                expect(columns).containsExactly(1.fr, 2.fr)
                expect(rows).containsExactly(3.fr, 3.fr, 4.fr)
                expect(areas.map { it.title }).containsExactly(
                    "a", "b",
                    "a", "b",
                    "c", "d",
                )
            }
        }

        context("deleting a row") {
            beforeEach { tab.deleteRow(0) }

            it("should delete the specified row's dimension and panels") {
                expect(columns).containsExactly(1.fr, 2.fr)
                expect(rows).containsExactly(4.fr)
                expect(areas.map { it.title }).containsExactly(
                    "c", "d",
                )
            }
        }
    }
})

val Number.fr get() = MutableLayoutDimen(this, "fr")
val String.panel get() = MutablePanel(this)