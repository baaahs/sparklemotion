package baaahs.visualizer

import baaahs.SparkleMotion
import baaahs.geom.Vector2F
import three.*
import kotlin.math.*
import kotlin.random.Random

interface PixelArranger {
    fun arrangePixels(surfaceGeometry: SurfaceGeometry, pixelCount: Int? = null): Array<Vector3>
}

class SwirlyPixelArranger(
    private val pixelDensity: Float = 0.2f,
    private val pixelSpacing: Float = 2f
) : PixelArranger {
    override fun arrangePixels(surfaceGeometry: SurfaceGeometry, pixelCount: Int?): Array<Vector3> =
        Arranger(surfaceGeometry, pixelCount).arrangePixels()

    private inner class Arranger(
        private val surfaceGeometry: SurfaceGeometry,
        pixelCount: Int? = null
    ) {
        private val pixelCount = pixelCount
            ?: min(SparkleMotion.MAX_PIXEL_COUNT, floor(surfaceGeometry.area * pixelDensity).toInt())
        private val isMultiFaced = surfaceGeometry.isMultiFaced
        private val edgeNeighbors = surfaceGeometry.edgeNeighbors

        fun arrangePixels(): Array<Vector3> {
            val panelFaces = surfaceGeometry.faceInfos
            var curFace = panelFaces[0]

            val pixelFlatPos = randomLocation(curFace.flatTriangle)
            val nextPixelFlatPos = Vector3()

            val pixelPositions = mutableListOf<Vector3>()
            pixelPositions.add(pixelFlatPos.clone())

            var tries = 1000
            var angleRad = Random.nextFloat() * 2 * PI
            var angleRadDelta = Random.nextFloat() * 0.5 - 0.5
            var pixelsSinceEdge = 0
            var pixelI = 1
            while (pixelI < pixelCount) {
                nextPixelFlatPos.x = pixelFlatPos.x + pixelSpacing * sin(angleRad)
                nextPixelFlatPos.y = pixelFlatPos.y + pixelSpacing * cos(angleRad)
                nextPixelFlatPos.z = pixelFlatPos.z

                // console.log("cur face: ", this.faceVs(curFace, panelGeometry))

                if (!isInside(curFace.flatTriangle, nextPixelFlatPos)) {
                    val nextPixelPos = curFace.backToWorld(nextPixelFlatPos.clone())
                    val newFace = getFaceForPoint(curFace, nextPixelPos)
                    if (newFace != null) {
//                        fun Vector3.asString() = "(${x.toString().substring(0, 5)}, ${y.toString().substring(0, 5)}, ${z.toString().substring(0, 5)})"
//                        console.log("moving from face ", curFace.index, "to", newFace.index, " of ", surfaceGeometry.faceInfos.size)
//                        console.log("from normal ", curFace.normal.asString(), " to ", newFace.normal.asString())

                        curFace = newFace
                        nextPixelFlatPos.copy(nextPixelPos)
                        newFace.facingForward(nextPixelFlatPos)

                        // Move it to be directly on the face.
                        nextPixelFlatPos.z = curFace.flatTriangle.a.z

                        if (!isInside(curFace.flatTriangle, nextPixelFlatPos)) {
                            // console.log(nextPos, "is not in", this.faceVs(curFace, panelGeometry))
                            nextPixelFlatPos.copy(randomLocation(curFace.flatTriangle))
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
                pixelPositions.add(curFace.backToWorld(nextPixelFlatPos.clone()))

                angleRad += angleRadDelta
                angleRadDelta *= 1 - Random.nextFloat() * 0.2 + 0.1

                // occasional disruption just in case we're in a tight loop...
                if (pixelsSinceEdge > pixelCount / 10) {
                    angleRad = Random.nextFloat() * 2 * PI
                    angleRadDelta = Random.nextFloat() * 0.5 - 0.5
                    pixelsSinceEdge = 0
                }
                pixelFlatPos.copy(nextPixelFlatPos)
                pixelsSinceEdge++

                pixelI++
            }

            curFace.backToWorld(pixelFlatPos)
            return pixelPositions.toTypedArray()
        }


        fun randomLocation(triangle: Triangle): Vector3 {
            val v = Vector3().copy(triangle.a)
            v.addScaledVector(Vector3().copy(triangle.b).sub(v), Random.nextFloat())
            v.addScaledVector(Vector3().copy(triangle.c).sub(v), Random.nextFloat())
            return v
        }

        fun isInside(triangle: Triangle, v: Vector3): Boolean {
            return isInside(
                xy(v),
                arrayOf(xy(triangle.a), xy(triangle.b), xy(triangle.c))
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
        fun getFaceForPoint(curFace: FaceInfo, v: Vector3): FaceInfo? {
            if (isMultiFaced) {
                val closestEdge = curFace.findNearestSegment(v)
                // console.log("Closest edge to", v, "is", edgeId, this.edgeNeighbors[edgeId])

                val neighbors = closestEdge
                    ?.let { edgeNeighbors.find(it) }
                    ?.filter { face -> face !== curFace }
                    ?: emptyList()

                if (neighbors.isEmpty()) {
                    return null
                } else if (neighbors.size > 1) {
//                console.warn("Found multiple neighbors for ", this.panel.name, " edge ", edgeId, ": ", neighbors)
                }

                // console.log("Face for ", v, "is", edgeId, neighbor[0])
                return neighbors[0]
            }
            return null
        }
    }
}