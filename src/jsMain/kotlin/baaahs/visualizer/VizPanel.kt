package baaahs.visualizer

import baaahs.Color
import baaahs.Pixels
import baaahs.SheepModel
import baaahs.geom.Vector2
import info.laht.threekt.THREE
import info.laht.threekt.THREE.FrontSide
import info.laht.threekt.core.BufferAttribute
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.materials.PointsMaterial
import info.laht.threekt.math.Matrix4
import info.laht.threekt.math.Triangle
import info.laht.threekt.math.Vector3
import info.laht.threekt.math.minus
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.objects.Points
import info.laht.threekt.scenes.Scene
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import org.w3c.dom.get
import three.Float32BufferAttribute
import kotlin.browser.document
import kotlin.math.max
import kotlin.math.min

class VizPanel(panel: SheepModel.Panel, private val geom: Geometry, private val scene: Scene) {
    private val name = panel.name
    internal val geometry = Geometry()
    var area = 0.0f
    private var panelNormal: Vector3
    val isMultiFaced: Boolean
    internal var edgeNeighbors: Map<String, List<Face3>>
    private val lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
    internal var faceMaterial: MeshBasicMaterial
    private var mesh: Mesh
    private var lines: List<Line>
    var vizPixels: VizPixels? = null
        set(value) {
            field?.removeFromScene(scene)
            value?.addToScene(scene)

            field = value
        }

    init {
        val panelGeometry = this.geometry
        val panelVertices = panelGeometry.vertices

        val triangle = Triangle() // for computing area...

        val faceAreas = mutableListOf<Float>()
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

            val faceArea = triangle.asDynamic().getArea() as Float
            faceAreas.add(faceArea)
            this.area += faceArea

            val normal: Vector3 = document["non-existant-key"]
            Face3(localVerts[0], localVerts[1], localVerts[2], normal)
        }.toTypedArray()

        isMultiFaced = panelGeometry.faces.size > 1

        panelGeometry.computeFaceNormals()
        val faceNormalSum = Vector3()
        panelGeometry.faces.forEachIndexed { index, face ->
            val faceArea = faceAreas[index]
            faceNormalSum.addScaledVector(face.normal!!, faceArea)
        }
        panelNormal = faceNormalSum.divideScalar(area.toDouble())

        val edgeNeighbors = mutableMapOf<String, MutableList<Face3>>()
        panelGeometry.faces.forEach { face ->
            face.segments().forEach { vs ->
                val vsKey = vs.asKey()
                val neighbors = edgeNeighbors.getOrPut(vsKey) { mutableListOf() }
                neighbors.add(face)
            }
        }
        this.edgeNeighbors = edgeNeighbors

        geom.computeVertexNormals() // todo: why is this here?

        val lines = panel.lines.map { line ->
            val lineGeo = Geometry()
            lineGeo.vertices = line.points.map { pt -> Vector3(pt.x, pt.y, pt.z) }.toTypedArray()
            lineGeo
        }

        this.faceMaterial = MeshBasicMaterial().apply { color.set(0x222222) }
        this.faceMaterial.side = FrontSide
        this.faceMaterial.transparent = false

        this.mesh = Mesh(panelGeometry, this.faceMaterial)
        this.mesh.asDynamic().panel = this // so we can get back to the VizPanel from a raycaster intersection...
        scene.add(this.mesh)

        this.lines = lines.map { line -> Line(line.asDynamic(), lineMaterial) }

        this.lines.forEach { line ->
            scene.add(line)
        }
    }

    class Point2(val x: Float, val y: Float) {
        operator fun component1() = x
        operator fun component2() = y
    }


    class VizPixels(positions: Array<Vector3>) : Pixels {
        override val size = positions.size
        private val points: Points
        private val pixGeometry = BufferGeometry()
        private val colorsBufferAttr: BufferAttribute

        init {
            val positionsArray = Float32Array(size * 3)
            positions.forEachIndexed { i, v ->
                positionsArray[i * 3] = v.x.toFloat()
                positionsArray[i * 3 + 1] = v.y.toFloat()
                positionsArray[i * 3 + 2] = v.z.toFloat()
            }
            val positionsBufferAttr = Float32BufferAttribute(positionsArray, 3)
            pixGeometry.addAttribute("position", positionsBufferAttr)

            colorsBufferAttr = Float32BufferAttribute(Float32Array(size * 3), 3)
            colorsBufferAttr.dynamic = true
            pixGeometry.addAttribute("color", colorsBufferAttr)
            val material = PointsMaterial()
                .apply { size = 3; vertexColors = THREE.VertexColors }
            points = Points().apply { geometry = pixGeometry; this.material = material }
        }

        fun addToScene(scene: Scene) {
            scene.add(points)
        }

        fun removeFromScene(scene: Scene) {
            scene.remove(points)
        }

        override fun get(i: Int): Color {
            val rgbBuf = colorsBufferAttr.array
            return Color(
                rgbBuf[i * 3] as Float,
                rgbBuf[i * 3 + 1] as Float,
                rgbBuf[i * 3 + 2] as Float
            )
        }

        override fun set(i: Int, color: Color) {
            val rgbBuf = colorsBufferAttr.array
            rgbBuf[i * 3] = color.redF
            rgbBuf[i * 3 + 1] = color.greenF
            rgbBuf[i * 3 + 2] = color.blueF
            colorsBufferAttr.needsUpdate = true
        }

        override fun set(colors: Array<Color>) {
            val maxCount = min(this.size, colors.size)
            val rgbBuf = colorsBufferAttr.array
            for (i in 0 until maxCount) {
                val pColor = colors[i]
                rgbBuf[i * 3] = pColor.redF
                rgbBuf[i * 3 + 1] = pColor.greenF
                rgbBuf[i * 3 + 2] = pColor.blueF
            }
            colorsBufferAttr.needsUpdate = true
        }

        fun getPixelLocationsInPanelSpace(vizPanel: VizPanel): Array<Vector2> {
            val panelGeom = vizPanel.geometry.clone()
            val pixGeom = pixGeometry.clone()

            val straightOnNormal = Vector3(0, 0, 1)

            // Rotate to straight on.
            val rotator = Rotator(vizPanel.panelNormal, straightOnNormal)
            rotator.rotate(panelGeom)
            rotator.rotate(pixGeom)

            // Translate and scale pixels to panel space (0f..1f)
            panelGeom.computeBoundingBox()
            val boundingBox = panelGeom.boundingBox!!
            val min = boundingBox.min
            val size = boundingBox.max - boundingBox.min

            val translate = Matrix4().makeTranslation(-min.x, -min.y, -min.z)
            panelGeom.applyMatrix(translate)
            pixGeom.applyMatrix(translate)

            val scale = Matrix4().makeScale(1.0 / size.x, 1.0 / size.y, 1.0)
            panelGeom.applyMatrix(scale)
            pixGeom.applyMatrix(scale)

            val pixelVs = mutableListOf<Vector2>()
            val pixelPositions = pixGeom.getAttribute("position")
            val array = pixelPositions.array as Float32Array
            for (i in 0 until pixelPositions.count * 3 step 3) {
                val v = Vector2(clamp(array[i]).toDouble(), clamp(array[i + 1]).toDouble())
                pixelVs.add(v)
            }

            return pixelVs.toTypedArray()
        }

        fun clamp(f: Float): Float = min(1f, max(f, 0f))
    }

    fun getPixelLocations(): Array<Vector2>? {
        return vizPixels?.getPixelLocationsInPanelSpace(this)
    }
}
