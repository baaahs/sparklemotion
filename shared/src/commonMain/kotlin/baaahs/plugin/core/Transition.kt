package baaahs.plugin.core

import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
import baaahs.randomId
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
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:Transition")
data class TransitionControl(@Transient private val `_`: Boolean = false) : Control {
    override val title: String get() = "Transition"

    override fun createMutable(mutableShow: MutableShow): MutableControl {
        return MutableTransitionControl()
    }

    override fun open(id: String, openContext: OpenContext): OpenControl {
        return OpenTransitionControl(id)
    }
}

class MutableTransitionControl : MutableControl {
    override val title: String get() = "Transition"

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return emptyList()
    }

    override fun buildControl(showBuilder: ShowBuilder): Control {
        return TransitionControl()
    }

    override fun previewOpen(): OpenControl {
        return OpenTransitionControl(randomId(title.camelize()))
    }
}

class OpenTransitionControl(
    override val id: String
) : OpenControl {
    override fun getState(): Map<String, JsonElement>? = null

    override fun applyState(state: Map<String, JsonElement>) {}

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableTransitionControl()
    }

    override fun getView(controlProps: ControlProps): View =
        controlViews.forTransition(this, controlProps)
}