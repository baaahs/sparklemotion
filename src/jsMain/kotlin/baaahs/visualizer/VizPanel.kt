package baaahs.visualizer

import baaahs.Color
import baaahs.Pixels
import baaahs.SheepModel
import info.laht.threekt.THREE.FrontSide
import info.laht.threekt.THREE.VertexColors
import info.laht.threekt.core.BufferAttribute
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.*
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.scenes.Scene
import org.khronos.webgl.Float32Array
import org.khronos.webgl.set
import org.w3c.dom.get
import three.Float32BufferAttribute
import kotlin.browser.document
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin
import kotlin.random.Random

class VizPanel(val panel: SheepModel.Panel, private val geom: Geometry, private val scene: Scene) {
    private val name = panel.name
    private val geometry = Geometry()
    var area = 0.0f
    private val isMultiFaced: Boolean
    private var edgeNeighbors = mutableMapOf<String, MutableList<Face3>>()
    private val lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
    internal var faceMaterial: MeshBasicMaterial
    private var mesh: Mesh
    private var lines: List<Line>
    val pixelSpacing = 2 // inches?
    var vizPixels: VizPixels? = null

    init {
        val panelGeometry = this.geometry
        val panelVertices = panelGeometry.vertices

        val triangle = Triangle(); // for computing area...

        panelGeometry.faces = panel.faces.faces.map { face ->
            val localVerts = face.vertexIds.map { vi ->
                val v = geom.vertices[vi]
                var lvi = panelVertices.indexOf(v)
                if (lvi == -1) {
                    lvi = panelVertices.size
                    panelVertices.asDynamic().push(v)
                }
                lvi
            }

            triangle.set(
                panelVertices[localVerts[0]],
                panelVertices[localVerts[1]],
                panelVertices[localVerts[2]]
            )

            this.area += (triangle.asDynamic().getArea() as Float)

            val normal: Vector3 = document["non-existant-key"]
            Face3(localVerts[0], localVerts[1], localVerts[2], normal)
        }.toTypedArray()

        isMultiFaced = panelGeometry.faces.size > 1

        panelGeometry.faces.forEach { face ->
            face.segments().forEach { vs ->
                val vsKey = vs.asKey()
                val neighbors = this.edgeNeighbors.getOrPut(vsKey) { mutableListOf() }
                neighbors.add(face)
            }
        }

        geom.computeVertexNormals(); // todo: why is this here?

        val lines = panel.lines.map { line ->
            val lineGeo = Geometry()
            lineGeo.vertices = line.points.map { pt -> Vector3(pt.x, pt.y, pt.z) }.toTypedArray()
            lineGeo
        }

        this.faceMaterial = MeshBasicMaterial().apply { color.set(0xaa0000) }
        this.faceMaterial.side = FrontSide
        this.faceMaterial.transparent = true
        this.faceMaterial.opacity = 0.99

        this.mesh = Mesh(panelGeometry, this.faceMaterial)
        this.mesh.asDynamic().panel = this; // so we can get back to the VizPanel from a raycaster intersection...
        this.mesh.visible = false

        this.lines = lines.map { line -> Line(line.asDynamic(), lineMaterial) }

        scene.add(this.mesh)
        this.lines.forEach { line ->
            scene.add(line)
        }
    }

    fun randomLocation(face: Face3, vertices: Array<Vector3>): Vector3 {
        val v = Vector3().copy(vertices[face.a])
        v.addScaledVector(Vector3().copy(vertices[face.b]).sub(v), Random.nextFloat())
        v.addScaledVector(Vector3().copy(vertices[face.c]).sub(v), Random.nextFloat())
        return v
    }

    class Point2(val x: Float, val y: Float) {
        operator fun component1() = x
        operator fun component2() = y
    }

    fun isInsideFace(curFace: Face3, v: Vector3): Boolean {
        val vertices = this.geometry.vertices

        return isInside(
            xy(v),
            arrayOf(
                xy(vertices[curFace.a]),
                xy(vertices[curFace.b]),
                xy(vertices[curFace.c])
            )
        )
    }

