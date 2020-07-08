package baaahs.glsl

import baaahs.IdentifiedSurface
import baaahs.Logger
import baaahs.Surface
import baaahs.geom.Vector3F
import kotlin.random.Random

interface SurfacePixelStrategy {
    fun forSurface(surface: Surface): List<Vector3F?>
}

object RandomSurfacePixelStrategy : SurfacePixelStrategy {
    override fun forSurface(surface: Surface): List<Vector3F?> {
        return when {
            surface is IdentifiedSurface && surface.pixelLocations != null -> {
                surface.pixelLocations
            }

            surface is IdentifiedSurface -> {
                // Randomly pick locations within the surface.
                val surfaceVertices = surface.modelSurface.allVertices().toList()
                var lastPixelLocation = surfaceVertices.random()
                (0 until surface.pixelCount).map {
                    lastPixelLocation = (lastPixelLocation + surfaceVertices.random()) / 2f
                    lastPixelLocation
                }
            }

            else -> {
                // Randomly pick locations. TODO: this should be based on model extents.
                val min = Vector3F(0f, 0f, 0f)
                val max = Vector3F(100f, 100f, 100f)
                val scale = max - min

                (0 until surface.pixelCount).map {
                    Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) * scale + min
                }
            }
        }
    }
}

object LinearSurfacePixelStrategy : SurfacePixelStrategy {
    val logger = Logger("LinearSurfacePixelStrategy")

    override fun forSurface(surface: Surface): List<Vector3F?> {
        val pixelCount = surface.pixelCount

        return when {
            surface is IdentifiedSurface && surface.pixelLocations != null -> {
                logger.debug { "Surface ${surface.name} has mapped pixels."}
                surface.pixelLocations
            }

            surface is IdentifiedSurface -> {
                logger.debug { "Surface ${surface.name} doesn't have mapped pixels."}
                // Generate pixel locations along a line from one vertex to the surface's center.
                val surfaceVertices = surface.modelSurface.allVertices()
                if (surfaceVertices.isEmpty()) return emptyList()

                val surfaceCenter = surfaceVertices.average()
                val vertex1 = surfaceVertices.first()

                interpolate(vertex1, surfaceCenter, pixelCount)
            }

            else -> {
                logger.debug { "Surface ${surface.describe()} is unknown."}
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