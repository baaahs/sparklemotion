package baaahs.visualizer.geometry

import baaahs.geom.Vector3F
import baaahs.model.Model
import baaahs.visualizer.toVector3
import three.BufferGeometry
import three.NormalOrGLBufferAttributes
import three.Vector3

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