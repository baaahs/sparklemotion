package baaahs.model

import baaahs.geom.Vector3F
import baaahs.geom.boundingBox
import baaahs.geom.center

abstract class Model<T : Model.Surface>: ModelInfo {
    abstract val name: String
    abstract val movingHeads: List<MovingHead>
    abstract val allSurfaces: List<T>
    abstract val geomVertices: List<Vector3F>

    private val allSurfacesByName: Map<String, T> by lazy { allSurfaces.associateBy { it.name } }

    fun findModelSurface(name: String) =
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

    /** A named surface in the geometry model. */
    interface Surface {
        val name: String
        val description: String
        val expectedPixelCount: Int?
        fun allVertices(): Collection<Vector3F>
        val faces: List<Face>
        val lines: List<Line>
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