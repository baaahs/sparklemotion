package baaahs.ui.gridlayout

import baaahs.describe
import baaahs.focused
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.ui.gridlayout.GridContainer.Quadrant
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.*

class GridContainerSpec : DescribeSpec({
    describe<GridContainer> {
        val gap by value { { 0 } }
        val bounds by value { Rect(50, 50, 100, 200) }
        val container by value {
            GridContainer(4, 4, bounds, gap = gap())
        }

        context("with no gap") {
            it("calculates grid sizes") {
                container.calculateRegionBounds(0, 0, 1, 1)
                    .shouldBe(Rect(50, 50, 25, 50))
                container.calculateRegionBounds(1, 1, 1, 1)
                    .shouldBe(Rect(75, 100, 25, 50))
                container.calculateRegionBounds(1, 1, 2, 2)
                    .shouldBe(Rect(75, 100, 50, 100))
            }

            it("doesn't fail on out-of-bounds arguments") {
                container.calculateRegionBounds(0, 0, 10, 10)
                    .shouldBe(Rect(50, 50, 100, 200))
            }

            it("clamps cells to the size of the layout").config(tags = setOf(focused)) {
                container.findCell(-1, -1)
                    .shouldBe(GridPosition(0, 0, Quadrant.TopLeft))
                container.findCell(bounds.right + 10, bounds.bottom + 10)
                    .shouldBe(GridPosition(container.columns - 1, container.rows - 1, Quadrant.BottomRight))
            }

            it("finds cell from coordinates") {
                val left = container.bounds.left
                val top = container.bounds.top

                val container = container
                container.findCell(0, 0)
                    .shouldBe(GridPosition(0, 0, Quadrant.TopLeft))

                container.findCell(12 + left, 25 + top)
                    .shouldBe(GridPosition(0, 0, Quadrant.TopLeft))
                container.findCell(13 + left, 25 + top)
                    .shouldBe(GridPosition(0, 0, Quadrant.TopRight))
                container.findCell(12 + left, 26 + top)
                    .shouldBe(GridPosition(0, 0, Quadrant.BottomLeft))
                container.findCell(13 + left, 26 + top)
                    .shouldBe(GridPosition(0, 0, Quadrant.BottomRight))

                container.findCell(25, 50)
                    .shouldBe(GridPosition(0, 0, Quadrant.TopLeft))
            }
        }

        context("with a gap") {
            override(bounds) { Rect(0, 0, 408, 408) }
            override(gap) { { 8 } }

            it("calculates grid sizes") {
                container.calculateRegionBounds(0, 0, 1, 1)
                    .shouldBe(Rect(0, 0, 96, 96))
                container.calculateRegionBounds(1, 1, 1, 1)
                    .shouldBe(Rect(104, 104, 96, 96))
                container.calculateRegionBounds(3, 3, 1, 1)
                    .shouldBe(Rect(312, 312, 96, 96))
            }
        }
    }
})