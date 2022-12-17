package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.Feed
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
@SerialName("baaahs.Core:Vacuity")
data class VacuityControl(
    /** The name for this vacuity. */
    override val title: String
) : Control {
    override fun suggestId(): String = "vacuity"

    override fun createMutable(mutableShow: MutableShow): MutableVacuityControl =
        MutableVacuityControl(title)

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl =
        OpenVacuityControl(id, title)
}

data class MutableVacuityControl(
    /** The name for this vacuity. */
    override var title: String
) : MutableControl {
    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return emptyList()
    }

    override fun buildControl(showBuilder: ShowBuilder): VacuityControl = VacuityControl(title)

    override fun previewOpen(): OpenVacuityControl {
        return OpenVacuityControl(randomId(title.camelize()), title)
    }
}

class OpenVacuityControl(
    override val id: String,
    val title: String
) : OpenControl {
    override fun getState(): Map<String, JsonElement> = emptyMap()

    override fun applyState(state: Map<String, JsonElement>) {}

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableVacuityControl(title)
    }

    override fun controlledDataSources(): Set<Feed> = emptySet()

    override fun getView(controlProps: ControlProps): View =
        controlViews.forVacuity(this, controlProps)
}