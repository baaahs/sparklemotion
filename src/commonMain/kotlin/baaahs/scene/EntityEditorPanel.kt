package baaahs.scene

import baaahs.model.Grid
import baaahs.model.LightBar
import baaahs.model.Model
import baaahs.model.ObjGroup
import baaahs.ui.Icon
import baaahs.ui.View
import baaahs.visualizer.visualizerBuilder

abstract class EntityEditorPanel<T : Model.Entity>(
    val title: String? = null,
    val icon: Icon? = null
) {
    abstract fun getView(editingEntity: EditingEntity<out T>): View
}

object TitleAndDescEntityEditorPanel : EntityEditorPanel<Model.Entity>() {
    override fun getView(editingEntity: EditingEntity<out Model.Entity>): View =
        visualizerBuilder.getTitleAndDescEditorView(editingEntity)
}

object TransformEntityEditorPanel : EntityEditorPanel<Model.Entity>("Transformation") {
    override fun getView(editingEntity: EditingEntity<out Model.Entity>): View =
        visualizerBuilder.getTransformEditorView(editingEntity)
}

object GridEditorPanel : EntityEditorPanel<Grid>("Grid") {
    override fun getView(editingEntity: EditingEntity<out Grid>): View =
        visualizerBuilder.getGridEditorView(editingEntity)
}

object LightBarEditorPanel : EntityEditorPanel<LightBar>("Light Bar") {
    override fun getView(editingEntity: EditingEntity<out LightBar>): View =
        visualizerBuilder.getLightBarEditorView(editingEntity)
}

object ObjModelEntityEditorPanel : EntityEditorPanel<ObjGroup>("Import") {
    override fun getView(editingEntity: EditingEntity<out ObjGroup>): View =
        visualizerBuilder.getObjModelEditorView(editingEntity)
}
