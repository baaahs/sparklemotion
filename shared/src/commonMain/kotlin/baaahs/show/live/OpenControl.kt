package baaahs.show.live

import baaahs.Gadget
import baaahs.app.ui.editor.ControlEditIntent
import baaahs.app.ui.editor.Editor
import baaahs.control.*
import baaahs.plugin.core.OpenTransitionControl
import baaahs.show.Feed
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
    fun controlledFeeds(): Set<Feed> = emptySet()
    fun addTo(builder: ActivePatchSet.Builder, depth: Int, layout: OpenGridLayout?) {}
    fun legacyAddTo(builder: ActivePatchSet.Builder, panel: Panel, depth: Int) {}
    fun applyConstraints() {}
    fun resetToDefault() {}
    fun toNewMutable(mutableShow: MutableShow): MutableControl
    fun getView(controlProps: ControlProps): View
    fun getEditIntent(): ControlEditIntent? = ControlEditIntent(id)
    fun listenForActivePatchSetChanges(callback: () -> Unit): Unit = Unit
}

abstract class FeedOpenControl : OpenControl {
    abstract val controlledFeed: Feed

    var inUse: Boolean = false

    override fun controlledFeeds(): Set<Feed> = setOf(controlledFeed)
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
    val openShow: OpenShow,
    val layout: OpenGridLayout? = null,
    val layoutEditor: Editor<MutableIGridLayout>? = null,
    val parentDimens: GridDimens? = null
) {
    fun withLayout(
        layout: OpenGridLayout?,
        editor: Editor<MutableIGridLayout>?,
        parentDimens: GridDimens?
    ): ControlProps =
        ControlProps(openShow, layout, editor, parentDimens)
}

val controlViews by lazy { getControlViews() }
expect fun getControlViews(): ControlViews