package baaahs.visualizer

import baaahs.model.Model
import baaahs.model.ObjGroup
import baaahs.sim.SimulationEnv
import three.js.*
import three_ext.expandByObjectLocal

class ObjGroupVisualizer(
    objGroup: ObjGroup,
    private val simulationEnv: SimulationEnv
) : BaseEntityVisualizer<ObjGroup>(objGroup), EntityGroupVisualizer {
    override val obj: Object3D
        get() = group

    override val children: Collection<EntityVisualizer<*>>
        get() = childEntityVisualizers.map { it.second }
    private val group = Group()

    private val childEntityVisualizers = arrayListOf<Pair<Model.Entity, EntityVisualizer<*>>>()

    private val boxHelper = Box3Helper(Box3(), Color("#22BB66")).also { helper ->
        helper.entityVisualizer = this@ObjGroupVisualizer
        helper.updateWithPadding(.02)
    }

    init { update(entity) }

    init {
//        group.matrix.copy(objGroup.transformation.nativeMatrix)
//        group.matrixAutoUpdate = false
//        group.matrixWorldNeedsUpdate = true
//        group.entityVisualizer = this

    }

    override fun isApplicable(newEntity: Model.Entity): ObjGroup? =
        newEntity as? ObjGroup

    override fun update(newEntity: ObjGroup) {
        super.update(newEntity)

        if (childEntityVisualizers.size == newEntity.entities.size) {
            newEntity.entities.zip(childEntityVisualizers).forEachIndexed { index, (newEntity, old) ->
                val (oldEntity, oldVisualizer) = old
                if (newEntity != oldEntity) {
                    if (oldVisualizer.updateIfApplicable(newEntity)) {
                        childEntityVisualizers[index] = newEntity to oldVisualizer
                    } else {
                        val newVisualizer = newEntity.createVisualizer(simulationEnv)
                        childEntityVisualizers[index] = newEntity to newVisualizer
                        group.children[index] = newVisualizer.obj
                    }
                }
            }
        } else {
            childEntityVisualizers.clear()
            newEntity.entities.forEach { entity ->
                val visualizer = entity.createVisualizer(simulationEnv)
                childEntityVisualizers.add(entity to visualizer)
                group.add(visualizer.obj)
            }
        }
    }

    private fun Box3Helper.updateWithPadding(amount: Double) {
        box.makeEmpty()
        val tempBox = Box3()
        group.children.forEach { child ->
            if (child != boxHelper) {
                tempBox.expandByObjectLocal(child)
            }
       }
        val padding = tempBox.min.distanceTo(tempBox.max) * amount
        tempBox.expandByScalar(padding)
        box.copy(tempBox)
    }

//    override fun addTo(parent: VizObj) {
//        parent.add(VizObj(group))
//        group.add(boxHelper)
//    }
}

var Object3D.entityVisualizer: EntityVisualizer<*>?
    get() = userData["entityVisualizer"] as EntityVisualizer<*>?
    set(value) { userData["entityVisualizer"] = value }