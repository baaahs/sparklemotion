package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.PolyLine.Segment
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly

object GridSpec : DescribeSpec({
    describe<Grid> {
        val grid by value {
            Grid(
                "grid",
                columns = 4, rows = 5, columnGap = 1f, rowGap = 1f,
                direction = GridData.Direction.RowsThenColumns,
                zigZag = true
            )
        }

        it("calculates segments") {
            grid.segments.shouldContainExactly(
                Segment(Vector3F(0.0, 0.0, 0.0), Vector3F(3.0, 0.0, 0.0), 4),
                Segment(Vector3F(3.0, 1.0, 0.0), Vector3F(0.0, 1.0, 0.0), 4),
                Segment(Vector3F(0.0, 2.0, 0.0), Vector3F(3.0, 2.0, 0.0), 4),
                Segment(Vector3F(3.0, 3.0, 0.0), Vector3F(0.0, 3.0, 0.0), 4),
                Segment(Vector3F(0.0, 4.0, 0.0), Vector3F(3.0, 4.0, 0.0), 4)
            )
        }

        context("with stagger") {
            override(grid) {
                Grid(
                    "grid",
                    columns = 4, rows = 5, columnGap = 1f, rowGap = 1f,
                    direction = GridData.Direction.RowsThenColumns,
                    zigZag = true, stagger = 2
                )
            }

            it("calculates segments with stagger") {
                grid.segments.shouldContainExactly(
                    Segment(Vector3F(0.0, 0.0, 0.0), Vector3F(3.0, 0.0, 0.0), 4),
                    Segment(Vector3F(3.5, 1.0, 0.0), Vector3F(0.5, 1.0, 0.0), 4),
                    Segment(Vector3F(0.0, 2.0, 0.0), Vector3F(3.0, 2.0, 0.0), 4),
                    Segment(Vector3F(3.5, 3.0, 0.0), Vector3F(0.5, 3.0, 0.0), 4),
                    Segment(Vector3F(0.0, 4.0, 0.0), Vector3F(3.0, 4.0, 0.0), 4)
                )
            }
        }
    }
})