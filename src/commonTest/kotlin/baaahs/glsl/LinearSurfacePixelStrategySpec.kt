package baaahs.glsl

import baaahs.TestModel
import baaahs.TestModelSurface
import baaahs.describe
import baaahs.geom.Vector3F
import ch.tutteli.atrium.api.fluent.en_GB.containsExactly
import ch.tutteli.atrium.api.verbs.expect
import org.spekframework.spek2.Spek
import kotlin.random.Random

@Suppress("unused")
object LinearSurfacePixelStrategySpec : Spek({
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
                expect(strategy.forKnownSurface(3, surfaceWithVertices, TestModel))
                    .containsExactly(
                        Vector3F(1f, 1f, 1f),
                        Vector3F(1.25f, 1.25f, 1.25f),
                        Vector3F(1.5f, 1.5f, 1.5f)
                    )
            }
        }

        context("#forUnknownSurface") {
            it("interpolates between two random vertices within the model's bounds") {
                expect(strategy.forUnknownSurface(3, TestModel))
                    .containsExactly(
                        Vector3F(x=-0.36027277f, y=0.1433261f, z=0.37724733f),
                        Vector3F(x=-0.11624631f, y=0.2586312f, z=0.07177293f),
                        Vector3F(x=0.12778014f, y=0.3739363f, z=-0.23370147f)
                    )
            }
        }
    }
})