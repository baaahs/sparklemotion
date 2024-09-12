package baaahs.visualizer

import baaahs.geom.Vector3F
import baaahs.model.Model
import three.js.*
import three_ext.vector3FacingForward

class SurfaceGeometry(val surface: Model.Surface) {
    val name = surface.name
    internal val geometry = BufferGeometry<NormalOrGLBufferAttributes>()
    internal val faceInfos: List<FaceInfo>
    val area: Double
    var panelNormal: Vector3
    val isMultiFaced: Boolean
    internal val edgeNeighbors: EdgeNeighbors
    val lines = surface.lines

    init {
        val panelGeometry = this.geometry

        val vertexIds = mutableMapOf<Vector3F, Int>()
        faceInfos = surface.faces.mapIndexed { index, face ->
            // Make sure we have a unique id for each vertex.
            val vertexIds = face.vertices.map { vertex ->
                vertexIds.getOrPut(vertex) { vertexIds.size }
            }

            FaceInfo(
                index,
                face.a.toVector3(), face.b.toVector3(), face.c.toVector3(),
                vertexIds[0], vertexIds[1], vertexIds[2]
            )
        }

        panelGeometry.setFromPoints(faceInfos.flatMap { it.allVertices }.toTypedArray())
        panelGeometry.computeVertexNormals()

        area = faceInfos.sumOf { it.area }
        isMultiFaced = faceInfos.size > 1

        panelNormal = faceInfos.fold(Vector3()) { acc, faceInfo ->
            acc.addScaledVector(faceInfo.normal, faceInfo.area)
        }.divideScalar(area)

        this.edgeNeighbors = EdgeNeighbors(faceInfos)
    }
}

class FaceInfo(
    val index: Int,
    a: Vector3,
    b: Vector3,
    c: Vector3,
    aId: Int,
    bId: Int,
    cId: Int
) {
    val triangle = Triangle(a, b, c)
    val area = triangle.getArea()
    val normal = triangle.getNormal(Vector3())
    val allVertices = listOf(a, b, c)
    val segments = arrayOf(
        LineSegment(a, b, aId, bId),
        LineSegment(b, c, bId, cId),
        LineSegment(c, a, cId, aId)
    )

    private val rotateToFlatMatrix = run {
        quaternion.setFromUnitVectors(normal, vector3FacingForward)
        Matrix4().makeRotationFromQuaternion(quaternion)
    }

    private val rotateFromFlatMatrix = run {
        quaternion.setFromUnitVectors(vector3FacingForward, normal)
        Matrix4().makeRotationFromQuaternion(quaternion)
    }

    val flatTriangle = Triangle(
        a.clone().applyMatrix4(rotateToFlatMatrix),
        b.clone().applyMatrix4(rotateToFlatMatrix),
        c.clone().applyMatrix4(rotateToFlatMatrix)
    )

    /** Mutates and returns input vector. */
    fun facingForward(point: Vector3): Vector3 =
        point.applyMatrix4(rotateToFlatMatrix)

    /** Mutates and returns input vector. */
    fun backToWorld(point: Vector3): Vector3 =
        point.applyMatrix4(rotateFromFlatMatrix)

    /** Finds the edge closest to v. */
    fun findNearestSegment(v: Vector3): LineSegment? {
        var closestEdge: LineSegment? = null
        var bestDistance = Float.POSITIVE_INFINITY
        segments.forEach { edgeSegment ->
            val closestPointOnEdge = Vector3()
            edgeSegment.line.closestPointToPoint(v, true, closestPointOnEdge)
            val thisDistance = closestPointOnEdge.distanceTo(v).toFloat()
            if (thisDistance < bestDistance) {
                closestEdge = edgeSegment
                bestDistance = thisDistance
            }
        }
        return closestEdge
    }

    class LineSegment(
        val start: Vector3, val end: Vector3,
        val startId: Int, val endId: Int
    ) {
        val line = Line3(start, end)
        val key = arrayOf(startId, endId).sorted().joinToString("-")
    }

    companion object {
        private val quaternion = Quaternion()
    }
}

class EdgeNeighbors(faceInfos: List<FaceInfo>) {
    val byLineSegment: Map<String, List<FaceInfo>> = run {
        mutableMapOf<String, MutableList<FaceInfo>>().also { map ->
            faceInfos.forEach { faceInfo ->
                faceInfo.segments.forEach { segment ->
                    map.getOrPut(segment.key) { mutableListOf() }
                        .add(faceInfo)
                }
            }
        }
    }

    fun find(segment: FaceInfo.LineSegment): List<FaceInfo> =
        byLineSegment[segment.key] ?: emptyList()
}