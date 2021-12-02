package baaahs.visualizer

import three.js.*

class SurfaceVisualizer(
    val surfaceGeometry: SurfaceGeometry,
    vizPixels: VizPixels? = null
) : EntityVisualizer {
    override val title: String get() = surfaceGeometry.name
    override var mapperIsRunning: Boolean = false
        set(isRunning) {
            field = isRunning
            faceMaterial.transparent = !isRunning
        }

    override var selected: Boolean = false
        set(value) {
            lineMaterial.linewidth = if (value) 3 else 1
            lineMaterial.needsUpdate = true
            field = value
        }

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

        mesh.name = "Surface: ${surfaceGeometry.name}"
        mesh.matrix.copy(surfaceGeometry.surface.transformation.nativeMatrix)
        mesh.matrixAutoUpdate = false
        mesh.updateMatrixWorld(true)

        this.lines = surfaceGeometry.lines.map { line ->
            val lineGeo = Geometry()
            lineGeo.vertices = line.vertices.map { pt -> pt.toVector3() }.toTypedArray()
            Line(lineGeo, lineMaterial)
        }
    }

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(this.mesh))
        lines.forEach { line -> scene.add(VizObj(line)) }
        vizPixels?.addToScene(scene)
        vizScene = scene
    }
}