package baaahs.control

import baaahs.Color
import baaahs.ShowPlayer
import baaahs.app.ui.EditorPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
import baaahs.gadgets.ColorPicker
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.live.controlViews
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
    /** The name for this slider. */
    override val title: String,

    /** The initial value for this slider. */
    val initialValue: Color = Color.WHITE,

    val controlledDataSourceId: String
) : Control {
    override fun suggestId(): String = controlledDataSourceId + "Control"

    override fun createMutable(mutableShow: MutableShow): MutableColorPickerControl {
        return MutableColorPickerControl(
            title, initialValue,
            mutableShow.findDataSource(controlledDataSourceId).dataSource
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

    val controlledDataSource: DataSource
) : MutableControl {
    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager): List<EditorPanel> {
        return emptyList()
    }

    override fun build(showBuilder: ShowBuilder): ColorPickerControl {
        return ColorPickerControl(
            title, initialValue, showBuilder.idFor(controlledDataSource)
        )
    }

    override fun previewOpen(): OpenColorPickerControl {
        val colorPicker = ColorPicker(title, initialValue)
        return OpenColorPickerControl(randomId(title.camelize()), colorPicker, controlledDataSource)
    }
}

class OpenColorPickerControl(
    override val id: String,
    val colorPicker: ColorPicker,
    val controlledDataSource: DataSource
) : OpenControl {
    override fun getState(): Map<String, JsonElement> = colorPicker.state

    override fun applyState(state: Map<String, JsonElement>) = colorPicker.applyState(state)

    override fun resetToDefault() {
        colorPicker.color = colorPicker.initialValue
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableColorPickerControl(
            colorPicker.title, colorPicker.initialValue, controlledDataSource
        )
    }

    override fun controlledDataSources(): Set<DataSource> =
        setOf(controlledDataSource)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forColorPicker(this, controlProps)
}