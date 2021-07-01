package baaahs.visualizer

import three.js.*

class SurfaceVisualizer(
    val surfaceGeometry: SurfaceGeometry,
    vizPixels: VizPixels? = null
) : EntityVisualizer {
    val name: String get() = surfaceGeometry.name
    private val lineMaterial = LineBasicMaterial().apply { color.set(0xaaaaaa) }
    private var faceMaterial = MeshBasicMaterial().apply { color.set(0x222222) }
    private val mesh = Mesh(surfaceGeometry.geometry, this.faceMaterial)
    private val lines: List<Line<*, *>>
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

        mesh.asDynamic().name = "Surface: ${surfaceGeometry.name}"

        // so we can get back to the SurfaceVisualizer from a raycaster intersection:
        this.mesh.userData.asDynamic()["SurfaceVisualizer"] = this

        this.lines = surfaceGeometry.lines.map { line -> Line(line, lineMaterial) }

    }

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(this.mesh))
        lines.forEach { line -> scene.add(VizObj(line)) }
        vizPixels?.addToScene(scene)
        vizScene = scene
    }

    override var mapperIsRunning: Boolean = false
        set(isRunning) {
            field = isRunning
            faceMaterial.transparent = !isRunning
        }

    companion object {
        fun getFromObject(object3D: Object3D): SurfaceVisualizer? =
            object3D.userData.asDynamic()["SurfaceVisualizer"] as SurfaceVisualizer?
    }

}