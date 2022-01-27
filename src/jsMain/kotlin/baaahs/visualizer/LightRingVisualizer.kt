package baaahs.visualizer

import baaahs.model.LightRing
import baaahs.model.Model
import baaahs.sim.SimulationEnv
import three.js.*
import three_ext.clear

class LightRingVisualizer(
    lightRing: LightRing,
    simulationEnv: SimulationEnv,
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

    init { update(entity) }

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToMesh(ringMesh.material)
    }

    override fun isApplicable(newEntity: Model.Entity): LightRing? =
        newEntity as? LightRing

    override fun update(newEntity: LightRing, callback: ((EntityVisualizer<*>) -> Unit)?) {
        super.update(newEntity, callback)

        val center = newEntity.center
        val normal = newEntity.planeNormal

        val ringGeom = WireframeGeometry(RingGeometry(
            innerRadius = newEntity.radius - 1,
            outerRadius = newEntity.radius + 1,
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
        obj.add(Mesh(SphereGeometry(newEntity.radius / 20).apply {
            translate(newEntity.radius, 0, 0)
        }, MeshBasicMaterial().apply {
            color = Color(0xaa0000)
            opacity = .25
            transparent = true
        }))
        ringMesh.geometry = ringGeom
        ringMesh.material = ringMaterial
    }
}