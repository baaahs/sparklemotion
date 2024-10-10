package baaahs.glsl

import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.model.ModelInfo
import baaahs.model.PixelArray
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.random.Random

interface SurfacePixelStrategy {
    fun forFixture(pixelCount: Int, entity: Model.Entity?, model: ModelInfo): List<Vector3F> {
        return entity?.let { forKnownEntity(pixelCount, entity, model) }
            ?: forUnknownEntity(pixelCount, model)
    }

    fun forKnownEntity(pixelCount: Int, entity: Model.Entity, model: ModelInfo): List<Vector3F>
    fun forUnknownEntity(pixelCount: Int, modelInfo: ModelInfo): List<Vector3F>
}

@Serializable @SerialName("None")
object NullSurfacePixelStrategy : SurfacePixelStrategy {
    override fun forKnownEntity(pixelCount: Int, entity: Model.Entity, model: ModelInfo): List<Vector3F> =
        emptyList()

    override fun forUnknownEntity(pixelCount: Int, modelInfo: ModelInfo): List<Vector3F> =
        emptyList()
}

@Serializable @SerialName("Random")
class RandomSurfacePixelStrategy(
    @Transient private val random: Random = Random
) : SurfacePixelStrategy {
    override fun forKnownEntity(pixelCount: Int, entity: Model.Entity, model: ModelInfo): List<Vector3F> {
        // Randomly pick locations within the surface.
        val entityVertices = entity.bounds.toList()
        var lastPixelLocation = entityVertices.random()
        return (0 until pixelCount).map {
            lastPixelLocation = (lastPixelLocation + entityVertices.random(random)) / 2f
            lastPixelLocation
        }
    }

    override fun forUnknownEntity(pixelCount: Int, modelInfo: ModelInfo): List<Vector3F> {
        // Randomly pick locations. TODO: this should be based on model extents.
        val min = Vector3F(0f, 0f, 0f)
        val max = Vector3F(100f, 100f, 100f)
        val scale = max - min

        return (0 until pixelCount).map {
            Vector3F(random.nextFloat(), random.nextFloat(), random.nextFloat()) * scale + min
        }
    }
}

@Serializable @SerialName("Linear")
class LinearSurfacePixelStrategy(
    @Transient private val random: Random = Random
) : SurfacePixelStrategy {
    override fun forKnownEntity(pixelCount: Int, entity: Model.Entity, model: ModelInfo): List<Vector3F> {
        if (entity is PixelArray) {
            return entity.calculatePixelLocalLocations(pixelCount)
        }

        // Generate pixel locations along a line from one vertex to the surface's center.
        val entityVertices = entity.bounds.toList()
        if (entityVertices.isEmpty()) return forUnknownEntity(pixelCount, model)

        val surfaceCenter = entityVertices.average()
        val vertex1 = entityVertices.first()

        return interpolate(vertex1, surfaceCenter, pixelCount)
    }

    override fun forUnknownEntity(pixelCount: Int, modelInfo: ModelInfo): List<Vector3F> {
        val min = modelInfo.boundsMin
        val max = modelInfo.boundsMax

        val scale = max - min
        val vertex1 = Vector3F(random.nextFloat(), random.nextFloat(), random.nextFloat()) * scale + min
        val vertex2 = Vector3F(random.nextFloat(), random.nextFloat(), random.nextFloat()) * scale + min

        return interpolate(vertex1, vertex2, pixelCount)
    }

    fun betweenPoints(startVertex: Vector3F, endVertex: Vector3F, count: Int): List<Vector3F> {
        return interpolate(startVertex, endVertex, count)
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