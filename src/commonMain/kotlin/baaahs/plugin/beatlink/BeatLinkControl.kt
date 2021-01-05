package baaahs.plugin.beatlink

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.app.ui.EditorPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.show.Control
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenContext
import baaahs.show.live.OpenControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableShow
import baaahs.show.mutable.ShowBuilder
import baaahs.ui.Renderer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.BeatLink:BeatLink")
data class BeatLinkControl(@Transient private val `_`: Boolean = false) : Control {
    override val title: String get() = "BeatLink"

    override fun createMutable(mutableShow: MutableShow): MutableControl {
        return MutableBeatLinkControl()
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        return OpenBeatLinkControl(id, showPlayer)
    }
}

class MutableBeatLinkControl : MutableControl {
    override val title: String get() = "BeatLink"

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager): List<EditorPanel> {
        return emptyList()
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return BeatLinkControl()
    }

}

class OpenBeatLinkControl(
    override val id: String,
    private val showPlayer: ShowPlayer
) : OpenControl {
    override val gadget: Gadget?
        get() = null

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableBeatLinkControl()
    }

    override fun getRenderer(controlProps: ControlProps): Renderer =
        beatLinkViews.forControl(this, controlProps)
}