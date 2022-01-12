package baaahs.visualizer

import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.model.PixelArray
import baaahs.model.PolyLine
import baaahs.sim.SimulationEnv
import three.js.*

class LightBarVisualizer(
    lightBar: LightBar,
    simulationEnv: SimulationEnv,
    vizPixels: VizPixels? = null
) : PixelArrayVisualizer<LightBar>(lightBar, simulationEnv, vizPixels) {
    init { update(entity) }

    override fun isApplicable(newEntity: Model.Entity): LightBar? =
        newEntity as? LightBar
}

class PolyLineVisualizer(
    polyLine: PolyLine,
    simulationEnv: SimulationEnv
) : PixelArrayVisualizer<PolyLine>(polyLine, simulationEnv) {
    init { update(entity) }

    override fun isApplicable(newEntity: Model.Entity): PolyLine? =
        newEntity as? PolyLine
}

abstract class PixelArrayVisualizer<T : PixelArray>(
    pixelArray: T,
    simulationEnv: SimulationEnv,
    vizPixels: VizPixels? = null
) : BaseEntityVisualizer<T>(pixelArray) {
    private val mesh: Mesh<BoxGeometry, MeshBasicMaterial> = Mesh()
    override val obj: Object3D get() = mesh

    override fun update(newEntity: T) {
        super.update(newEntity)

        val bounds = entity.bounds
        val startVertex = bounds.first
        val endVertex = bounds.second
        val normal = endVertex.minus(startVertex).normalize()

        // TODO: This is wrong.
        val width = endVertex.x - startVertex.x
        val length = endVertex.y - startVertex.y
        mesh.geometry = BoxGeometry(width, length, 1).apply {
            translate(width / 2, length / 2, 0)

            Rotator(Vector3(0, 1, 0), normal.toVector3())
                .rotate(this)

            with(startVertex) { translate(x, y, z) }
        }

        mesh.material = barMaterial
    }

//    override val title: String = pixelArray.name
//    override var mapperIsRunning: Boolean = false
//
//    override var selected: Boolean = false
//        set(value) {
//            boxMesh.material = if (value) selectedBarMaterial else barMaterial
//            field = value
//        }
//
//    private var parent: VizObj? = null
//    var vizPixels : VizPixels? = vizPixels
//        set(value) {
//            parent?.let { scene ->
//                field?.removeFrom(scene)
//                value?.addTo(scene)
//            }
//
//            field = value
//        }

//    private val boxMesh: Mesh<BoxGeometry, MeshBasicMaterial>

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

//    init {
//        val bounds = pixelArray.bounds
//        val startVertex = bounds.first
//        val endVertex = bounds.second
//        val normal = endVertex.minus(startVertex).normalize()
//
//        // TODO: This is wrong.
//        val width = endVertex.x - startVertex.x
//        val length = endVertex.y - startVertex.y
//
//        val boxGeom = BoxGeometry(width, length, 1)
//        boxGeom.translate(width / 2, length / 2, 0)
//
//        Rotator(Vector3(0, 1, 0), normal.toVector3())
//            .rotate(boxGeom)
//
//        with(startVertex) { boxGeom.translate(x, y, z) }
//
//        boxMesh = Mesh(boxGeom, barMaterial).apply {
//            matrixAutoUpdate = false
//            entityVisualizer = this@LightBarVisualizer
//        }
//    }

//    override fun addTo(parent: VizObj) {
//        parent.add(VizObj(boxMesh))
//        vizPixels?.addTo(parent)
//        this.parent = parent
//    }
}
