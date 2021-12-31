package baaahs.visualizer

import baaahs.geom.Matrix4F
import baaahs.model.Model
import baaahs.model.ObjGroup
import baaahs.sim.SimulationEnv
import three.js.*

class ObjGroupVisualizer(
    private val objGroup: ObjGroup,
    simulationEnv: SimulationEnv
) : EntityVisualizer, EntityGroupVisualizer {
    override val entity: Model.Entity get() = objGroup
    override val title: String get() = entity.title
    override var mapperIsRunning: Boolean = false
    override var selected: Boolean = false
    override var transformation: Matrix4F = objGroup.transformation
        set(value) {
            field = value
//            value.copyTo(boxHelper)
            value.copyTo(group)
            boxHelper.updateWithPadding()
        }

    private val childEntityVisualizers: Map<Model.Entity, EntityVisualizer>
    override val children: Collection<EntityVisualizer>
        get() = childEntityVisualizers.values

    private val group = Group()

    init {
        childEntityVisualizers = objGroup.entities.associateWith { entity ->
            entity.createVisualizer(simulationEnv).also {
                it.addTo(VizObj(group))
            }
        }
    }

    private val boxHelper = Box3Helper(Box3(), Color("#22BB66")).also { helper ->
        helper.entityVisualizer = this@ObjGroupVisualizer
        helper.updateWithPadding()
    }

    private fun Box3Helper.updateWithPadding() {
        box.makeEmpty()
        group.children.forEach { child ->
            box.expandByObject(child)
            child.matrixWorldNeedsUpdate = true
        }
        val padding = box.min.distanceTo(box.max) * .02
        box.expandByScalar(padding)
//        this.applyMatrix4(transformation.nativeMatrix)
    }

    override fun addTo(parent: VizObj) {
        parent.add(VizObj(group))
        parent.add(VizObj(boxHelper))
    }
}

var Object3D.entityVisualizer: EntityVisualizer?
    get() = userData["entityVisualizer"] as EntityVisualizer?
    set(value) { userData["entityVisualizer"] = value }