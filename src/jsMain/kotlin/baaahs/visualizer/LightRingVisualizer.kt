package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.LightRing
import baaahs.model.Model
import three.js.Color
import three.js.Mesh
import three.js.MeshBasicMaterial
import three.js.RingGeometry

class LightRingVisualizer(
    lightRing: LightRing,
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
        }

    private var vizScene: VizScene? = null
    var vizPixels : VizPixels? = vizPixels
        set(value) {
            vizScene?.let { scene ->
                field?.removeFromScene(scene)
                value?.addToScene(scene)
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

        ringMesh = Mesh(ringGeom, ringMaterial)
    }

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(ringMesh))
        vizPixels?.addToScene(scene)
        vizScene = scene
    }
}