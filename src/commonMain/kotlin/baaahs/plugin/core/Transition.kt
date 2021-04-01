package baaahs.plugin.core

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.app.ui.EditorPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.show.Control
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
import kotlinx.serialization.Transient

@Serializable
@SerialName("baaahs.Core:Transition")
data class TransitionControl(@Transient private val `_`: Boolean = false) : Control {
    override val title: String get() = "Transition"

    override fun createMutable(mutableShow: MutableShow): MutableControl {
        return MutableTransitionControl()
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        return OpenTransitionControl(id, showPlayer)
    }
}

class MutableTransitionControl : MutableControl {
    override val title: String get() = "Transition"

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager): List<EditorPanel> {
        return emptyList()
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return TransitionControl()
    }

}

class OpenTransitionControl(
    override val id: String,
    private val showPlayer: ShowPlayer
) : OpenControl {
    override val gadget: Gadget?
        get() = null

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableTransitionControl()
    }

    override fun getView(controlProps: ControlProps): View =
        controlViews.forTransition(this, controlProps)
}