package baaahs.visualizer

import baaahs.geom.Vector2
import baaahs.model.Model
import three.js.*

class SurfaceGeometry(surface: Model.Surface) {
    val name = surface.name
    internal val geometry = Geometry()
    var area = 0.0f
    var panelNormal: Vector3
    val isMultiFaced: Boolean
    internal var edgeNeighbors: Map<String, List<Face3>>
    var lines: List<Geometry>

    init {
        val panelGeometry = this.geometry
        val panelVertices = mutableListOf<Vector3>()

        val faceAreas = mutableListOf<Float>()
        panelGeometry.faces = surface.faces.map { face ->
            val localVerts = face.vertices.map { v -> panelVertices.findOrAdd(v.toVector3()) }

            val faceArea = Triangle(
                face.a.toVector3(), face.b.toVector3(), face.c.toVector3()
            ).getArea().toFloat()
            faceAreas.add(faceArea)
            this.area += faceArea

            Face3(localVerts[0], localVerts[1], localVerts[2], Vector3(), three.js.Color(1, 1, 1), 0)
        }.toTypedArray()
        panelGeometry.vertices = panelVertices.toTypedArray()

        isMultiFaced = panelGeometry.faces.size > 1

        panelGeometry.computeFaceNormals()
        val faceNormalSum = Vector3()
        panelGeometry.faces.forEachIndexed { index, face ->
            val faceArea = faceAreas[index]
            faceNormalSum.addScaledVector(face.normal, faceArea.toDouble())
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

        lines = surface.lines.map { line ->
            val lineGeo = Geometry()
            lineGeo.vertices = line.vertices.map { pt -> Vector3(pt.x, pt.y, pt.z) }.toTypedArray()
            lineGeo
        }
    }
}

class VizSurface(val surfaceGeometry: SurfaceGeometry, val scene: Scene) {
    val name: String get() = surfaceGeometry.name
    private val lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
    var faceMaterial: Material = MeshBasicMaterial().apply { color.set(0x222222) }
        set(value) {
            mesh.material = value
            field = value
        }
    private val mesh = Mesh(surfaceGeometry.geometry, this.faceMaterial)
    private val lines: List<Line<*, *>>
    val panelNormal: Vector3 get() = surfaceGeometry.panelNormal
    val geometry: Geometry get() = surfaceGeometry.geometry

    var vizPixels: VizPixels? = null
        set(value) {
            field?.removeFromScene(scene)
            value?.addToScene(scene)

            field = value
        }

    init {
        this.faceMaterial.side = FrontSide
        this.faceMaterial.transparent = false

        mesh.asDynamic().name = "Surface: ${surfaceGeometry.name}"

        // so we can get back to the VizPanel from a raycaster intersection:
        this.mesh.userData.asDynamic()["VizPanel"] = this

        scene.add(this.mesh)

        this.lines = surfaceGeometry.lines.map { line -> Line(line, lineMaterial) }

        this.lines.forEach { line ->
            scene.add(line)
        }
    }

    fun getPixelLocationsInPanelSpace(): Array<Vector2>? {
        return vizPixels?.getPixelLocationsInPanelSpace(this)
    }

    fun getPixelLocationsInModelSpace(): Array<Vector3>? {
        return vizPixels?.getPixelLocationsInModelSpace(this)
    }

    companion object {
        fun getFromObject(object3D: Object3D): VizSurface? =
            object3D.userData.asDynamic()["VizPanel"] as VizSurface?
    }

}