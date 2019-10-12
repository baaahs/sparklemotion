package baaahs.glsl

import baaahs.IdentifiedSurface
import baaahs.Surface
import baaahs.geom.Vector3F
import kotlin.random.Random

abstract class SurfacePixelStrategy {
    abstract fun forSurface(surface: Surface): List<Vector3F?>
}

object RandomSurfacePixelStrategy : SurfacePixelStrategy() {
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
                // Randomly pick locations.
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

object LinearSurfacePixelStrategy : SurfacePixelStrategy() {
    override fun forSurface(surface: Surface): List<Vector3F?> {
        val pixelCount = surface.pixelCount

        return when {
            surface is IdentifiedSurface && surface.pixelLocations != null -> {
                surface.pixelLocations
            }

            surface is IdentifiedSurface -> {
                // Generate pixel locations along a line from one vertex to the surface's center.
                val surfaceVertices = surface.modelSurface.allVertices()
                val surfaceCenter = surfaceVertices.average()
                val vertex1 = surfaceVertices.first()

                (0 until pixelCount).map {
                    interpolate(vertex1, surfaceCenter, it.toFloat() / pixelCount)
                }
            }

            else -> {
                // Randomly pick locations.
                val min = Vector3F(0f, 0f, 0f)
                val max = Vector3F(100f, 100f, 100f)
                val scale = max - min
                val vertex1 = Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) * scale + min
                val vertex2 = Vector3F(Random.nextFloat(), Random.nextFloat(), Random.nextFloat()) * scale + min

                (0 until pixelCount).map {
                    interpolate(vertex1, vertex2, it.toFloat() / pixelCount)
                }
            }
        }
    }

    private fun Collection<Vector3F>.average(): Vector3F {
        return reduce { acc, vector3F -> acc + vector3F } / size.toFloat()
    }

    private fun interpolate(from: Vector3F, to: Vector3F, degree: Float): Vector3F {
        val delta = to - from
        return from + delta * degree
    }
}