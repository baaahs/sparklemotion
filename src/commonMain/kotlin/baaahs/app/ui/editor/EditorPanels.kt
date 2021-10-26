package baaahs.app.ui.editor

import baaahs.Severity
import baaahs.app.ui.CommonIcons
import baaahs.app.ui.dialog.DialogPanel
import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.control.MutableVisualizerControl
import baaahs.gl.openShader
import baaahs.severity
import baaahs.show.live.ShaderInstanceResolver
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.Icon
import baaahs.ui.View

data class GenericPropertiesEditorPanel(
    private val editableManager: EditableManager,
    private val propsEditors: List<PropsEditor>
) : DialogPanel {
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

    override fun getView(): View =
        editorPanelViews.forGenericPropertiesPanel(editableManager, propsEditors)
}

data class PatchHolderEditorPanel(
    private val editableManager: EditableManager,
    private val mutablePatchHolder: MutablePatchHolder
) : DialogPanel {
    override val title: String
        get() = "Patches"
    override val listSubhead: String?
        get() = null
    override val icon: Icon
        get() = CommonIcons.Patch

    override fun getNestedDialogPanels(): List<DialogPanel> {
        return mutablePatchHolder.patches.map { mutablePatch -> mutablePatch.getEditorPanel(editableManager) }
    }

    override fun getView(): View =
        editorPanelViews.forPatchHolder(editableManager, mutablePatchHolder)
}

data class PatchEditorPanel(
    private val editableManager: EditableManager,
    private val mutablePatch: MutablePatch
) : DialogPanel {
    override val title: String
        get() = mutablePatch.surfaces.name
    override val listSubhead: String
        get() = "Fixtures"
    override val icon: Icon
        get() = CommonIcons.Fixture

    override fun getNestedDialogPanels(): List<DialogPanel> {
        return mutablePatch.mutableShaderInstances.map { mutableShaderInstance ->
            mutableShaderInstance.getEditorPanel(this)
        }
    }

    override fun getView(): View =
        editorPanelViews.forPatch(editableManager, mutablePatch)

    inner class ShaderInstanceEditorPanel(
        private val mutableShaderInstance: MutableShaderInstance
    ) : DialogPanel {
        // TODO: This is a clunky way to get our cached toolchain... clean up somehow.
        val toolchain = editableManager.session!!.toolchain
        private val openShader = toolchain.openShader(mutableShaderInstance.mutableShader.build())
        private val liveShaderInstance = run {
            val shaderInstance = mutableShaderInstance.build(ShowBuilder())
            ShaderInstanceResolver.build(openShader, shaderInstance, emptyMap())
        }

        override val title: String
            get() = mutableShaderInstance.mutableShader.title
        override val listSubhead: String
            get() = "Shaders"
        override val icon: Icon
            get() = openShader.shaderType.icon
        override val problemLevel: Severity?
            by lazy { liveShaderInstance.problems.severity() }

        override fun getView(): View =
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
    fun getView(editableManager: EditableManager): View
}

data class TitlePropsEditor(val mutablePatchHolder: MutablePatchHolder) : PropsEditor {
    override fun getView(editableManager: EditableManager): View =
        editorPanelViews.forTitleComponent(editableManager, mutablePatchHolder)
}

data class ButtonPropsEditor(
    val mutableButtonControl: MutableButtonControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager): View =
        editorPanelViews.forButton(editableManager, mutableButtonControl)
}

data class ButtonGroupPropsEditor(
    val mutableButtonGroupControl: MutableButtonGroupControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager): View =
        editorPanelViews.forButtonGroup(editableManager, mutableButtonGroupControl)
}

data class VisualizerPropsEditor(
    val mutableVisualizerControl: MutableVisualizerControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager): View =
        editorPanelViews.forVisualizer(editableManager, mutableVisualizerControl)
}

interface EditorPanelViews {
    fun forGenericPropertiesPanel(editableManager: EditableManager, propsEditors: List<PropsEditor>): View
    fun forPatchHolder(editableManager: EditableManager, mutablePatchHolder: MutablePatchHolder): View
    fun forPatch(editableManager: EditableManager, mutablePatch: MutablePatch): View
    fun forShaderInstance(
        editableManager: EditableManager,
        mutablePatch: MutablePatch,
        mutableShaderInstance: MutableShaderInstance
    ): View
    fun forButton(editableManager: EditableManager, mutableButtonControl: MutableButtonControl): View
    fun forButtonGroup(editableManager: EditableManager, mutableButtonGroupControl: MutableButtonGroupControl): View
    fun forVisualizer(editableManager: EditableManager, mutableVisualizerControl: MutableVisualizerControl): View

    fun forTitleComponent(editableManager: EditableManager, mutablePatchHolder: MutablePatchHolder): View
}

val editorPanelViews by lazy { getEditorPanelViews() }
expect fun getEditorPanelViews(): EditorPanelViews