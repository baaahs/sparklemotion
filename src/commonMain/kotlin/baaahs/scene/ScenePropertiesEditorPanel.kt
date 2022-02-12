package baaahs.scene

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.PropsEditor
import baaahs.app.ui.editor.editorPanelViews
import baaahs.ui.Icon
import baaahs.ui.View

data class ScenePropertiesEditorPanel(
    private val editableManager: EditableManager<*>,
    private val propsEditors: List<PropsEditor>
) : DialogPanel {
    constructor(
        editableManager: EditableManager<*>,
        vararg propsEditors: PropsEditor
    ) : this(editableManager, propsEditors.toList())

    override val title: String
        get() = "Properties"
    override val icon: Icon
        get() = CommonIcons.Settings

    override fun getView(): View =
        editorPanelViews.forGenericPropertiesPanel(editableManager, propsEditors)
}

data class SceneTitlePropsEditor(val mutableScene: MutableScene) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forSceneTitleComponent(editableManager, mutableScene)
}

data class ModelUnitsPropsEditor(val mutableScene: MutableScene) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forModelUnitsComponent(editableManager, mutableScene)
}
