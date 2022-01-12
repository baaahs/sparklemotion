package baaahs.show.live

import baaahs.app.ui.editor.ControlEditIntent
import baaahs.app.ui.editor.EditIntent
import baaahs.control.*
import baaahs.plugin.core.OpenTransitionControl
import baaahs.show.DataSource
import baaahs.show.Panel
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.ui.View
import kotlinx.serialization.json.JsonElement

interface OpenControl {
    val id: String
    fun isActive(): Boolean = true
    fun getState(): Map<String, JsonElement>?
    fun applyState(state: Map<String, JsonElement>)
    fun controlledDataSources(): Set<DataSource> = emptySet()
    fun addTo(activePatchSetBuilder: ActivePatchSet.Builder, panel: Panel, depth: Int) {}
    fun applyConstraints() {}
    fun resetToDefault() {}
    fun toNewMutable(mutableShow: MutableShow): MutableControl
    fun getView(controlProps: ControlProps): View
    fun getEditIntent(): EditIntent? = ControlEditIntent(id)
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
    fun forSlider(openSlider: OpenSliderControl, controlProps: ControlProps): View
    fun forTransition(openTransitionControl: OpenTransitionControl, controlProps: ControlProps): View
    fun forVacuity(openVacuityControl: OpenVacuityControl, controlProps: ControlProps): View
    fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps): View
    fun forXyPad(openXyPadControl: OpenXyPadControl, controlProps: ControlProps): View
}

data class ControlProps(
    val onShowStateChange: () -> Unit,
    val editMode: Boolean,
    val controlDisplay: ControlDisplay?
)

val controlViews by lazy { getControlViews() }
expect fun getControlViews(): ControlViews