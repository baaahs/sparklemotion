package baaahs.ui.gridlayout

import baaahs.kotest.value
import baaahs.show.mutable.ShowBuilder
import baaahs.show.mutable.editForSpec
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe

@Suppress("unused")
class GridAdapterSpec : DescribeSpec({
    describe("MutableGridTab.applyChanges()") {
        val tab by value { "abc".toGridTab("Tab") }
        val mutableTab by value { tab.editForSpec() }

        it("updates from grid model — root node change") {
            val updatedModel = "acb".toGridTab("Tab").createModel()
            mutableTab.applyChanges(updatedModel)
            val updatedTab = mutableTab.build(ShowBuilder())
            updatedTab shouldBe "acb".toGridTab("Tab")
        }

        context("with nested grids") {
            value(tab) { "aa\n\n# a:\nxyz".toGridTab("Tab") }
            it("updates from grid model — sub node change") {
                val updatedModel = "aa\n\n# a:\nzyx".toGridTab("Tab").createModel()
                mutableTab.applyChanges(updatedModel)
                val updatedTab = mutableTab.build(ShowBuilder())
                updatedTab shouldBe "aa\n\n# a:\nzyx".toGridTab("Tab")
            }
        }
    }
})