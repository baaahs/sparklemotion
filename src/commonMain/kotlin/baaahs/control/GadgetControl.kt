package baaahs.control

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.app.ui.EditIntent
import baaahs.app.ui.EditorPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.camelize
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

@Serializable
@SerialName("baaahs.Core:Gadget")
data class GadgetControl(
    val gadget: Gadget,
    val controlledDataSourceId: String
) : Control {
    override val title: String
        get() = gadget.title

    override fun suggestId(): String = controlledDataSourceId + "Control"

    override fun createMutable(mutableShow: MutableShow): MutableGadgetControl {
        return MutableGadgetControl(gadget, mutableShow.findDataSource(controlledDataSourceId).dataSource)
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        val controlledDataSource = openContext.getDataSource(controlledDataSourceId)
        showPlayer.registerGadget(id, gadget, controlledDataSource)
        return OpenGadgetControl(id, gadget, controlledDataSource)
    }
}

data class MutableGadgetControl(
    var gadget: Gadget,
    val controlledDataSource: DataSource
) : MutableControl {
    override val title: String
        get() = gadget.title

    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager): List<EditorPanel> {
        return emptyList()
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return GadgetControl(gadget, showBuilder.idFor(controlledDataSource))
    }

    fun open(): OpenControl {
        return OpenGadgetControl(randomId(gadget.title.camelize()), gadget, controlledDataSource)
    }
}
class OpenGadgetControl(
    override val id: String,
    override val gadget: Gadget,
    val controlledDataSource: DataSource
) : OpenControl {
    override fun controlledDataSources(): Set<DataSource> =
        setOf(controlledDataSource)

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        MutableGadgetControl(gadget, controlledDataSource)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forGadget(this, controlProps)

    override fun getEditIntent(): EditIntent? = null
}
