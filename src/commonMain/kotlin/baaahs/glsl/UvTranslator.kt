package baaahs.glsl

import baaahs.IdentifiedSurface
import baaahs.Model
import baaahs.Surface
import baaahs.geom.Vector3F
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.random.Random

interface UvTranslator {
    fun forSurface(surface: Surface): SurfaceUvTranslator {
        return if (surface is IdentifiedSurface) {
            if (surface.pixelLocations != null) {
                forPixels(surface.pixelLocations)
            } else {
                val center = surface.modelSurface.allVertices().average()
                forPixels(listOf(center))
            }
        } else {
            forPixels(listOf(
                Vector3F(
                    Random.nextFloat() * 100f,
                    Random.nextFloat() * 100f,
                    1f
                )
            ))
        }
    }

    fun forPixels(pixelLocations: List<Vector3F?>): SurfaceUvTranslator

    interface SurfaceUvTranslator {
        val pixelCount: Int
        fun getUV(pixelIndex: Int): Pair<Float, Float>
    }
}

object PanelSpaceUvTranslator : UvTranslator {
    override fun forPixels(pixelLocations: List<Vector3F?>): UvTranslator.SurfaceUvTranslator {
        return object : UvTranslator.SurfaceUvTranslator {
            override val pixelCount: Int = pixelLocations.size

            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                val vector3F = pixelLocations[pixelIndex]
                return (vector3F?.x ?: 0f) to (vector3F?.y ?: 0f)
            }
        }
    }
}

class ModelSpaceUvTranslator(val model: Model<*>) : UvTranslator {
    val modelCenter = model.modelCenter
    val modelExtents = model.modelExtents

    override fun forPixels(pixelLocations: List<Vector3F?>): UvTranslator.SurfaceUvTranslator {
        return object : UvTranslator.SurfaceUvTranslator {
            override val pixelCount: Int = pixelLocations.size

            override fun getUV(pixelIndex: Int): Pair<Float, Float> {
                val pixelLocation = pixelLocations[pixelIndex] ?: modelCenter

                val normalDelta = pixelLocation.minus(modelCenter).normalize()
                var theta = atan2(abs(normalDelta.z), normalDelta.x) // theta in range [-π,π]
                if (theta < 0.0f) theta += (2.0f * PI.toFloat()) // theta in range [0,2π)
                val u = theta / (2.0f * PI.toFloat()) // u in range [0,1)
                val v = (pixelLocation.minus(modelCenter).y + modelExtents.y / 2.0f) / modelExtents.y
                return u to v
            }
        }
    }
}

private fun Collection<Vector3F>.average(): Vector3F {
    return reduce { acc, vector3F -> acc.plus(vector3F) }.dividedByScalar(size.toFloat())
}
