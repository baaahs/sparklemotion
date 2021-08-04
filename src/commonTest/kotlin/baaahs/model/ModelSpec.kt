package baaahs.model

import baaahs.describe
import baaahs.fakeModel
import baaahs.fixtures.PixelArrayDevice
import baaahs.geom.Vector3F
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object ModelSpec : Spek({
    describe<Model> {
        context("model bounds") {
            it("should include all points defining a surface") {
                val v1 = Vector3F(1f, 0f, 5f)
                val v2 = Vector3F(-1f, 1f, 0f)
                val v3 = Vector3F(0f, 1f, -.25f)

                val surface = Model.Surface(
                    "triangle", "triangle", PixelArrayDevice, null,
                    listOf(Model.Face(v1, v2, v3)),
                    listOf(Model.Line(v1, v2), Model.Line(v2, v3), Model.Line(v3, v1))
                )

                expect(fakeModel(surface).modelBounds).toEqual(
                    Vector3F(-1f, 0f, -.25f) to Vector3F(1f, 1f, 5f)
                )
            }

            it("should include start and end points in a light bar") {
                val v1 = Vector3F(1f, 0f, 5f)
                val v2 = Vector3F(-1f, 1f, 0f)
                val v3 = Vector3F(0f, 1f, -.25f)
                val v4 = Vector3F(7f, -3f, 5.25f)

                expect(fakeModel(
                    LightBar("bar1", "bar2", PixelArrayDevice, v1, v2),
                    LightBar("bar2", "bar2", PixelArrayDevice, v3, v4)
                ).modelBounds).toEqual(
                    Vector3F(-1f, -3f, -.25f) to Vector3F(7f, 1f, 5.25f)
                )
            }
        }
    }
})