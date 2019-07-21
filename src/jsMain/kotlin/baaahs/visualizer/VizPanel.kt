package baaahs.visualizer

import baaahs.Color
import baaahs.Pixels
import baaahs.SheepModel
import baaahs.geom.Vector2
import info.laht.threekt.THREE.AdditiveBlending
import info.laht.threekt.THREE.FrontSide
import info.laht.threekt.THREE.VertexColors
import info.laht.threekt.core.BufferGeometry
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.geometries.PlaneBufferGeometry
import info.laht.threekt.loaders.TextureLoader
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.*
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.scenes.Scene
import org.khronos.webgl.Float32Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import org.w3c.dom.get
import three.BufferGeometryUtils
import three.Float32BufferAttribute
import kotlin.browser.document
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class VizPanel(panel: SheepModel.Panel, geom: Geometry, private val scene: Scene) {
    companion object {
        private val roundLightTx = TextureLoader().load(
            "./visualizer/textures/round.png",
            { println("loaded!") },
            { println("progress!") },
            { println("error!") }
        )
    }

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
                val v = geom.vertices[vi].clone()
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

        this.faceMaterial = MeshBasicMaterial().apply { color.set(0x222222) }
        this.faceMaterial.side = FrontSide
        this.faceMaterial.transparent = false

        this.mesh = Mesh(panelGeometry, this.faceMaterial)
        this.mesh.asDynamic().panel = this // so we can get back to the VizPanel from a raycaster intersection...
        scene.add(this.mesh)

        this.lines = panel.lines.map { line ->
            val lineGeom = Geometry()
            lineGeom.vertices = line.points.map { pt -> Vector3(pt.x, pt.y, pt.z) }.toTypedArray()
            Line(lineGeom.asDynamic(), lineMaterial).also { scene.add(it) }
        }
    }

    class Point2(val x: Float, val y: Float) {
        operator fun component1() = x
        operator fun component2() = y
    }


    class VizPixels(vizPanel: VizPanel, positions: Array<Vector3>) : Pixels {
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

    fun reorient(panelLeft: Int) {
//        console.log("vertices", name, geometry.vertices)

        // rotate to face forward
        val rotator = Rotator(panelNormal, Vector3(0, 0, 1))
        rotator.rotate(geometry)
        lines.forEach { rotator.rotate(it.geometry) }
//        console.log("normalized vertices", name, geometry.vertices)

        val line3s = lines.mapIndexed { i, line ->
            val geom = line.geometry as Geometry
            Line3(geom.vertices[0], geom.vertices[1])
        }
//        console.log("line3s", line3s);

        var longestEdgeIndex = 0
        line3s.mapIndexed { i, line3 ->
            line3.distance()
        }.reduceIndexed { index, acc, d ->
            if (d > acc) longestEdgeIndex = index; d
        }

        val longestEdge = line3s[longestEdgeIndex]
//        console.log("longestEdge", longestEdge);

        // move so start of longest edge is at (0,0,0)
        val offset = longestEdge.start
        geometry.translate(-offset.x, -offset.y, -offset.z)
//        console.log("lines:" , lines.map { (it.geometry as Geometry).vertices }.toTypedArray())
//        lines.forEach { it.geometry.translate(-offset.x, -offset.y, -offset.z) }
//        console.log("at 0,0,0", name, geometry.vertices)

        // rotate so longest edge is flat at grade
        val delta = longestEdge.end.clone().sub(longestEdge.start)
        val angle2 = atan2(delta.y, delta.x)
        val matrix = Matrix4().makeRotationZ(-angle2)
        geometry.applyMatrix(matrix)
//        lines.forEach { it.applyMatrix(matrix) }
//        console.log("rotated again", geometry.vertices)

        // move so bounding box min is at (0,0,0)
        geometry.computeBoundingBox()
        val min = geometry.boundingBox!!.min
        val max = geometry.boundingBox!!.max
        geometry.translate(-min.x/* + panelLeft * 100.0*/, -max.y, -min.z)
//        lines.forEach { it.geometry.translate(-min.x + panelLeft * 100.0, -min.y, -min.z) }
//        console.log("zero", name, geometry.vertices)

        geometry.computeBoundingBox()
        val dist = geometry.boundingBox!!.max.clone().sub(geometry.boundingBox!!.min)
        val x = abs(dist.x)
        val y = abs(dist.y)
        console.log(name, max(x, y), min(x, y))
    }
}
