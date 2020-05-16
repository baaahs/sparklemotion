package baaahs.visualizer

import baaahs.Model
import baaahs.geom.Vector2
import info.laht.threekt.THREE.FrontSide
import info.laht.threekt.core.Face3
import info.laht.threekt.core.Geometry
import info.laht.threekt.core.Object3D
import info.laht.threekt.materials.LineBasicMaterial
import info.laht.threekt.materials.MeshBasicMaterial
import info.laht.threekt.math.Triangle
import info.laht.threekt.math.Vector3
import info.laht.threekt.objects.Line
import info.laht.threekt.objects.Mesh
import info.laht.threekt.scenes.Scene
import org.w3c.dom.get
import kotlin.browser.document

class VizSurface(panel: Model.Surface, private val geom: Geometry, private val scene: Scene) {
    companion object {
        fun getFromObject(object3D: Object3D): VizSurface? =
            object3D.userData.asDynamic()["VizPanel"] as VizSurface?
    }

    val name = panel.name
    internal val geometry = Geometry()
    var area = 0.0f
    var panelNormal: Vector3
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

    fun getPixelLocationsInPanelSpace(): Array<Vector2>? {
        return vizPixels?.getPixelLocationsInPanelSpace(this)
    }

    fun getPixelLocationsInModelSpace(): Array<Vector3>? {
        return vizPixels?.getPixelLocationsInModelSpace(this)
    }
}
