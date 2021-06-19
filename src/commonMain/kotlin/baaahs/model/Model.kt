package baaahs.model

import baaahs.fixtures.DeviceType
import baaahs.geom.Matrix4
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.geom.center

abstract class Model : ModelInfo {
    abstract val name: String
    abstract val movingHeads: List<MovingHead>
    abstract val allSurfaces: List<Surface>
    abstract val allEntities: List<Entity>

    abstract val geomVertices: List<Vector3F>

    private val allSurfacesByName: Map<String, Entity> by lazy { allEntities.associateBy { it.name } }

    fun findEntity(name: String) =
        allSurfacesByName[name] ?: throw RuntimeException("no such model surface $name")

    private val allVertices by lazy {
        hashSetOf<Vector3F>().apply { allSurfaces.map { addAll(it.allVertices()) } }
    }

    // TODO: modelBounds et al. should be based on allEntities, not allVertices.
    private val modelBounds by lazy { boundingBox(allVertices) }
    private val modelExtents by lazy { val (min, max) = modelBounds; max - min }
    private val modelCenter by lazy { center(allVertices) }

    override val extents get() = modelExtents
    override val center: Vector3F get() = modelCenter

    interface Entity {
        val name: String
        val description: String
        val deviceType: DeviceType
    }

    interface FixtureInfo {
        val origin: Vector3F?
        val heading: Vector3F?
        val matrix: Matrix4?
    }

    /** A named surface in the geometry model. */
    open class Surface(
        override val name: String,
        override val description: String,
        override val deviceType: DeviceType,
        val expectedPixelCount: Int?,
        val faces: List<Face>,
        val lines: List<Line>
    ) : Entity {
        open fun allVertices(): Collection<Vector3F> {
            val vertices = hashSetOf<Vector3F>()
            vertices.addAll(lines.flatMap { it.vertices })
            return vertices
        }
    }

    data class Line(val vertices: List<Vector3F>)

    class Face(
        internal val allVertices: List<Vector3F>,
        val vertexA: Int,
        val vertexB: Int,
        val vertexC: Int
    ) {
        val a: Vector3F get() = allVertices[vertexA]
        val b: Vector3F get() = allVertices[vertexB]
        val c: Vector3F get() = allVertices[vertexC]

        val vertices: Array<Vector3F> = arrayOf(a, b, c)
    }
}