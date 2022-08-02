package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
import baaahs.gadgets.TextInput
import baaahs.geom.Vector2F
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.DataSource
import baaahs.show.live.*
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:TextInput")
data class TextInputControl(
    override val title: String,

    val initialValue: String = "",

    override val controlledDataSourceId: String
) : Control {
    override fun createMutable(mutableShow: MutableShow): MutableTextInputControl {
        return MutableTextInputControl(
            title, initialValue,
            mutableShow.findDataSource(controlledDataSourceId).dataSource
        )
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenTextInputControl {
        val controlledDataSource = openContext.getDataSource(controlledDataSourceId)
        val textInput = TextInput(title, initialValue)
        return OpenTextInputControl(id, textInput, controlledDataSource)
            .also { showPlayer.registerGadget(id, textInput, controlledDataSource) }
    }
}

class MutableTextInputControl(
    override var title: String,
    var initialValue: String = "",
    var controlledDataSource: DataSource
) : MutableControl {
    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> = emptyList()

    override fun buildControl(showBuilder: ShowBuilder): TextInputControl {
        return TextInputControl(
            title, initialValue,
            showBuilder.idFor(controlledDataSource)
        )
    }

    override fun previewOpen(): OpenControl {
        val textInput = TextInput(title, initialValue)
        return OpenTextInputControl(randomId(title.camelize()), textInput, controlledDataSource)
    }
}

class OpenTextInputControl(
    override val id: String,
    val textInput: TextInput,
    override val controlledDataSource: DataSource
) : DataSourceOpenControl() {
    override val gadget: TextInput
        get() = textInput

    override fun getState(): Map<String, JsonElement> = textInput.state

    override fun applyState(state: Map<String, JsonElement>) = textInput.applyState(state)

    override fun resetToDefault() {
        textInput.value = ""
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        MutableTextInputControl(textInput.title, textInput.initialValue, controlledDataSource)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forTextInput(this, controlProps)
}
