package baaahs.visualizer

import baaahs.model.LightRing
import three.js.Color
import three.js.Mesh
import three.js.MeshBasicMaterial
import three.js.RingGeometry

class LightRingVisualizer(
    lightRing: LightRing,
    vizPixels: VizPixels? = null
) : EntityVisualizer {
    override val title: String = lightRing.name
    override var mapperIsRunning: Boolean = false

    override var selected: Boolean = false
        set(value) {
            ringMesh.material.wireframeLinewidth = if (value) 3 else 1
            ringMesh.material.needsUpdate = true
            field = value
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

        ringMesh = Mesh(ringGeom, MeshBasicMaterial().apply {
            color = Color(0x4444FF)
            opacity = .25
            transparent = true
            wireframe = true
        })
    }

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(ringMesh))
        vizPixels?.addToScene(scene)
        vizScene = scene
    }
}