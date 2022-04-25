package baaahs.model

import baaahs.describe
import baaahs.fakeModel
import baaahs.geom.EulerAngle
import baaahs.geom.Matrix4F
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.nuffin
import baaahs.toEqual
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek

object ModelSpec : Spek({
    describe<Model> {
        context("applying parent transformations") {
            it("multiplies them") {
                val data = LightBarData(
                    "bar",
                    position = Vector3F.unit3d,
                    startVertex = Vector3F.origin, endVertex = Vector3F.unit3d
                )
                val entity =
                    data.open(Matrix4F.compose(Vector3F(.5, .5, .5), EulerAngle.identity))
                expect(entity.position).toEqual(Vector3F(1.5, 1.5, 1.5))
            }
        }

        context("model bounds") {
            val model by value { nuffin<Model>() }
            val v1 by value { Vector3F(1f, 0f, 5f) }
            val v2 by value { Vector3F(-1f, 1f, 0f) }
            val v3 by value { Vector3F(0f, 1f, -.25f) }

            context("with a hand-created OBJ import") {
                val position by value { Vector3F.origin }
                override(model) {
                    val geometry = Model.Geometry(listOf(v1, v2, v3))
                    fakeModel(
                        Model.Surface(
                            "triangle", "triangle", null,
                            listOf(Model.Face(geometry, 0, 1, 2)),
                            listOf(
                                Model.Line(geometry, 0, 1),
                                Model.Line(geometry, 1, 2),
                                Model.Line(geometry, 2, 0)
                            ),
                            geometry,
                            position
                        )
                    )
                }

                it("should include all points defining a surface within modelBounds") {
                    expect(model.modelBounds)
                        .toEqual(Vector3F(-1f, 0f, -.25f) to Vector3F(1f, 1f, 5f))
                }

                it("should compute the correct center") {
                    expect(model.center)
                        .toEqual(Vector3F(0f, .5f, 2.375f))
                }

                context("with a transformation") {
                    value(position) { Vector3F.unit3d }

                    it("should include all points defining a surface within modelBounds") {
                        expect(model.modelBounds)
                            .toEqual(Vector3F(0f, 1f, .75f) to Vector3F(2f, 2f, 6f))
                    }

                    it("should compute the correct center") {
                        expect(model.center)
                            .toEqual(Vector3F(1f, 1.5f, 3.375f))
                    }
                }
            }

            context("with light bars") {
                val bar1Position by value { Vector3F.origin }
                val v4 by value { Vector3F(7f, -3f, 5.25f) }

                override(model) {
                    fakeModel(
                        LightBar("bar1", startVertex = v1, endVertex = v2, position = bar1Position),
                        LightBar("bar2", startVertex = v3, endVertex = v4)
                    )
                }

                it("should include start and end points in a light bar") {
                    expect(model.modelBounds)
                        .toEqual(Vector3F(-1f, -3f, -.25f) to Vector3F(7f, 1f, 5.25f))
                }

                it("should compute the correct center") {
                    expect(model.center)
                        .toEqual(Vector3F(3f, -1f, 2.5f))
                }

                context("with a transformation") {
                    value(bar1Position) { Vector3F.unit3d }

                    it("should include start and end points in a light bar") {
                        expect(model.modelBounds)
                            .toEqual(Vector3F(0f, -3f, -.25f) to Vector3F(7f, 2f, 6f))
                    }

                    it("should compute the correct center") {
                        expect(model.center)
                            .toEqual(Vector3F(3.5f, -.5f, 2.875f))
                    }
                }
            }

            context("with light rings") {
                override(model) {
                    fakeModel(
                        LightRing("bar1", "bar2", position = v1, radius = 1f),
                        LightRing("bar2", "bar2", position = v2, radius = 1f)
                    )
                }

                it("should include all points on both light rings") {
                    expect(model.modelBounds)
                        .toEqual(Vector3F(-2f, -1f, 0f) to Vector3F(2f, 2f, 5f))
                }

                it("should compute the correct center") {
                    expect(model.center)
                        .toEqual(Vector3F(0f, .5f, 2.5f))
                }
            }
        }
    }
})