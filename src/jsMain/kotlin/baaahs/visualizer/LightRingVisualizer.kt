package baaahs.visualizer

import baaahs.model.LightRing
import three.js.*
import three_ext.clear

class LightRingVisualizer(
    lightRing: LightRing,
    vizPixels: VizPixels? = null
) : BaseEntityVisualizer<LightRing>(lightRing) {
    private val ringMesh: Mesh<WireframeGeometry, MeshBasicMaterial> = Mesh()
    private val ringMaterial = MeshBasicMaterial()

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
        entityStyle.applyToMesh(ringMesh.material)
    }

    override fun isApplicable(newItem: Any): LightRing? =
        newItem as? LightRing

    override fun update(newItem: LightRing) {
        super.update(newItem)

        val center = newItem.center
        val normal = newItem.planeNormal

        val ringGeom = WireframeGeometry(RingGeometry(
            innerRadius = newItem.radius - 1,
            outerRadius = newItem.radius + 1,
            thetaSegments = 16, phiSegments = 1
        ))

        Rotator(three_ext.vector3FacingForward, normal.toVector3())
            .rotate(ringGeom)

        with(center) { ringGeom.translate(x, y, z) }

        obj.clear()
        obj.add(LineSegments(ringGeom, LineBasicMaterial().apply {
            color = Color(0xffccaa)
            opacity = .25
            transparent = true
        }))

        // TODO: Replace with arrow.
        obj.add(Mesh(SphereGeometry(newItem.radius / 20).apply {
            translate(newItem.radius, 0, 0)
        }, MeshBasicMaterial().apply {
            color = Color(0xaa0000)
            opacity = .25
            transparent = true
        }))
        ringMesh.geometry = ringGeom
        ringMesh.material = ringMaterial
    }
}