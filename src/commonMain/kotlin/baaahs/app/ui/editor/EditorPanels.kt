package baaahs.app.ui.editor

import baaahs.app.ui.CommonIcons
import baaahs.app.ui.dialog.DialogPanel
import baaahs.control.MutableButtonControl
import baaahs.control.MutableButtonGroupControl
import baaahs.control.MutableSliderControl
import baaahs.control.MutableVisualizerControl
import baaahs.gl.openShader
import baaahs.scene.MutableScene
import baaahs.show.live.OpenIGridLayout
import baaahs.show.live.PatchResolver
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutablePatch
import baaahs.show.mutable.MutablePatchHolder
import baaahs.show.mutable.ShowBuilder
import baaahs.sm.webapi.Severity
import baaahs.sm.webapi.severity
import baaahs.ui.Icon
import baaahs.ui.View

data class SingleShaderSimplifiedEditorPanel(
    private val editableManager: EditableManager<*>,
    private val mutablePatchHolder: MutablePatchHolder
) : DialogPanel {
    override val title: String
        get() = "Simplified View: ${mutablePatchHolder.title}"
    override val noMargin: Boolean
        get() = true

    override fun getView(): View =
        editorPanelViews.forSingleShaderSimplifiedEditorPanel(editableManager, mutablePatchHolder)
}

data class GenericPropertiesEditorPanel(
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

data class PatchesOverviewPanel(
    private val editableManager: EditableManager<*>,
    private val mutablePatchHolder: MutablePatchHolder
) : DialogPanel {
    override val title: String
        get() = "Patches"
    override val icon: Icon
        get() = CommonIcons.Patch

    override fun getNestedDialogPanels(): List<DialogPanel> {
        return mutablePatchHolder.patches.map { mutablePatch ->
            mutablePatch.getEditorPanel(editableManager)
        }
    }

    override fun getView(): View =
        editorPanelViews.forPatchHolder(editableManager, mutablePatchHolder)
}

data class PatchEditorPanel(
    private val editableManager: EditableManager<*>,
    private val mutablePatch: MutablePatch
) : DialogPanel {
    override val title: String
        get() = mutablePatch.title
    override val listSubhead: String
        get() = "Shaders"
    override val icon: Icon
        get() = openShader.shaderType.icon
    override val problemLevel: Severity?
            by lazy { openPatch.problems.severity() }

    // TODO: This is a clunky way to get our cached toolchain... clean up somehow.
    val toolchain = (editableManager.session!! as ShowEditableManager.ShowSession).toolchain
    private val openShader = toolchain.openShader(mutablePatch.mutableShader.build())
    private val openPatch = run {
    val patch = mutablePatch.build(ShowBuilder())
        PatchResolver.build(openShader, patch, emptyMap(), toolchain)
    }

    override fun getView(): View =
        editorPanelViews.forPatch(editableManager, mutablePatch)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PatchEditorPanel) return false

        if (mutablePatch != other.mutablePatch) return false

        return true
    }

    override fun hashCode(): Int {
        return mutablePatch.hashCode()
    }
}

data class GridLayoutEditorPanel(
    private val editableManager: EditableManager<*>,
    private val layout: OpenIGridLayout,
    private val layoutEditor: Editor<MutableIGridLayout>
) : DialogPanel {
    override val title: String
        get() = "Properties"
    override val icon: Icon
        get() = CommonIcons.Settings

    override fun getView(): View =
        editorPanelViews.forGridLayout(editableManager, layoutEditor)
}

interface PropsEditor {
    fun getView(editableManager: EditableManager<*>): View
}

data class TitlePropsEditor(val mutablePatchHolder: MutablePatchHolder) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forTitleComponent(editableManager, mutablePatchHolder)
}

data class ButtonPropsEditor(
    val mutableButtonControl: MutableButtonControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forButton(editableManager, mutableButtonControl)
}

data class ButtonGroupPropsEditor(
    val mutableButtonGroupControl: MutableButtonGroupControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forButtonGroup(editableManager, mutableButtonGroupControl)
}

data class SliderPropsEditor(
    val mutableSliderControl: MutableSliderControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forSlider(editableManager, mutableSliderControl)
}

data class VisualizerPropsEditor(
    val mutableVisualizerControl: MutableVisualizerControl
) : PropsEditor {
    override fun getView(editableManager: EditableManager<*>): View =
        editorPanelViews.forVisualizer(editableManager, mutableVisualizerControl)
}

interface EditorPanelViews {
    fun forSingleShaderSimplifiedEditorPanel(
        editableManager: EditableManager<*>,
        mutablePatchHolder: MutablePatchHolder
    ): View
    fun forGenericPropertiesPanel(editableManager: EditableManager<*>, propsEditors: List<PropsEditor>): View
    fun forPatchHolder(editableManager: EditableManager<*>, mutablePatchHolder: MutablePatchHolder): View
    fun forPatch(editableManager: EditableManager<*>, mutablePatch: MutablePatch): View
    fun forButton(editableManager: EditableManager<*>, mutableButtonControl: MutableButtonControl): View
    fun forButtonGroup(editableManager: EditableManager<*>, mutableButtonGroupControl: MutableButtonGroupControl): View
    fun forSlider(editableManager: EditableManager<*>, mutableSliderControl: MutableSliderControl): View
    fun forVisualizer(editableManager: EditableManager<*>, mutableVisualizerControl: MutableVisualizerControl): View

    fun forTitleComponent(editableManager: EditableManager<*>, mutablePatchHolder: MutablePatchHolder): View

    fun forSceneTitleComponent(editableManager: EditableManager<*>, mutableScene: MutableScene): View
    fun forModelUnitsComponent(editableManager: EditableManager<*>, mutableScene: MutableScene): View

    fun forGridLayout(editableManager: EditableManager<*>, editor: Editor<MutableIGridLayout>): View
}

val editorPanelViews by lazy { getEditorPanelViews() }
expect fun getEditorPanelViews(): EditorPanelViews