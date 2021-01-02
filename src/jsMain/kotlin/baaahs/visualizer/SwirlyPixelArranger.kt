package baaahs.visualizer

import baaahs.SparkleMotion
import baaahs.geom.Vector2F
import three.js.*
import three_ext.Matrix4
import kotlin.math.*
import kotlin.random.Random

class SwirlyPixelArranger(private val pixelDensity: Float = 0.2f, private val pixelSpacing : Float = 2f) {

    fun arrangePixels(surfaceGeometry: SurfaceGeometry): Array<Vector3> = PanelArranger(surfaceGeometry).arrangePixels()

    inner class PanelArranger(surfaceGeometry: SurfaceGeometry) {
        private val pixelCount = min(SparkleMotion.MAX_PIXEL_COUNT, floor(surfaceGeometry.area * pixelDensity).toInt())
        private val panelGeometry = surfaceGeometry.geometry.clone()
        private val vertices = panelGeometry.vertices
        private val isMultiFaced = surfaceGeometry.isMultiFaced
        private val edgeNeighbors = surfaceGeometry.edgeNeighbors

        fun arrangePixels(): Array<Vector3> {
            panelGeometry.computeFaceNormals()

            val pixelsGeometry = Geometry()

            val quaternion = Quaternion()

            val panelFaces = panelGeometry.faces
            var curFace = panelFaces[0]
            var revertToNormal = curFace.normal.clone()
            val straightOnNormal = Vector3(0, 0, 1)
            quaternion.setFromUnitVectors(curFace.normal, straightOnNormal)
            val matrix = Matrix4()
            matrix.makeRotationFromQuaternion(quaternion)
            panelGeometry.applyMatrix4(matrix)
            pixelsGeometry.applyMatrix4(matrix)

            val pos = randomLocation(curFace, vertices)
            val nextPos = Vector3()

            pixelsGeometry.vertices.asDynamic().push(pos.clone())

            var tries = 1000
            var angleRad = Random.nextFloat() * 2 * PI
            var angleRadDelta = Random.nextFloat() * 0.5 - 0.5
            var pixelsSinceEdge = 0
            var pixelI = 1
            while (pixelI < pixelCount) {
                nextPos.x = pos.x + pixelSpacing * sin(angleRad)
                nextPos.y = pos.y + pixelSpacing * cos(angleRad)
                nextPos.z = pos.z

                // console.log("cur face: ", this.faceVs(curFace, panelGeometry))

                if (!isInsideFace(curFace, nextPos)) {
                    val newFace = getFaceForPoint(curFace, nextPos)
                    if (newFace != null) {
                        // console.log("moving from", curFace, "to", newFace)
                        // console.log("prior face vs:", this.faceVs(curFace, panelGeometry))

                        quaternion.setFromUnitVectors(straightOnNormal, revertToNormal)
                        matrix.makeRotationFromQuaternion(quaternion)
                        panelGeometry.applyMatrix4(matrix)
                        pixelsGeometry.applyMatrix4(matrix)
                        nextPos.applyMatrix4(matrix)

                        curFace = newFace
                        revertToNormal = curFace.normal.clone()
                        quaternion.setFromUnitVectors(curFace.normal, straightOnNormal)
                        matrix.makeRotationFromQuaternion(quaternion)
                        panelGeometry.applyMatrix4(matrix)
                        pixelsGeometry.applyMatrix4(matrix)
                        // console.log("pos was", nextPos)
                        nextPos.applyMatrix4(matrix)
                        // console.log("pos is now", nextPos)
                        // console.log("new face vs:", this.faceVs(newFace, panelGeometry))
                        nextPos.z = panelGeometry.vertices[newFace.a].z
                        if (!isInsideFace(curFace, nextPos)) {
                            // console.log(nextPos, "is not in", this.faceVs(curFace, panelGeometry))
                            nextPos.copy(randomLocation(curFace, vertices))
                        } else {
                            // console.log("AWESOME", nextPos, "is in", this.faceVs(curFace, panelGeometry))
                        }
                    } else {
                        angleRad = Random.nextFloat() * 2 * PI
                        if (tries-- < 0) break
                        pixelsSinceEdge = 0
                        continue
                    }
                }

                // console.log("pixel z = ", nextPos.z)
                pixelsGeometry.vertices.asDynamic().push(nextPos.clone())

                angleRad += angleRadDelta
                angleRadDelta *= 1 - Random.nextFloat() * 0.2 + 0.1

                // occasional disruption just in case we're in a tight loop...
                if (pixelsSinceEdge > pixelCount / 10) {
                    angleRad = Random.nextFloat() * 2 * PI
                    angleRadDelta = Random.nextFloat() * 0.5 - 0.5
                    pixelsSinceEdge = 0
                }
                pos.copy(nextPos)
                pixelsSinceEdge++

                pixelI++
            }

            quaternion.setFromUnitVectors(straightOnNormal, revertToNormal)
            matrix.makeRotationFromQuaternion(quaternion)
            panelGeometry.applyMatrix4(matrix)
            pixelsGeometry.applyMatrix4(matrix)

            return pixelsGeometry.vertices
        }


        fun randomLocation(face: Face3, vertices: Array<Vector3>): Vector3 {
            val v = Vector3().copy(vertices[face.a])
            v.addScaledVector(Vector3().copy(vertices[face.b]).sub(v), Random.nextFloat())
            v.addScaledVector(Vector3().copy(vertices[face.c]).sub(v), Random.nextFloat())
            return v
        }

        fun isInsideFace(curFace: Face3, v: Vector3): Boolean {
            val vertices = panelGeometry.vertices

            return isInside(
                xy(v),
                arrayOf(
                    xy(vertices[curFace.a]),
                    xy(vertices[curFace.b]),
                    xy(vertices[curFace.c])
                )
            )
        }

        fun isInside(point: Vector2F, vs: Array<Vector2F>): Boolean {
            // ray-casting algorithm based on
            // https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html

            val (x, y) = point

            var inside = false

            var i = 0
            var j = vs.size - 1
            while (i < vs.size) {
                val xi = vs[i].x
                val yi = vs[i].y
                val xj = vs[j].x
                val yj = vs[j].y

                val intersect = ((yi > y) != (yj > y))
                        && (x < (xj - xi) * (y - yi) / (yj - yi) + xi)
                if (intersect) {
                    inside = !inside
                }

                j = i++
            }

            return inside
        }

        fun xy(v: Vector3) = Vector2F(v.x.toFloat(), v.y.toFloat())

        // we've tried to add a pixel that's not inside curFace; figure out which face it corresponds to...
        internal fun getFaceForPoint(curFace: Face3, v: Vector3): Face3? {
            if (isMultiFaced) {
                val vertices = panelGeometry.vertices

                // find the edge closest to v...
                var closestEdge = arrayOf(-1, -1)
                var bestDistance = Float.POSITIVE_INFINITY
                curFace.segments().forEach { edgeVs ->
                    val closestPointOnEdge = Vector3()
                    val v0 = edgeVs[0]
                    val v1 = edgeVs[1]
                    Line3(vertices[v0], vertices[v1]).closestPointToPoint(v, true, closestPointOnEdge)
                    val thisDistance = closestPointOnEdge.distanceTo(v).toFloat()
                    if (thisDistance < bestDistance) {
                        closestEdge = edgeVs
                        bestDistance = thisDistance
                    }
                }

                val edgeId = closestEdge.asKey()
                // console.log("Closest edge to", v, "is", edgeId, this.edgeNeighbors[edgeId])

                val neighbors = edgeNeighbors[edgeId]
                val neighbor = neighbors?.filter { f -> f !== curFace } ?: emptyList()
                if (neighbor.size == 0) {
                    return null
                } else if (neighbor.size > 1) {
//                console.warn("Found multiple neighbors for ", this.panel.name, " edge ", edgeId, ": ", neighbors)
                }

                // console.log("Face for ", v, "is", edgeId, neighbor[0])
                return neighbor[0]
            }
            return null
        }
    }
}