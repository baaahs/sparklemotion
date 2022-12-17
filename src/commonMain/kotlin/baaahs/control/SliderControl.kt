package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.GenericPropertiesEditorPanel
import baaahs.app.ui.editor.SliderPropsEditor
import baaahs.camelize
import baaahs.gadgets.Slider
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.Feed
import baaahs.show.live.*
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:Slider")
data class SliderControl(
    /** The name for this slider. */
    override val title: String,

    /** The initial value for this slider. */
    val initialValue: Float = 1f,

    /** The minimum value for this slider. */
    val minValue: Float = 0f,

    /** The maximum value for this slider. */
    val maxValue: Float = 1f,

    /** The step value for the slider. */
    val stepValue: Float? = null,

    override val controlledDataSourceId: String
) : Control {
    override fun createMutable(mutableShow: MutableShow): MutableSliderControl {
        return MutableSliderControl(
            title, initialValue, minValue, maxValue, stepValue,
            mutableShow.findDataSource(controlledDataSourceId).feed
        )
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        val controlledDataSource = openContext.getDataSource(controlledDataSourceId)
        val slider = Slider(title, initialValue, minValue, maxValue, stepValue)
        showPlayer.registerGadget(id, slider, controlledDataSource)
        return OpenSliderControl(id, slider, controlledDataSource)
    }
}

data class MutableSliderControl(
    /** The name for this slider. */
    override var title: String,

    /** The initial value for this slider. */
    var initialValue: Float = 1f,

    /** The minimum value for this slider. */
    var minValue: Float = 0f,

    /** The maximum value for this slider. */
    var maxValue: Float = 1f,

    /** The step value for the slider. */
    var stepValue: Float? = null,

    val controlledFeed: Feed
) : MutableControl {
    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> = listOf(
        GenericPropertiesEditorPanel(
            editableManager,
            SliderPropsEditor(this)
        )
    )

    override fun buildControl(showBuilder: ShowBuilder): SliderControl {
        return SliderControl(
            title, initialValue, minValue, maxValue, stepValue,
            showBuilder.idFor(controlledFeed)
        )
    }

    override fun previewOpen(): OpenSliderControl {
        val slider = Slider(title, initialValue, minValue, maxValue, stepValue)
        return OpenSliderControl(randomId(title.camelize()), slider, controlledFeed)
    }
}

class OpenSliderControl(
    override val id: String,
    val slider: Slider,
    override val controlledFeed: Feed
) : DataSourceOpenControl() {
    override val gadget: Slider
        get() = slider

    override fun getState(): Map<String, JsonElement> = slider.state

    override fun applyState(state: Map<String, JsonElement>) = slider.applyState(state)

    override fun resetToDefault() {
        slider.position = slider.initialValue
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableSliderControl(
            slider.title, slider.initialValue, slider.minValue, slider.maxValue, slider.stepValue, controlledFeed
        )
    }

    override fun controlledDataSources(): Set<Feed> =
        setOf(controlledFeed)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forSlider(this, controlProps)
}