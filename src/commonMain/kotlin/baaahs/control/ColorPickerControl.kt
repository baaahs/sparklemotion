package baaahs.control

import baaahs.Color
import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
import baaahs.gadgets.ColorPicker
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
@SerialName("baaahs.Core:ColorPicker")
data class ColorPickerControl(
    /** The name for this color picker. */
    override val title: String,

    /** The initial value for this color picker. */
    val initialValue: Color = Color.WHITE,

    override val controlledDataSourceId: String
) : Control {
    override fun createMutable(mutableShow: MutableShow): MutableColorPickerControl {
        return MutableColorPickerControl(
            title, initialValue,
            mutableShow.findDataSource(controlledDataSourceId).feed
        )
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        val controlledDataSource = openContext.getDataSource(controlledDataSourceId)
        val colorPicker = ColorPicker(title, initialValue)
        showPlayer.registerGadget(id, colorPicker, controlledDataSource)
        return OpenColorPickerControl(id, colorPicker, controlledDataSource)
    }
}

data class MutableColorPickerControl(
    /** The name for this color picker. */
    override var title: String,

    /** The initial value for this color picker. */
    val initialValue: Color = Color.WHITE,

    val controlledFeed: Feed
) : MutableControl {
    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return emptyList()
    }

    override fun buildControl(showBuilder: ShowBuilder): ColorPickerControl {
        return ColorPickerControl(
            title, initialValue, showBuilder.idFor(controlledFeed)
        )
    }

    override fun previewOpen(): OpenColorPickerControl {
        val colorPicker = ColorPicker(title, initialValue)
        return OpenColorPickerControl(randomId(title.camelize()), colorPicker, controlledFeed)
    }
}

class OpenColorPickerControl(
    override val id: String,
    val colorPicker: ColorPicker,
    override val controlledFeed: Feed
) : DataSourceOpenControl() {
    override val gadget: ColorPicker
        get() = colorPicker

    override fun getState(): Map<String, JsonElement> = colorPicker.state

    override fun applyState(state: Map<String, JsonElement>) = colorPicker.applyState(state)

    override fun resetToDefault() {
        colorPicker.color = colorPicker.initialValue
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableColorPickerControl(
            colorPicker.title, colorPicker.initialValue, controlledFeed
        )
    }

    override fun controlledDataSources(): Set<Feed> =
        setOf(controlledFeed)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forColorPicker(this, controlProps)
}