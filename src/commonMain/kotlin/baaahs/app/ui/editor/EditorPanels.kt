package baaahs.app.ui.editor

import baaahs.Severity
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.EditorPanel
import baaahs.show.mutable.*
import baaahs.ui.Icon
import baaahs.ui.Renderer

data class GenericPropertiesEditorPanel(
    private val editableManager: EditableManager,
    private val propsEditors: List<PropsEditor>
) : EditorPanel {
    constructor(
        editableManager: EditableManager,
        vararg propsEditors: PropsEditor
    ) : this(editableManager, propsEditors.toList())

    override val title: String
        get() = "Properties"
    override val listSubhead: String?
        get() = null
    override val icon: Icon
        get() = CommonIcons.Settings

    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forGenericPropertiesPanel(editableManager, propsEditors)
}

data class PatchHolderEditorPanel(
    private val editableManager: EditableManager,
    private val mutablePatchHolder: MutablePatchHolder
) : EditorPanel {
    override val title: String
        get() = "Patches"
    override val listSubhead: String?
        get() = null
    override val icon: Icon
        get() = CommonIcons.Patch

    override fun getNestedEditorPanels(): List<EditorPanel> {
        return mutablePatchHolder.patches.map { mutablePatch -> mutablePatch.getEditorPanel(editableManager) }
    }

    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forPatchHolder(editableManager, mutablePatchHolder)
}

data class PatchEditorPanel(
    private val editableManager: EditableManager,
    private val mutablePatch: MutablePatch
) : EditorPanel {
    override val title: String
        get() = mutablePatch.surfaces.name
    override val listSubhead: String
        get() = "Fixtures"
    override val icon: Icon
        get() = CommonIcons.Fixture

    override fun getNestedEditorPanels(): List<EditorPanel> {
        return mutablePatch.mutableShaderInstances.map { mutableShaderInstance ->
            mutableShaderInstance.getEditorPanel(this)
        }
    }

    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forPatch(editableManager, mutablePatch)

    inner class ShaderInstanceEditorPanel(
        private val mutableShaderInstance: MutableShaderInstance
    ) : EditorPanel {
        override val title: String
            get() = mutableShaderInstance.mutableShader.title
        override val listSubhead: String
            get() = "Shaders"
        override val icon: Icon
            get() = CommonIcons.UnknownShader // TODO: Derive this via ShaderType.
        override val problemLevel: Severity?
            get() = super.problemLevel

        override fun getRenderer(editableManager: EditableManager): Renderer =
            editorPanelViews.forShaderInstance(editableManager, mutablePatch, mutableShaderInstance)

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ShaderInstanceEditorPanel) return false

            if (mutableShaderInstance != other.mutableShaderInstance) return false

            return true
        }

        override fun hashCode(): Int {
            return mutableShaderInstance.hashCode()
        }
    }
}

interface PropsEditor {
    fun getRenderer(editableManager: EditableManager): Renderer
}

data class TitlePropsEditor(val mutablePatchHolder: MutablePatchHolder) : PropsEditor {
    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forTitleComponent(editableManager, mutablePatchHolder)
}

data class ButtonPropsEditor(
    val mutableButtonControl: MutableButtonControl
) : PropsEditor {
    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forButton(editableManager, mutableButtonControl)
}

data class ButtonGroupPropsEditor(
    val mutableButtonGroupControl: MutableButtonGroupControl
) : PropsEditor {
    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forButtonGroup(editableManager, mutableButtonGroupControl)
}

data class VisualizerPropsEditor(
    val mutableVisualizerControl: MutableVisualizerControl
) : PropsEditor {
    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forVisualizer(editableManager, mutableVisualizerControl)
}

interface EditorPanelViews {
    fun forGenericPropertiesPanel(editableManager: EditableManager, propsEditors: List<PropsEditor>): Renderer
    fun forPatchHolder(editableManager: EditableManager, mutablePatchHolder: MutablePatchHolder): Renderer
    fun forPatch(editableManager: EditableManager, mutablePatch: MutablePatch): Renderer
    fun forShaderInstance(
        editableManager: EditableManager,
        mutablePatch: MutablePatch,
        mutableShaderInstance: MutableShaderInstance
    ): Renderer
    fun forButton(editableManager: EditableManager, mutableButtonControl: MutableButtonControl): Renderer
    fun forButtonGroup(editableManager: EditableManager, mutableButtonGroupControl: MutableButtonGroupControl): Renderer
    fun forVisualizer(editableManager: EditableManager, mutableVisualizerControl: MutableVisualizerControl): Renderer

    fun forTitleComponent(editableManager: EditableManager, mutablePatchHolder: MutablePatchHolder): Renderer
}

val editorPanelViews by lazy { getEditorPanelViews() }
expect fun getEditorPanelViews(): EditorPanelViews