    companion object {
        fun isInside(point: Point2, vs: Array<Point2>): Boolean {
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

        fun xy(v: Vector3) = Point2(v.x.toFloat(), v.y.toFloat())
    }

    // we've tried to add a pixel that's not inside curFace; figure out which face it corresponds to...
    private fun getFaceForPoint(curFace: Face3, v: Vector3): Face3? {
        if (this.isMultiFaced) {
            val vertices = this.geometry.vertices

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

            val neighbors = this.edgeNeighbors[edgeId]
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

    fun setPanelColor(panelBgColor: Color, pixelColors: Array<Color>?) {
        this.mesh.visible = true

        if (vizPixels == null || pixelColors == null) {
            this.faceMaterial.color.set(panelBgColor.rgb)
        } else {
            this.faceMaterial.color.r = .3
            this.faceMaterial.color.g = .3
            this.faceMaterial.color.b = .3

            vizPixels?.set(pixelColors)
        }
    }

    class VizPixels(
        override val count: Int,
        private val colorsBufferAttr: BufferAttribute,
        private val geometry: BufferGeometry
    ) : Pixels {
        override fun set(colors: Array<Color>) {
            val maxCount = min(count, colors.size)
            val rgbBuf = colorsBufferAttr.array
            for (i in 0 until maxCount) {
                val pColor = colors[i]
                rgbBuf[i * 3] = pColor.redF
                rgbBuf[i * 3 + 1] = pColor.greenF
                rgbBuf[i * 3 + 2] = pColor.blueF
            }
            this.colorsBufferAttr.needsUpdate = true
        }
    }

    inner class SwirlyPixelArranger {
        fun arrangePixels(pixelCount: Int): VizPixels {
            val panelGeometry = geometry
            val vertices = panelGeometry.vertices
            panelGeometry.computeFaceNormals()
            val pixelsGeometry = Geometry()

            val quaternion = Quaternion()

            val panelFaces = panelGeometry.faces
            var curFace = panelFaces[0]
            var revertToNormal = curFace.normal!!.clone()
            val straightOnNormal = Vector3(0, 0, 1)
            quaternion.setFromUnitVectors(curFace.normal!!, straightOnNormal)
            val matrix = Matrix4()
            matrix.makeRotationFromQuaternion(quaternion)
            panelGeometry.applyMatrix(matrix)
            pixelsGeometry.applyMatrix(matrix)

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
                        panelGeometry.applyMatrix(matrix)
                        pixelsGeometry.applyMatrix(matrix)
                        nextPos.applyMatrix4(matrix)

                        curFace = newFace
                        revertToNormal = curFace.normal!!.clone()
                        quaternion.setFromUnitVectors(curFace.normal!!, straightOnNormal)
                        matrix.makeRotationFromQuaternion(quaternion)
                        panelGeometry.applyMatrix(matrix)
                        pixelsGeometry.applyMatrix(matrix)
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

                pixelI++;
            }

            quaternion.setFromUnitVectors(straightOnNormal, revertToNormal)
            matrix.makeRotationFromQuaternion(quaternion)
            panelGeometry.applyMatrix(matrix)
            pixelsGeometry.applyMatrix(matrix)

            val pixBufGeometry = BufferGeometry()
            val positions = Float32Array(pixelCount * 3)
            pixelsGeometry.vertices.forEachIndexed { i, v ->
                positions[i * 3] = v.x.toFloat()
                positions[i * 3 + 1] = v.y.toFloat()
                positions[i * 3 + 2] = v.z.toFloat()
            }
            val positionsBufferAttr = Float32BufferAttribute(positions, 3)
            pixBufGeometry.addAttribute("position", positionsBufferAttr)

            val colorsBufferAttr = Float32BufferAttribute(Float32Array(pixelCount * 3), 3)
            colorsBufferAttr.dynamic = true
            pixBufGeometry.addAttribute("color", colorsBufferAttr)
            val material = PointsMaterial().apply { size = 3; vertexColors = VertexColors }
            val points = Points().apply { geometry = pixBufGeometry; this.material = material }
            scene.add(points)

            return VizPixels(pixelCount, colorsBufferAttr, pixBufGeometry)
        }
    }

    private fun Face3.segments() = arrayOf(arrayOf(a, b), arrayOf(b, c), arrayOf(c, a))
    private fun Array<Int>.asKey() = sorted().joinToString("-")
}
