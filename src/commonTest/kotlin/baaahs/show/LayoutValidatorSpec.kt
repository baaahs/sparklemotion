package baaahs.show

import baaahs.describe
import baaahs.gl.override
import baaahs.toBeSpecified
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.fluent.en_GB.isEmpty
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object LayoutValidatorSpec : Spek({
    describe<LayoutValidator> {
        val layoutValidator by value { LayoutValidator() }
        val tab by value<Tab> { toBeSpecified() }
        val invalidRegions by value { layoutValidator.findInvalidRegions(tab) }

        context("when all regions are single cells") {
            override(tab) { tab(3, 3, "ABC DEF GHI") }
            it("should return no invalid regions") {
                expect(invalidRegions).isEmpty()
            }
        }

        context("when all regions are rectangular") {
            override(tab) { tab(3, 3, "AAB AAC DDC") }
            it("should return no invalid regions") {
                expect(invalidRegions).isEmpty()
            }
        }

        context("when a region isn't rectangular") {
            override(tab) { tab(3, 3, "AAB AEC DDC") }
            it("should return the non-rectangular region") {
                expect(invalidRegions).containsExactly("A")
            }
        }

        context("when multiple regions aren't rectangular") {
            override(tab) { tab(3, 3, "AAB ADC DDC") }
            it("should return the non-rectangular region") {
                expect(invalidRegions).containsExactly("A", "D")
            }
        }

        context("when a region is non-contiguous") {
            override(tab) { tab(3, 3, "AAB AAC DDA") }
            it("should return the non-contiguous region") {
                expect(invalidRegions).containsExactly("A")
            }
        }
    }
})

fun tab(columns: Int, rows: Int, areas: String): Tab {
    val areaNames = areas.filter { it != ' ' }.map { it.toString() }
    if (areaNames.size != columns * rows) error("should be ${columns * rows} areas")
    return Tab(
        "Main",
        Array(columns) { "1fr" }.toList(),
        Array(rows) { "1fr" }.toList(),
        areaNames.toList()
    )
}