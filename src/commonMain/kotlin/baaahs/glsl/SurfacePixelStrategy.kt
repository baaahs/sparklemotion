package baaahs.glsl

import baaahs.IdentifiedSurface
import baaahs.Surface
import baaahs.geom.Vector3F
import kotlin.random.Random

abstract class SurfacePixelStrategy {
    abstract fun forSurface(surface: Surface): List<Vector3F?>
}

object DefaultSurfacePixelStrategy : SurfacePixelStrategy() {
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
                listOf(
                    Vector3F(Random.nextFloat() * 100f, Random.nextFloat() * 100f, 1f)
                )
            }
        }
    }

    private fun Collection<Vector3F>.average(): Vector3F {
        return reduce { acc, vector3F -> acc + vector3F } / size.toFloat()
    }
}