package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.Model
import three.js.*

class SurfaceVisualizer(
    private val surface: Model.Surface,
    val surfaceGeometry: SurfaceGeometry,
    vizPixels: VizPixels? = null
) : EntityVisualizer {
    override val entity: Model.Entity = surface
    override val title: String get() = surface.name
    override var mapperIsRunning: Boolean = false
        set(isRunning) {
            field = isRunning
            faceMaterial.transparent = !isRunning
        }

    override var selected: Boolean = false
        set(value) {
            mesh.material = if (value) selectedFaceMaterial else faceMaterial
            lines.forEach {
                it.material = if (value) selectedLineMaterial else lineMaterial
            }
            field = value
        }

    override var transformation: Matrix4F
        get() = Matrix4F(mesh.matrix)
        set(value) {
            mesh.matrix = value.nativeMatrix
            mesh.updateMatrixWorld(true)
            lines.forEach { line ->
                line.matrix = value.nativeMatrix
                line.updateMatrixWorld(true)
            }
        }

    private val lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
    private val selectedLineMaterial = LineBasicMaterial().apply {
        color.set(0xffccaa)
        linewidth = 3
    }
    private var faceMaterial = MeshBasicMaterial().apply { color.set(0x222222) }
    private var selectedFaceMaterial = MeshBasicMaterial().apply { color.set(0x443322) }
    private val mesh = Mesh(surfaceGeometry.geometry, this.faceMaterial)
    private val lines: List<Line<*, LineBasicMaterial>>
    val panelNormal: Vector3 get() = surfaceGeometry.panelNormal
    val geometry: Geometry get() = surfaceGeometry.geometry
    private var vizScene: VizScene? = null
    var vizPixels: VizPixels? = vizPixels
        set(value) {
            vizScene?.let { scene ->
                field?.removeFromScene(scene)
                value?.addToScene(scene)
            }

            field = value
        }

    init {
        this.faceMaterial.side = FrontSide
        this.faceMaterial.transparent = false

        mesh.name = "Surface: ${surfaceGeometry.name}"
        mesh.matrix.copy(surfaceGeometry.surface.transformation.nativeMatrix)
        mesh.matrixAutoUpdate = false
        mesh.updateMatrixWorld(true)

        this.lines = surfaceGeometry.lines.map { line ->
            val lineGeo = Geometry()
            lineGeo.vertices = line.vertices.map { pt -> pt.toVector3() }.toTypedArray()
            Line(lineGeo, lineMaterial).apply { matrixAutoUpdate = false }
        }
    }

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(this.mesh))
        lines.forEach { line -> scene.add(VizObj(line)) }
        vizPixels?.addToScene(scene)
        vizScene = scene
    }
}