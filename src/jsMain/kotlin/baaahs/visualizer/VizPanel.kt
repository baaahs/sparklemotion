package baaahs.visualizer

import baaahs.Color
import baaahs.Model
import baaahs.Pixels
import baaahs.geom.Vector2
import info.laht.threekt.THREE.AdditiveBlending
import info.laht.threekt.THREE.FrontSide
import info.laht.threekt.THREE.VertexColors
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.loaders.TextureLoader
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Triangle
import info.laht.threekt.math.Vector3
import info.laht.threekt.math.minus
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.scenes.Scene
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import org.w3c.dom.get
import three.BufferGeometryUtils
import three.Float32BufferAttribute
import three.Matrix4
import kotlin.browser.document
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class VizPanel(panel: Model.Surface, private val geom: Geometry, private val scene: Scene) {
    companion object {
        private val roundLightTx = TextureLoader().load(
            "./visualizer/textures/round.png",
            { println("loaded!") },
            { println("progress!") },
            { println("error!") }
        )

        fun getFromObject(object3D: Object3D): VizPanel? =
            object3D.userData.asDynamic()["VizPanel"] as VizPanel?
    }

    val name = panel.name
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
        panelGeometry.faces = panel.faces.map { face ->
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
            lineGeo.vertices = line.vertices.map { pt -> Vector3(pt.x, pt.y, pt.z) }.toTypedArray()
            lineGeo
        }

        this.faceMaterial = MeshBasicMaterial().apply { color.set(0x222222) }
        this.faceMaterial.side = FrontSide
        this.faceMaterial.transparent = false

        this.mesh = Mesh(panelGeometry, this.faceMaterial)
        mesh.asDynamic().name = "Surface: $name"

        // so we can get back to the VizPanel from a raycaster intersection:
        this.mesh.userData.asDynamic()["VizPanel"] = this

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


    class VizPixels(vizPanel: VizPanel, val positions: Array<Vector3>) : Pixels {
        override val size = positions.size
        private val pixGeometry = BufferGeometry()
        private val planeGeometry: BufferGeometry
        private val vertexColorBufferAttr: Float32BufferAttribute
        private val colorsAsInts = IntArray(size) // store colors as an int array too for Pixels.get()

        init {
            val positionsArray = Float32Array(size * 3)
            positions.forEachIndexed { i, v ->
                positionsArray[i * 3] = v.x.toFloat()
                positionsArray[i * 3 + 1] = v.y.toFloat()
                positionsArray[i * 3 + 2] = v.z.toFloat()
            }

            val positionsBufferAttr = Float32BufferAttribute(positionsArray, 3)
            pixGeometry.addAttribute("position", positionsBufferAttr)

            vertexColorBufferAttr = Float32BufferAttribute(Float32Array(size * 3 * 4), 3)
            vertexColorBufferAttr.dynamic = true

            val rotator = Rotator(Vector3(0, 0, 1), vizPanel.panelNormal)
            planeGeometry = BufferGeometryUtils.mergeBufferGeometries(positions.map { position ->
                val geometry = PlaneBufferGeometry(2 + Random.nextFloat() * 8, 2 + Random.nextFloat() * 8)
                rotator.rotate(geometry)
                geometry.translate(position.x, position.y, position.z)
                geometry
            }.toTypedArray())
            planeGeometry.addAttribute("color", vertexColorBufferAttr)
        }

        private val pixelsMesh = Mesh(planeGeometry, MeshBasicMaterial().apply {
            side = FrontSide
            transparent = true
            blending = AdditiveBlending
//            depthFunc = AlwaysDepth
            depthTest = false
            depthWrite = false
            vertexColors = VertexColors

            map = roundLightTx
        })

        fun addToScene(scene: Scene) {
            scene.add(pixelsMesh)
        }

        fun removeFromScene(scene: Scene) {
            scene.remove(pixelsMesh)
        }

        override fun get(i: Int): Color {
            return Color(colorsAsInts[i])
        }

        override fun set(i: Int, color: Color) {
            colorsAsInts[i] = color.argb

            val redF = color.redF / 2
            val greenF = color.greenF / 2
            val blueF = color.blueF / 2

            val rgb3Buf = vertexColorBufferAttr
            rgb3Buf.setXYZ(i * 4, redF, greenF, blueF)
            rgb3Buf.setXYZ(i * 4 + 1, redF, greenF, blueF)
            rgb3Buf.setXYZ(i * 4 + 2, redF, greenF, blueF)
            rgb3Buf.setXYZ(i * 4 + 3, redF, greenF, blueF)
            vertexColorBufferAttr.needsUpdate = true
        }

        override fun set(colors: Array<Color>) {
            val maxCount = min(this.size, colors.size)
            val rgbBuf = vertexColorBufferAttr.array
            for (i in 0 until maxCount) {
                colorsAsInts[i] = colors[i].argb

                val pColor = colors[i]
                rgbBuf[i * 3] = pColor.redF / 2
                rgbBuf[i * 3 + 1] = pColor.greenF / 2
                rgbBuf[i * 3 + 2] = pColor.blueF / 2
            }
            vertexColorBufferAttr.needsUpdate = true
        }

        fun getPixelLocationsInModelSpace(vizPanel: VizPanel): Array<Vector3> = positions

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

    fun getPixelLocationsInPanelSpace(): Array<Vector2>? {
        return vizPixels?.getPixelLocationsInPanelSpace(this)
    }

    fun getPixelLocationsInModelSpace(): Array<Vector3>? {
        return vizPixels?.getPixelLocationsInModelSpace(this)
    }
}
