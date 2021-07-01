package baaahs.visualizer

import baaahs.model.LightBar
import three.js.*

class LightBarVisualizer(
    lightBar: LightBar,
    val vizPixels: VizPixels
) : EntityVisualizer {
    override var mapperIsRunning: Boolean = false

    private val object3D = Object3D()

    init {
        val length = lightBar.length

        length?.let {
            val startVertex = lightBar.startVertex!!
            val endVertex = lightBar.endVertex!!
            val normal = endVertex.minus(startVertex).normalize()

            val boxGeom = BoxGeometry(1, length, 1)
            boxGeom.translate(0, length / 2, 0)

            Rotator(Vector3(0, 1, 0), normal.toVector3())
                .rotate(boxGeom)

            with(startVertex) {
                boxGeom.translate(x, y, z)
            }

            val boxMesh = Mesh(boxGeom, MeshNormalMaterial().apply {
                wireframe = true
            })
            object3D.add(boxMesh)
        }
    }

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(object3D))
        vizPixels.addToScene(scene)
    }
}