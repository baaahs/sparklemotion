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
    override var selected: Boolean = false
        set(value) {
            ringMesh.material = if (value) selectedRingMaterial else ringMaterial
            field = value
        }

    private val ringMesh: Mesh<WireframeGeometry, MeshBasicMaterial> = Mesh()
    private val ringMaterial = MeshBasicMaterial().apply {
        color = Color(0x666666)
        opacity = .25
        transparent = true
        wireframe = true
        wireframeLinewidth = 1
    }
    private val selectedRingMaterial = MeshBasicMaterial().apply {
        color = Color(0xffccaa)
        opacity = .25
        transparent = true
        wireframe = true
        wireframeLinewidth = 3
    }

    override val obj: Object3D = Object3D()

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

    override fun isApplicable(newEntity: Model.Entity): LightRing? =
        newEntity as? LightRing

    override fun update(newEntity: LightRing) {
        super.update(newEntity)

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