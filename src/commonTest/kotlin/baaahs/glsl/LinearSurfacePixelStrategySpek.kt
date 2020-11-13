package baaahs.glsl

import baaahs.TestModel
import baaahs.TestModelSurface
import baaahs.describe
import baaahs.geom.Vector3F
import org.spekframework.spek2.Spek
import kotlin.random.Random
import kotlin.test.expect

@Suppress("unused")
object LinearSurfacePixelStrategySpek : Spek({
    describe<LinearSurfacePixelStrategy> {
        val strategy by value { LinearSurfacePixelStrategy(Random(1)) }
        context("#forKnownSurface") {
            val surfaceWithVertices by value {
                TestModelSurface(
                    "zyx", vertices = listOf(
                        Vector3F(1f, 1f, 1f),
                        Vector3F(2f, 2f, 1f),
                        Vector3F(1f, 2f, 2f),
                        Vector3F(2f, 1f, 2f)
                    )
                )
            }

            it("interpolates between vertex 0 and the surface's center") {
                expect(
                    listOf(
                        Vector3F(1f, 1f, 1f),
                        Vector3F(1.25f, 1.25f, 1.25f),
                        Vector3F(1.5f, 1.5f, 1.5f)
                    )
                ) { strategy.forKnownSurface(3, surfaceWithVertices, TestModel) }
            }
        }

        context("#forUnknownSurface") {
            it("interpolates between vertex 0 and the surface's center") {
                expect(
                    listOf(
                        Vector3F(x=0.13972723f, y=0.6433261f, z=0.87724733f),
                        Vector3F(x=0.3837537f, y=0.7586312f, z=0.57177293f),
                        Vector3F(x=0.62778014f, y=0.8739363f, z=0.26629853f)
                    )
                ) { strategy.forUnknownSurface(3, TestModel) }
            }
        }
    }
})