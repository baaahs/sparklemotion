package baaahs.glsl

import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.model.ModelInfo
import kotlin.random.Random

interface SurfacePixelStrategy {
    fun forFixture(pixelCount: Int, surface: Model.Surface?, model: ModelInfo): List<Vector3F> {
        return if (surface != null) {
            forKnownSurface(pixelCount, surface, model)
        } else {
            forUnknownSurface(pixelCount, model)
        }
    }

    fun forKnownSurface(pixelCount: Int, surface: Model.Surface, model: ModelInfo): List<Vector3F>
    fun forUnknownSurface(pixelCount: Int, modelInfo: ModelInfo): List<Vector3F>
}

class RandomSurfacePixelStrategy(private val random: Random = Random) : SurfacePixelStrategy {
    override fun forKnownSurface(pixelCount: Int, surface: Model.Surface, model: ModelInfo): List<Vector3F> {
        // Randomly pick locations within the surface.
        val surfaceVertices = surface.allVertices().toList()
        var lastPixelLocation = surfaceVertices.random()
        return (0 until pixelCount).map {
            lastPixelLocation = (lastPixelLocation + surfaceVertices.random(random)) / 2f
            lastPixelLocation
        }
    }

    override fun forUnknownSurface(pixelCount: Int, modelInfo: ModelInfo): List<Vector3F> {
        // Randomly pick locations. TODO: this should be based on model extents.
        val min = Vector3F(0f, 0f, 0f)
        val max = Vector3F(100f, 100f, 100f)
        val scale = max - min

        return (0 until pixelCount).map {
            Vector3F(random.nextFloat(), random.nextFloat(), random.nextFloat()) * scale + min
        }
    }
}

class LinearSurfacePixelStrategy(private val random: Random = Random) : SurfacePixelStrategy {
    override fun forKnownSurface(pixelCount: Int, surface: Model.Surface, model: ModelInfo): List<Vector3F> {
        // Generate pixel locations along a line from one vertex to the surface's center.
        val surfaceVertices = surface.allVertices()
        if (surfaceVertices.isEmpty()) return forUnknownSurface(pixelCount, model)

        val surfaceCenter = surfaceVertices.average()
        val vertex1 = surfaceVertices.first()

        return interpolate(vertex1, surfaceCenter, pixelCount)
    }

    override fun forUnknownSurface(pixelCount: Int, modelInfo: ModelInfo): List<Vector3F> {
        val min = modelInfo.boundsMin
        val max = modelInfo.boundsMax

        val scale = max - min
        val vertex1 = Vector3F(random.nextFloat(), random.nextFloat(), random.nextFloat()) * scale + min
        val vertex2 = Vector3F(random.nextFloat(), random.nextFloat(), random.nextFloat()) * scale + min

        return interpolate(vertex1, vertex2, pixelCount)
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