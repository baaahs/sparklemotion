package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.dialog.DialogPanel
import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.control.MutableVisualizerControl
import baaahs.gl.openShader
import baaahs.show.Show
import baaahs.show.live.ShaderInstanceResolver
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.MutableShaderInstance
import baaahs.show.mutable.ShowBuilder
import baaahs.sm.webapi.Severity
import baaahs.sm.webapi.severity
import baaahs.ui.Icon
import baaahs.ui.View

data class GenericPropertiesEditorPanel(
    private val editableManager: EditableManager<Show>,
    private val propsEditors: List<PropsEditor>
) : DialogPanel {
    constructor(
        editableManager: EditableManager<Show>,
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
    private val editableManager: EditableManager<Show>,
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
    private val editableManager: EditableManager<Show>,
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
        val toolchain = (editableManager.session!! as ShowEditableManager.ShowSession).toolchain
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
    fun getView(editableManager: EditableManager<Show>): View
}

data class TitlePropsEditor(val mutablePatchHolder: MutablePatchHolder) : PropsEditor {
    override fun getView(editableManager: EditableManager<Show>): View =
        editorPanelViews.forTitleComponent(editableManager, mutablePatchHolder)
}

data class ButtonPropsEditor(
    val mutableButtonControl: MutableButtonControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<Show>): View =
        editorPanelViews.forButton(editableManager, mutableButtonControl)
}

data class ButtonGroupPropsEditor(
    val mutableButtonGroupControl: MutableButtonGroupControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<Show>): View =
        editorPanelViews.forButtonGroup(editableManager, mutableButtonGroupControl)
}

data class VisualizerPropsEditor(
    val mutableVisualizerControl: MutableVisualizerControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<Show>): View =
        editorPanelViews.forVisualizer(editableManager, mutableVisualizerControl)
}

interface EditorPanelViews {
    fun forGenericPropertiesPanel(editableManager: EditableManager<Show>, propsEditors: List<PropsEditor>): View
    fun forPatchHolder(editableManager: EditableManager<Show>, mutablePatchHolder: MutablePatchHolder): View
    fun forPatch(editableManager: EditableManager<Show>, mutablePatch: MutablePatch): View
    fun forShaderInstance(
        editableManager: EditableManager<Show>,
        mutablePatch: MutablePatch,
        mutableShaderInstance: MutableShaderInstance
    ): View
    fun forButton(editableManager: EditableManager<Show>, mutableButtonControl: MutableButtonControl): View
    fun forButtonGroup(editableManager: EditableManager<Show>, mutableButtonGroupControl: MutableButtonGroupControl): View
    fun forVisualizer(editableManager: EditableManager<Show>, mutableVisualizerControl: MutableVisualizerControl): View

    fun forTitleComponent(editableManager: EditableManager<Show>, mutablePatchHolder: MutablePatchHolder): View
}

val editorPanelViews by lazy { getEditorPanelViews() }
expect fun getEditorPanelViews(): EditorPanelViews