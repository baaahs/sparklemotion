package baaahs.visualizer

import baaahs.model.Model
import three.js.*

class SurfaceVisualizer(
    private val surface: Model.Surface,
    val surfaceGeometry: SurfaceGeometry,
    vizPixels: VizPixels? = null
) : BaseEntityVisualizer<Model.Surface>(surface) {
    private val mesh = Mesh(surfaceGeometry.geometry, MeshBasicMaterial()).apply {
        name = "Surface: ${surfaceGeometry.name}"
    }

    private val lineMaterial = LineDashedMaterial()
    private val lines: List<Line<*, LineDashedMaterial>> = surfaceGeometry.lines.map { line ->
        val lineGeo = Geometry()
        lineGeo.vertices = line.vertices.map { pt -> pt.toVector3() }.toTypedArray()
        Line(lineGeo, lineMaterial).apply {
            matrixAutoUpdate = false
            entityVisualizer = this@SurfaceVisualizer
            mesh.add(this)
        }
    }

    val panelNormal: Vector3 get() = surfaceGeometry.panelNormal
    val geometry: Geometry get() = surfaceGeometry.geometry
    var vizPixels: VizPixels? = vizPixels
        set(value) {
            field?.removeFrom(obj)
            value?.addTo(obj)

            field = value
        }

    override val obj = Group().apply {
        println("New SurfaceVisualizer for ${entity.title}: vizPixels=$vizPixels")
        add(mesh)
        vizPixels?.addTo(this)
    }

    init { update(entity) }

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToMesh(mesh.material)
        entityStyle.applyToLine(lineMaterial)
    }

    override fun isApplicable(newEntity: Model.Entity): Model.Surface? = null

    //    override fun addTo(parent: VizObj) {
//        parent.add(VizObj(mesh))
//        mesh.updateMatrixWorld(true)
////        lines.forEach { line -> parent.add(VizObj(line)) }
////        vizPixels?.addTo(parent)
//        this.parent = parent
//    }
}