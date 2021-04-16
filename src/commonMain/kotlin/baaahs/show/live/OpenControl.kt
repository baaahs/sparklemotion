package baaahs.show.live

import baaahs.Gadget
import baaahs.app.ui.ControlEditIntent
import baaahs.app.ui.EditIntent
import baaahs.control.OpenButtonControl
import baaahs.control.OpenButtonGroupControl
import baaahs.control.OpenGadgetControl
import baaahs.control.OpenVisualizerControl
import baaahs.plugin.core.OpenTransitionControl
import baaahs.show.DataSource
import baaahs.show.Panel
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.ui.View
import kotlinx.serialization.json.JsonElement

interface OpenControl {
    val id: String
    val gadget: Gadget?
    fun isActive(): Boolean = true
    fun getState(): Map<String, JsonElement>? = gadget?.state
    fun applyState(state: Map<String, JsonElement>) = gadget?.applyState(state)
    fun controlledDataSources(): Set<DataSource> = emptySet()
    fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, panel: Panel, depth: Int) {}
    fun applyConstraints() {}
    fun toNewMutable(mutableShow: MutableShow): MutableControl
    fun getView(controlProps: ControlProps): View
    fun getEditIntent(): EditIntent? = ControlEditIntent(id)
}

interface ControlContainer {
    fun containedControls() : List<OpenControl>
}

interface ControlViews {
    fun forGadget(openGadgetControl: OpenGadgetControl, controlProps: ControlProps): View
    fun forButton(openButtonControl: OpenButtonControl, controlProps: ControlProps): View
    fun forButtonGroup(openButtonGroupControl: OpenButtonGroupControl, controlProps: ControlProps): View
    fun forTransition(openTransitionControl: OpenTransitionControl, controlProps: ControlProps): View
    fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps): View
}

data class ControlProps(
    val onShowStateChange: () -> Unit,
    val editMode: Boolean,
    val controlDisplay: ControlDisplay?
)

val controlViews by lazy { getControlViews() }
expect fun getControlViews(): ControlViews