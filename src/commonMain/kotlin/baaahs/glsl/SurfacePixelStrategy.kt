package baaahs.glsl

import baaahs.Logger
import baaahs.fixtures.Fixture
import baaahs.fixtures.IdentifiedFixture
import baaahs.geom.Vector3F
import kotlin.random.Random

interface SurfacePixelStrategy {
    fun forFixture(fixture: Fixture): List<Vector3F?>
}

object RandomSurfacePixelStrategy : SurfacePixelStrategy {
    override fun forFixture(fixture: Fixture): List<Vector3F?> {
        return when {
            fixture is IdentifiedFixture && fixture.pixelLocations != null -> {
                fixture.pixelLocations
            }

            fixture is IdentifiedFixture -> {
                // Randomly pick locations within the surface.
                val surfaceVertices = fixture.modelSurface.allVertices().toList()
                var lastPixelLocation = surfaceVertices.random()
                (0 until fixture.pixelCount).map {
                    lastPixelLocation = (lastPixelLocation + surfaceVertices.random()) / 2f
                    lastPixelLocation
                }
            }

            else -> {
                // Randomly pick locations. TODO: this should be based on model extents.
                val min = Vector3F(0f, 0f, 0f)
                val max = Vector3F(100f, 100f, 100f)
                val scale = max - min

                (0 until fixture.pixelCount).map {
                    Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) * scale + min
                }
            }
        }
    }
}

object LinearSurfacePixelStrategy : SurfacePixelStrategy {
    val logger = Logger("LinearSurfacePixelStrategy")

    override fun forFixture(fixture: Fixture): List<Vector3F?> {
        val pixelCount = fixture.pixelCount

        return when {
            fixture is IdentifiedFixture && fixture.pixelLocations != null -> {
                logger.debug { "Surface ${fixture.name} has mapped pixels."}
                fixture.pixelLocations
            }

            fixture is IdentifiedFixture -> {
                logger.debug { "Surface ${fixture.name} doesn't have mapped pixels."}
                // Generate pixel locations along a line from one vertex to the surface's center.
                val surfaceVertices = fixture.modelSurface.allVertices()
                if (surfaceVertices.isEmpty()) return emptyList()

                val surfaceCenter = surfaceVertices.average()
                val vertex1 = surfaceVertices.first()

                interpolate(vertex1, surfaceCenter, pixelCount)
            }

            else -> {
                logger.debug { "Surface ${fixture.describe()} is unknown."}
                // Randomly pick locations. TODO: this should be based on model extents.
                val min = Vector3F(0f, 0f, 0f)
                val max = Vector3F(100f, 100f, 100f)
                val scale = max - min
                val vertex1 = Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) * scale + min
                val vertex2 = Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) * scale + min

                interpolate(vertex1, vertex2, pixelCount)
            }
        }
    }

    private fun Collection<Vector3F>.average(): Vector3F {
        if (isEmpty()) return Vector3F.origin

        return reduce { acc, vector3F -> acc + vector3F } / size.toFloat()
    }

    private fun interpolate(from: Vector3F, to: Vector3F, steps: Int): List<Vector3F> {
        return if (steps == 1) {
            listOf(from)
        } else {
            (0 until steps).map { interpolate(from, to, it / (steps - 1f)) }
        }
    }

    private fun interpolate(from: Vector3F, to: Vector3F, degree: Float): Vector3F {
        val delta = to - from
        return from + delta * degree
    }
}