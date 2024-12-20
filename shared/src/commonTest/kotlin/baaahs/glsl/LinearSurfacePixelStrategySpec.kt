package baaahs.glsl

import baaahs.TestModel
import baaahs.describe
import baaahs.geom.Vector3F
import baaahs.gl.override
import baaahs.kotest.value
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.testModelSurface
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import kotlin.random.Random

@Suppress("unused")
class LinearSurfacePixelStrategySpec : DescribeSpec({
    describe<LinearSurfacePixelStrategy> {
        val strategy by value { LinearSurfacePixelStrategy(Random(1)) }
        context("#forKnownSurface") {
            val entity by value<Model.Entity> {
                val vertices = listOf(
                    Vector3F(1f, 1f, 1f),
                    Vector3F(2f, 2f, 1f),
                    Vector3F(1f, 2f, 2f),
                    Vector3F(2f, 1f, 2f)
                )
                testModelSurface(
                    "zyx", vertices = vertices,
                    faces = listOf(Model.Face(vertices[0], vertices[1], vertices[2]))
                )
            }

            it("interpolates between vertex 0 and the surface's center") {
                strategy.forKnownEntity(3, entity, TestModel)
                    .shouldContainExactly(
                        Vector3F(1f, 1f, 1f),
                        Vector3F(1.25f, 1.25f, 1.25f),
                        Vector3F(1.5f, 1.5f, 1.5f)
                    )
            }

            context("for LinearPixelArray entities") {
                override(entity) { LightBar("", "", startVertex = Vector3F.origin, endVertex = Vector3F.unit3d) }

                it("interpolates along its entire length") {
                    strategy.forKnownEntity(3, entity, TestModel)
                        .shouldContainExactly(
                            Vector3F(0f, 0f, 0f),
                            Vector3F(.5f, .5f, .5f),
                            Vector3F(1f, 1f, 1f)
                        )
                }

            }
        }

        context("#forUnknownSurface") {
            it("interpolates between two random vertices within the model's bounds") {
                strategy.forUnknownEntity(3, TestModel)
                    .shouldContainExactly(
                        Vector3F(x = -0.36027277f, y = 0.1433261f, z = 0.37724733f),
                        Vector3F(x = -0.11624631f, y = 0.2586312f, z = 0.07177293f),
                        Vector3F(x = 0.12778014f, y = 0.3739363f, z = -0.23370147f)
                    )
            }
        }
    }
})