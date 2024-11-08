package baaahs.model

import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.kotest.value
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import io.kotest.core.spec.style.DescribeSpec
import kotlin.math.sqrt

object LineSpec : DescribeSpec({
    describe<Model.Line> {
        val vertices by value {
            listOf(
                Vector3F(0f, 0f, 0f),
                Vector3F(0f, 1f, 0f),
                Vector3F(1f, 0f, 0f),
            )
        }

        val line by value {
            Model.Line(Model.Geometry(vertices), listOf(0, 1, 2, 0))
        }

        context("shortestDistanceTo") {
            it("calculates the shortest distance to a point") {
                expect(line.shortestDistanceTo(Vector3F(0f, 0f, 0f)))
                    .toEqual(0f)

                expect(line.shortestDistanceTo(Vector3F(1f, 1f, 0f)))
                    .toEqual(sqrt(2f) / 2f)
            }
        }
    }
})