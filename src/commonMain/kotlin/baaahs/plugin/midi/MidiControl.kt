package baaahs.plugin.midi

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Midi:Midi")
data class MidiControl(@Transient private val `_`: Boolean = false) : Control {
    override val title: String get() = "Midi"

    override fun createMutable(mutableShow: MutableShow): MutableControl {
        return MutableMidiControl()
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        return OpenMidiControl(id)
    }
}

class MutableMidiControl : MutableControl {
    override val title: String get() = "Midi"

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return emptyList()
    }

    override fun buildControl(showBuilder: ShowBuilder): MidiControl {
        return MidiControl()
    }

    override fun previewOpen(): OpenControl {
        return OpenMidiControl(randomId(title.camelize()))
    }
}

class OpenMidiControl(
    override val id: String
) : OpenControl {
    override fun getState(): Map<String, JsonElement>? = null

    override fun applyState(state: Map<String, JsonElement>) {}

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableMidiControl()
    }

    override fun getView(controlProps: ControlProps): View =
        midiViews.forControl(this, controlProps)
}