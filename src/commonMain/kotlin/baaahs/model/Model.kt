package baaahs.model

import baaahs.fixtures.DeviceType
import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.geom.center

abstract class Model : ModelInfo {
    abstract val name: String
    abstract val movingHeads: List<MovingHead>
    abstract val allSurfaces: List<Surface>
    val allEntities: List<Entity> get() = allSurfaces + movingHeads

    abstract val geomVertices: List<Vector3F>

    private val allSurfacesByName: Map<String, Surface> by lazy { allSurfaces.associateBy { it.name } }

    fun findSurface(name: String) =
        allSurfacesByName[name] ?: throw RuntimeException("no such model surface $name")

    val allVertices by lazy {
        val allVertices = hashSetOf<Vector3F>()
        allSurfaces.map { allVertices.addAll(it.allVertices()) }
        allVertices
    }

    val modelBounds by lazy {
        boundingBox(allVertices)
    }

    val modelExtents by lazy {
        val (min, max) = modelBounds
        max - min
    }
    override val extents
        get() = modelExtents

    val modelCenter by lazy {
        center(allVertices)
    }
    override val center: Vector3F
        get() = modelCenter

    interface Entity {
        val name: String
        val description: String
        val deviceType: DeviceType
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
        private val allVertices: List<Vector3F>,
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