package baaahs.visualizer

import baaahs.model.Model
import baaahs.sim.SimulationEnv
import three.js.*

class SurfaceVisualizer(
    private val surface: Model.Surface,
    val surfaceGeometry: SurfaceGeometry,
    private val simulationEnv: SimulationEnv,
    vizPixels: VizPixels? = null
) : BaseEntityVisualizer<Model.Surface>(surface) {
//    override var mapperIsRunning: Boolean = false
//        set(isRunning) {
//            field = isRunning
//            faceMaterial.transparent = !isRunning
//        }

//    override var selected: Boolean = false
//        set(value) {
//            mesh.material = if (value) selectedFaceMaterial else faceMaterial
//            lines.forEach {
//                it.material = if (value) selectedLineMaterial else lineMaterial
//            }
//            field = value
//        }

    private val lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
    private val selectedLineMaterial = LineBasicMaterial().apply {
        color.set(0xffccaa)
        linewidth = 3
    }
    private var faceMaterial = MeshBasicMaterial().apply {
        color.set(0x222222)
        side = FrontSide
        transparent = false
    }
    private var selectedFaceMaterial = MeshBasicMaterial().apply { color.set(0x443322) }
    private val mesh = Mesh(surfaceGeometry.geometry, this.faceMaterial)
    private val lines: List<Line<*, LineBasicMaterial>>
    val panelNormal: Vector3 get() = surfaceGeometry.panelNormal
    val geometry: Geometry get() = surfaceGeometry.geometry
    var vizPixels: VizPixels? = vizPixels
        set(value) {
            val meshObj = VizObj(obj)
            field?.removeFrom(meshObj)
            value?.addTo(meshObj)

            field = value
        }
    override val obj = Group().apply {
        println("New SurfaceVisualizer for ${entity.title}: vizPixels=$vizPixels")
        add(mesh)
        vizPixels?.addTo(VizObj(this))
    }

    init { update(entity) }

    init {
        mesh.name = "Surface: ${surfaceGeometry.name}"
//        mesh.matrix.copy(surfaceGeometry.surface.transformation.nativeMatrix)
//        mesh.matrixAutoUpdate = false
//        mesh.matrixWorldNeedsUpdate = true
//        mesh.entityVisualizer = this

        lines = surfaceGeometry.lines.map { line ->
            val lineGeo = Geometry()
            lineGeo.vertices = line.vertices.map { pt -> pt.toVector3() }.toTypedArray()
            Line(lineGeo, lineMaterial).apply {
                matrixAutoUpdate = false
                entityVisualizer = this@SurfaceVisualizer
                mesh.add(this)
            }
        }
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