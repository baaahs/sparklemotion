package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.LightRing
import baaahs.model.Model
import baaahs.sim.SimulationEnv
import three.js.Color
import three.js.Mesh
import three.js.MeshBasicMaterial
import three.js.RingGeometry

class LightRingVisualizer(
    lightRing: LightRing,
    simulationEnv: SimulationEnv,
    vizPixels: VizPixels? = null
) : EntityVisualizer {
    override val entity: Model.Entity = lightRing
    override val title: String = lightRing.name
    override var mapperIsRunning: Boolean = false

    override var selected: Boolean = false
        set(value) {
            ringMesh.material = if (value) selectedRingMaterial else ringMaterial
            field = value
        }

    override var transformation: Matrix4F
        get() = Matrix4F(ringMesh.matrix)
        set(value) {
            ringMesh.matrix = value.nativeMatrix
            ringMesh.updateMatrixWorld(true)
        }

    private var parent: VizObj? = null
    var vizPixels : VizPixels? = vizPixels
        set(value) {
            parent?.let { parent ->
                field?.removeFrom(parent)
                value?.addTo(parent)
            }

            field = value
        }

    private val ringMesh: Mesh<RingGeometry, MeshBasicMaterial>
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

    init {
        val center = lightRing.center
        val normal = lightRing.planeNormal

        val ringGeom = RingGeometry(
            innerRadius = lightRing.radius - 1,
            outerRadius = lightRing.radius + 1,
            thetaSegments = 16, phiSegments = 1
        )

        Rotator(three_ext.vector3FacingForward, normal.toVector3())
            .rotate(ringGeom)

        with(center) { ringGeom.translate(x, y, z) }

        ringMesh = Mesh(ringGeom, ringMaterial).apply {
            matrixAutoUpdate = false
            entityVisualizer = this@LightRingVisualizer
        }
    }

    override fun addTo(parent: VizObj) {
        parent.add(VizObj(ringMesh))
        vizPixels?.addTo(parent)
        this.parent = parent
    }
}