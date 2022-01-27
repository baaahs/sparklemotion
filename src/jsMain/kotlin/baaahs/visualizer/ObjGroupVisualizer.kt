package baaahs.visualizer

import baaahs.model.EntityId
import baaahs.model.Model
import baaahs.model.ObjGroup
import baaahs.util.three.addPadding
import three.js.*
import three_ext.expandByObjectLocal

class ObjGroupVisualizer(
    objGroup: ObjGroup,
    adapter: EntityAdapter
) : BaseEntityVisualizer<ObjGroup>(objGroup) {
    override val obj: Object3D = Group()
    private val groupVisualizer = GroupVisualizer("Group: ${objGroup.title}", objGroup.entities, adapter)
        .also { obj.add(it.groupObj) }

    private val boxHelperMaterial = LineDashedMaterial()
    private val boxHelper = Box3Helper(Box3(), Color("#22BB66")).also { helper ->
        helper.entityVisualizer = this@ObjGroupVisualizer
        helper.updateWithPadding(.02)
        helper.material = boxHelperMaterial
        obj.add(helper)
    }

    init { update(entity) }

    override fun findById(id: EntityId): EntityVisualizer<*>? =
        super.findById(id) ?: groupVisualizer.findById(id)

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToLine(boxHelperMaterial)
    }

    override fun traverse(callback: (EntityVisualizer<*>) -> Unit) {
        super.traverse(callback)
        groupVisualizer.traverse(callback)
    }

    override fun isApplicable(newEntity: Model.Entity): ObjGroup? =
        newEntity as? ObjGroup

    override fun update(newEntity: ObjGroup, callback: ((EntityVisualizer<*>) -> Unit)?) {
        super.update(newEntity, callback)
        groupVisualizer.updateChildren(newEntity.entities) {
            it.obj.objGroupName = newEntity.title
            callback?.invoke(it)
        }
        boxHelper.updateWithPadding(.02)
    }

    private fun Box3Helper.updateWithPadding(amount: Double) {
        box.makeEmpty()
        groupVisualizer.groupObj.children.forEach { child ->
            if (child != boxHelper) {
                box.expandByObjectLocal(child)
            }
        }
        box.addPadding(amount)
    }
}

var Object3D.objGroupName: String?
    get() = userData["objGroupName"] as String?
    set(value) { userData["objGroupName"] = value }

var Object3D.entityVisualizer: EntityVisualizer<*>?
    get() = userData["entityVisualizer"] as EntityVisualizer<*>?
    set(value) { userData["entityVisualizer"] = value }