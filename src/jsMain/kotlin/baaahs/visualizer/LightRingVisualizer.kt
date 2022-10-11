package baaahs.visualizer

import baaahs.fixtures.PixelArrayRemoteConfig
import baaahs.fixtures.RemoteConfig
import baaahs.io.ByteArrayReader
import baaahs.model.LightRing
import baaahs.sim.LightBarSimulation
import three.js.*
import three_ext.clear

class LightRingVisualizer(
    lightRing: LightRing,
    private val adapter: EntityAdapter,
    vizPixels: VizPixels?
) : BaseEntityVisualizer<LightRing>(lightRing) {
    private val ringMesh = Mesh<RingGeometry, MeshBasicMaterial>()
    private val ringMaterial = MeshBasicMaterial()

    private val lineMaterial = LineDashedMaterial()
    private val pixel0IndicatorMaterial = MeshBasicMaterial()

    private val pixelsPreview = PixelsPreview()

    override val obj = Object3D()

    private var parent: VizObj? = null
    var vizPixels : VizPixels? = vizPixels
        set(value) {
            parent?.let { parent ->
                field?.removeFrom(parent)
                value?.addTo(parent)
            }

            field = value
        }

    init { update(item) }

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToMesh(ringMesh.material, EntityStyle.Use.BacklitSurface)
        entityStyle.applyToLine(lineMaterial, EntityStyle.Use.BacklitSurface)
        entityStyle.applyToMesh(pixel0IndicatorMaterial, EntityStyle.Use.LightStrandHint)

        pixelsPreview.applyStyle(entityStyle)
    }

    override fun isApplicable(newItem: Any): LightRing? =
        newItem as? LightRing

    override fun update(newItem: LightRing) {
        super.update(newItem)

        val ringGeom = RingGeometry(
            innerRadius = newItem.radius - 1,
            outerRadius = newItem.radius + 1,
            thetaSegments = 16, phiSegments = 1
        )

        obj.clear()
        obj.add(ringMesh)
        obj.add(pixelsPreview)
        vizPixels?.addTo(obj)

        val pixelLocations = newItem.calculatePixelLocalLocations(pixelCount_UNKNOWN_BUSTED)

        // TODO: Replace with arrow.
        pixelLocations.firstOrNull()?.let { pixel0 ->
            obj.add(Mesh(SphereGeometry(newItem.radius / 20).apply {
                translate(pixel0.x, pixel0.y, pixel0.z)
            }, pixel0IndicatorMaterial))
        }
        ringMesh.geometry = ringGeom
        ringMesh.material = ringMaterial

        pixelsPreview.setLocations(pixelLocations.map { it.toVector3() }.toTypedArray())
    }

    override fun receiveRemoteConfig(remoteConfig: RemoteConfig) {
        remoteConfig as PixelArrayRemoteConfig
        vizPixels = VizPixels(
            remoteConfig.pixelLocations.map { it.toVector3() }.toTypedArray(),
            LightBarSimulation.pixelVisualizationNormal,
            item.transformation,
            remoteConfig.pixelFormat,
            adapter.units.fromCm(VizPixels.diffusedLedRangeCm)
        )
    }

    override fun receiveRemoteFrameData(reader: ByteArrayReader) {
        vizPixels?.readColors(reader)
    }

    companion object {
        // TODO!!!
        const val pixelCount_UNKNOWN_BUSTED = 100
    }

}