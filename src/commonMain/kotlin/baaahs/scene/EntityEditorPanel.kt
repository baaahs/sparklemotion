package baaahs.scene

import baaahs.ui.Icon
import baaahs.ui.View
import baaahs.visualizer.visualizerBuilder

abstract class EntityEditorPanel<T : MutableEntity>(
    val title: String? = null,
    val icon: Icon? = null
) {
    abstract fun getView(editingEntity: EditingEntity<out T>): View
}

object TitleAndDescEntityEditorPanel : EntityEditorPanel<MutableEntity>() {
    override fun getView(editingEntity: EditingEntity<out MutableEntity>): View =
        visualizerBuilder.getTitleAndDescEditorView(editingEntity)
}

object TransformEntityEditorPanel : EntityEditorPanel<MutableEntity>("Transformation") {
    override fun getView(editingEntity: EditingEntity<out MutableEntity>): View =
        visualizerBuilder.getTransformEditorView(editingEntity)
}

object GridEditorPanel : EntityEditorPanel<MutableGridData>("Grid") {
    override fun getView(editingEntity: EditingEntity<out MutableGridData>): View =
        visualizerBuilder.getGridEditorView(editingEntity)
}

object LightBarEditorPanel : EntityEditorPanel<MutableLightBarData>("Light Bar") {
    override fun getView(editingEntity: EditingEntity<out MutableLightBarData>): View =
        visualizerBuilder.getLightBarEditorView(editingEntity)
}

object HengeEditorPanel : EntityEditorPanel<MutableHengeData>("Henge") {
    override fun getView(editingEntity: EditingEntity<out MutableHengeData>): View =
        visualizerBuilder.getHengeEditorView(editingEntity)
}

object LightRingEditorPanel : EntityEditorPanel<MutableLightRingData>("Light Ring") {
    override fun getView(editingEntity: EditingEntity<out MutableLightRingData>): View =
        visualizerBuilder.getLightRingEditorView(editingEntity)
}

object MovingHeadEditorPanel : EntityEditorPanel<MutableMovingHeadData>("Moving Head") {
    override fun getView(editingEntity: EditingEntity<out MutableMovingHeadData>): View =
        visualizerBuilder.getMovingHeadEditorView(editingEntity)
}

object ImportedEntityEditorPanel : EntityEditorPanel<MutableImportedEntityGroup>("Import") {
    override fun getView(editingEntity: EditingEntity<out MutableImportedEntityGroup>): View =
        visualizerBuilder.getImportedEntityEditorView(editingEntity)
}
