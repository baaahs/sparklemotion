package baaahs.show.live

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.app.ui.editor.ControlEditIntent
import baaahs.app.ui.editor.Editor
import baaahs.control.*
import baaahs.plugin.core.OpenTransitionControl
import baaahs.show.DataSource
import baaahs.show.Panel
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableIGridLayout
import baaahs.show.mutable.MutableShow
import baaahs.ui.View
import kotlinx.serialization.json.JsonElement

interface OpenControl {
    val id: String
    val gadget: Gadget?
        get() = null

    fun isActive(): Boolean = true
    fun getState(): Map<String, JsonElement>?
    fun applyState(state: Map<String, JsonElement>)
    fun controlledDataSources(): Set<DataSource> = emptySet()
    fun addTo(builder: ActivePatchSet.Builder, depth: Int, layout: OpenGridLayout?) {}
    fun legacyAddTo(builder: ActivePatchSet.Builder, panel: Panel, depth: Int) {}
    fun applyConstraints() {}
    fun resetToDefault() {}
    fun toNewMutable(mutableShow: MutableShow): MutableControl
    fun getView(controlProps: ControlProps): View
    fun getEditIntent(): ControlEditIntent? = ControlEditIntent(id)
    fun initPatchMods(showPlayer: ShowPlayer) {}
}

abstract class DataSourceOpenControl : OpenControl {
    abstract val controlledDataSource: DataSource

    var inUse: Boolean = false

    override fun controlledDataSources(): Set<DataSource> = setOf(controlledDataSource)
}

interface ControlContainer {
    fun containedControls() : List<OpenControl>
}

interface ControlViews {
    fun forButton(openButtonControl: OpenButtonControl, controlProps: ControlProps): View
    fun forButtonGroup(openButtonGroupControl: OpenButtonGroupControl, controlProps: ControlProps): View
    fun forColorPicker(openColorPickerControl: OpenColorPickerControl, controlProps: ControlProps): View
    fun forImagePicker(openImagePickerControl: OpenImagePickerControl, controlProps: ControlProps): View
    fun forSlider(openSlider: OpenSliderControl, controlProps: ControlProps): View
    fun forTransition(openTransitionControl: OpenTransitionControl, controlProps: ControlProps): View
    fun forVacuity(openVacuityControl: OpenVacuityControl, controlProps: ControlProps): View
    fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps): View
    fun forXyPad(openXyPadControl: OpenXyPadControl, controlProps: ControlProps): View
}

class ControlProps(
    val onShowStateChange: () -> Unit,
    val controlDisplay: ControlDisplay?,
    val layout: OpenGridLayout? = null,
    val layoutEditor: Editor<MutableIGridLayout>? = null
) {
    val relevantUnplacedControls get() =
        controlDisplay?.relevantUnplacedControls
            ?: emptyList()

    fun withLayout(
        layout: OpenGridLayout?,
        editor: Editor<MutableIGridLayout>?
    ): ControlProps =
        ControlProps(onShowStateChange, controlDisplay, layout, editor)
}

val controlViews by lazy { getControlViews() }
expect fun getControlViews(): ControlViews