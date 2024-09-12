package baaahs.visualizer

import baaahs.device.PixelArrayDevice
import baaahs.fixtures.FixtureConfig
import baaahs.io.ByteArrayReader
import baaahs.model.Model
import three.*

class SurfaceVisualizer(
    private val surface: Model.Surface,
    private val adapter: EntityAdapter,
    val surfaceGeometry: SurfaceGeometry,
    vizPixels: VizPixels? = null
) : BaseEntityVisualizer<Model.Surface>(surface) {
    private val mesh = Mesh(surfaceGeometry.geometry, MeshBasicMaterial()).apply {
        name = "Surface: ${surfaceGeometry.name}"
    }

    private val lineMaterial = LineDashedMaterial()
    private val lines: List<Line<*, LineDashedMaterial>> = surfaceGeometry.lines.map { line ->
        val lineGeo = BufferGeometry<NormalOrGLBufferAttributes>()
        lineGeo.setFromPoints(line.vertices.map { pt -> pt.toVector3() }.toTypedArray())
        Line(lineGeo, lineMaterial).apply {
            matrixAutoUpdate = false
            mesh.add(this)
        }
    }

    val panelNormal: Vector3 get() = surfaceGeometry.panelNormal
    val geometry: BufferGeometry<NormalOrGLBufferAttributes> get() = surfaceGeometry.geometry
    var vizPixels: VizPixels? = vizPixels
        set(value) {
            field?.removeFrom(obj)
            value?.addTo(obj)

            field = value
        }

    override val obj = Group().apply {
        add(mesh)
        vizPixels?.addTo(this)
    }

    init { update(item) }

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToMesh(mesh.material, EntityStyle.Use.BacklitSurface)
        entityStyle.applyToLine(lineMaterial, EntityStyle.Use.BacklitSurface)
    }

    override fun isApplicable(newItem: Any): Model.Surface? = null

    override fun receiveFixtureConfig(fixtureConfig: FixtureConfig) {
        fixtureConfig as PixelArrayDevice.Config

        vizPixels = VizPixels(
            fixtureConfig.pixelLocations.arrayOfVector3(),
            surfaceGeometry.panelNormal,
            surface.transformation,
            fixtureConfig.pixelFormat,
            adapter.units.fromCm(VizPixels.diffusedLedRangeCm)
        )
    }

    override fun receiveRemoteFrameData(reader: ByteArrayReader) {
        vizPixels?.readColors(reader)
    }

    //    override fun addTo(parent: VizObj) {
//        parent.add(VizObj(mesh))
//        mesh.updateMatrixWorld(true)
////        lines.forEach { line -> parent.add(VizObj(line)) }
////        vizPixels?.addTo(parent)
//        this.parent = parent
//    }
}