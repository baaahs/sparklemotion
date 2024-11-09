package baaahs.show.mutable

import baaahs.describe
import baaahs.kotest.value
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly

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
                columns.shouldContainExactly(1.fr, 2.fr, 2.fr)
                rows.shouldContainExactly(3.fr, 4.fr)
                areas.map { it.title }.shouldContainExactly(
                    "a", "b", "b",
                    "c", "d", "d",
                )
            }
        }

        context("duplicating a column") {
            beforeEach { tab.duplicateColumn(0) }

            it("should duplicate the specified column's dimension and panels") {
                columns.shouldContainExactly(1.fr, 1.fr, 2.fr)
                rows.shouldContainExactly(3.fr, 4.fr)
                areas.map { it.title }.shouldContainExactly(
                    "a", "a", "b",
                    "c", "c", "d",
                )
            }
        }

        context("deleting a column") {
            beforeEach { tab.deleteColumn(0) }

            it("should delete the specified column's dimension and panels") {
                columns.shouldContainExactly(2.fr)
                rows.shouldContainExactly(3.fr, 4.fr)
                areas.map { it.title }.shouldContainExactly(
                    "b",
                    "d",
                )
            }
        }

        context("appending a row") {
            beforeEach { tab.appendRow() }

            it("should duplicate the last row's dimension and panels") {
                columns.shouldContainExactly(1.fr, 2.fr)
                rows.shouldContainExactly(3.fr, 4.fr, 4.fr)
                areas.map { it.title }.shouldContainExactly(
                    "a", "b",
                    "c", "d",
                    "c", "d",
                )
            }
        }

        context("duplicating a row") {
            beforeEach { tab.duplicateRow(0) }

            it("should duplicate the specified row's dimension and panels") {
                columns.shouldContainExactly(1.fr, 2.fr)
                rows.shouldContainExactly(3.fr, 3.fr, 4.fr)
                areas.map { it.title }.shouldContainExactly(
                    "a", "b",
                    "a", "b",
                    "c", "d",
                )
            }
        }

        context("deleting a row") {
            beforeEach { tab.deleteRow(0) }

            it("should delete the specified row's dimension and panels") {
                columns.shouldContainExactly(1.fr, 2.fr)
                rows.shouldContainExactly(4.fr)
                areas.map { it.title }.shouldContainExactly(
                    "c", "d",
                )
            }
        }
    }
})

val Number.fr get() = MutableLayoutDimen(this, "fr")
val String.panel get() = MutablePanel(this)