package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.Model
import baaahs.model.PixelArray
import three.js.BoxGeometry
import three.js.Mesh
import three.js.MeshBasicMaterial
import three.js.Vector3

class LightBarVisualizer(
    pixelArray: PixelArray,
    vizPixels: VizPixels? = null
) : EntityVisualizer {
    override val entity: Model.Entity = pixelArray
    override val title: String = pixelArray.name
    override var mapperIsRunning: Boolean = false

    override var selected: Boolean = false
        set(value) {
            boxMesh.material = if (value) selectedBarMaterial else barMaterial
            field = value
        }

    override var transformation: Matrix4F
        get() = Matrix4F(boxMesh.matrix)
        set(value) {
            boxMesh.matrix = value.nativeMatrix
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

    private val boxMesh: Mesh<BoxGeometry, MeshBasicMaterial>

    private val barMaterial = MeshBasicMaterial().apply {
        wireframeLinewidth = 1
        color.set(0xaaaaaa)
        wireframe = true
    }
    private val selectedBarMaterial = MeshBasicMaterial().apply {
        wireframeLinewidth = 3
        color.set(0xffccaa)
        wireframe = true
    }

    init {
        val bounds = pixelArray.bounds
        val startVertex = bounds.first
        val endVertex = bounds.second
        val normal = endVertex.minus(startVertex).normalize()

        // TODO: This is wrong.
        val width = endVertex.x - startVertex.x
        val length = endVertex.y - startVertex.y

        val boxGeom = BoxGeometry(width, length, 1)
        boxGeom.translate(width / 2, length / 2, 0)

        Rotator(Vector3(0, 1, 0), normal.toVector3())
            .rotate(boxGeom)

        with(startVertex) { boxGeom.translate(x, y, z) }

        boxMesh = Mesh(boxGeom, barMaterial)
    }

    override fun addTo(scene: VizScene) {
        scene.add(VizObj(boxMesh))
        vizPixels?.addToScene(scene)
        vizScene = scene
    }
}