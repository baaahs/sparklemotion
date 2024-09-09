package baaahs.visualizer

import baaahs.model.ImportedEntityGroup
import baaahs.model.Model
import baaahs.util.three.addPadding
import three.js.*
import three_ext.expandByObjectLocal

class EntityGroupVisualizer(
    objGroup: Model.EntityGroup,
    adapter: EntityAdapter
) : BaseEntityVisualizer<Model.EntityGroup>(objGroup) {
    override val obj: Object3D = Group()
    private val groupVisualizer =
        GroupVisualizer("Group: ${objGroup.title}", objGroup.entities, adapter)
            .also { obj.add(it.groupObj) }

    private val boxHelperMaterial = LineDashedMaterial()
    private val boxHelper = Box3Helper(Box3(), Color("#22BB66")).also { helper ->
        helper.updateWithPadding(.02)
        helper.material = boxHelperMaterial
        obj.add(helper)
    }

    init { update(item) }

    override fun find(predicate: (Any) -> Boolean): ItemVisualizer<*>? =
        super.find(predicate) ?: groupVisualizer.find(predicate)

    override fun applyStyle(entityStyle: EntityStyle) {
        entityStyle.applyToLine(boxHelperMaterial, EntityStyle.Use.GroupOutline)
    }

    override fun traverse(callback: (ItemVisualizer<*>) -> Unit) {
        super.traverse(callback)
        groupVisualizer.traverse(callback)
    }

    override fun isApplicable(newItem: Any): ImportedEntityGroup? =
        newItem as? ImportedEntityGroup

    override fun update(newItem: Model.EntityGroup) {
        super.update(newItem)
        groupVisualizer.updateChildren(newItem.entities)
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

var Object3D.itemVisualizer: ItemVisualizer<*>?
    get() = userData.asDynamic()["entityVisualizer"] as ItemVisualizer<*>?
    set(value) { userData.asDynamic()["entityVisualizer"] = value }