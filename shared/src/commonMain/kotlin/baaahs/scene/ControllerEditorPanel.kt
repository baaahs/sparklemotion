package baaahs.scene

import baaahs.ui.Icon
import baaahs.ui.View
import baaahs.visualizer.entity.visualizerBuilder

abstract class ControllerEditorPanel<T: MutableControllerConfig>(
    val title: String? = null,
    val icon: Icon? = null
) {
    abstract fun getView(editingController: EditingController<*>): View
}

class EditingController<T: MutableControllerConfig>(
    val config: T,
    val onChange: () -> Unit
) {
    fun getEditorPanelViews(): List<View> =
        config.getEditorPanels(this).map { it.getView(this) }
}

object BrainControllerEditorPanel : ControllerEditorPanel<MutableBrainControllerConfig>() {
    override fun getView(editingController: EditingController<*>): View =
        visualizerBuilder.getBrainControllerEditorView(editingController as EditingController<MutableBrainControllerConfig>)
}

object DirectDmxControllerEditorPanel : ControllerEditorPanel<MutableDirectDmxControllerConfig>() {
    override fun getView(editingController: EditingController<*>): View =
        visualizerBuilder.getDirectDmxControllerEditorView(editingController as EditingController<MutableDirectDmxControllerConfig>)
}
object SacnControllerEditorPanel : ControllerEditorPanel<MutableSacnControllerConfig>() {
    override fun getView(editingController: EditingController<*>): View =
        visualizerBuilder.getSacnControllerEditorView(editingController as EditingController<MutableSacnControllerConfig>)
}
