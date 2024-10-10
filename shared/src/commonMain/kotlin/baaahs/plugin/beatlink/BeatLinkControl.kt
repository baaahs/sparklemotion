package baaahs.plugin.beatlink

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
@SerialName("baaahs.BeatLink:BeatLink")
data class BeatLinkControl(@Transient private val `_`: Boolean = false) : Control {
    override val title: String get() = "BeatLink"

    override fun createMutable(mutableShow: MutableShow): MutableControl {
        return MutableBeatLinkControl()
    }

    override fun open(id: String, openContext: OpenContext): OpenControl {
        return OpenBeatLinkControl(id)
    }
}

class MutableBeatLinkControl : MutableControl {
    override val title: String get() = "BeatLink"

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return emptyList()
    }

    override fun buildControl(showBuilder: ShowBuilder): BeatLinkControl {
        return BeatLinkControl()
    }

    override fun previewOpen(): OpenControl {
        return OpenBeatLinkControl(randomId(title.camelize()))
    }
}

class OpenBeatLinkControl(
    override val id: String
) : OpenControl {
    override fun getState(): Map<String, JsonElement>? = null

    override fun applyState(state: Map<String, JsonElement>) {}

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableBeatLinkControl()
    }

    override fun getView(controlProps: ControlProps): View =
        beatLinkViews.forControl(this, controlProps)
}