package baaahs.glsl

import TestModel
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
                        Vector3F(x = 0.27945447f, y = 1.2866522f, z = 1.7544947f),
                        Vector3F(x = 0.7675074f, y = 1.5172625f, z = 1.1435459f),
                        Vector3F(x = 1.2555603f, y = 1.7478726f, z = 0.53259706f)
                    )
                ) { strategy.forUnknownSurface(3, TestModel) }
            }
        }
    }
})