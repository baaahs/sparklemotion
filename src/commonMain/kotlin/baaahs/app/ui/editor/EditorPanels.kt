package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.EditorPanel
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShaderInstance
import baaahs.ui.Icon
import baaahs.ui.Renderer

data class GenericPropertiesEditorPanel(
    val components: List<EditorPanelComponent>
) : EditorPanel {
    constructor(vararg components: EditorPanelComponent) : this(components.toList())

    override val title: String
        get() = "Properties"
    override val listSubhead: String?
        get() = null
    override val icon: Icon?
        get() = CommonIcons.Settings

    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forGenericPropertiesPanel(editableManager, components)
}

data class PatchHolderEditorPanel(
    private val mutablePatchHolder: MutablePatchHolder
) : EditorPanel {
    override val title: String
        get() = "Patches"
    override val listSubhead: String?
        get() = null
    override val icon: Icon?
        get() = CommonIcons.Patch

    override fun getNestedEditorPanels(): List<EditorPanel> {
        return mutablePatchHolder.patches.map { mutablePatch -> mutablePatch.getEditorPanel() }
    }

    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forPatchHolder(editableManager, mutablePatchHolder)
}

data class PatchEditorPanel(
    private val mutablePatch: MutablePatch
) : EditorPanel {
    override val title: String
        get() = mutablePatch.surfaces.name
    override val listSubhead: String?
        get() = "Fixtures"
    override val icon: Icon?
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
        override val listSubhead: String?
            get() = "Shaders"
        override val icon: Icon?
            get() = mutableShaderInstance.mutableShader.type.icon

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

interface EditorPanelComponent {
    fun getRenderer(editableManager: EditableManager): Renderer
}

data class TitleEditorPanelComponent(val mutablePatchHolder: MutablePatchHolder) : EditorPanelComponent {
    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forTitleComponent(editableManager, mutablePatchHolder)
}

data class ButtonGroupDirectionEditorPanelComponent(
    val mutableButtonGroupControl: MutableButtonGroupControl
) : EditorPanelComponent {
    override fun getRenderer(editableManager: EditableManager): Renderer =
        editorPanelViews.forButtonGroup(editableManager, mutableButtonGroupControl)
}

interface EditorPanelViews {
    fun forGenericPropertiesPanel(editableManager: EditableManager, components: List<EditorPanelComponent>): Renderer
    fun forPatchHolder(editableManager: EditableManager, mutablePatchHolder: MutablePatchHolder): Renderer
    fun forPatch(editableManager: EditableManager, mutablePatch: MutablePatch): Renderer
    fun forShaderInstance(
        editableManager: EditableManager,
        mutablePatch: MutablePatch,
        mutableShaderInstance: MutableShaderInstance
    ): Renderer
    fun forButtonGroup(editableManager: EditableManager, mutableButtonGroupControl: MutableButtonGroupControl): Renderer

    fun forTitleComponent(editableManager: EditableManager, mutablePatchHolder: MutablePatchHolder): Renderer
}

val editorPanelViews by lazy { getEditorPanelViews() }
expect fun getEditorPanelViews(): EditorPanelViews