package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.GenericPropertiesEditorPanel
import baaahs.camelize
import baaahs.gadgets.Select
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
@SerialName("baaahs.Core:Select")
data class SelectControl(
    /** The name for this select. */
    override val title: String,

    val options: List<Pair<Int, String>>,

    val initialSelectionIndex: Int,

    override val controlledFeedId: String
) : Control {
    override fun createMutable(mutableShow: MutableShow): MutableSelectControl {
        return MutableSelectControl(
            title, options.toMutableList(), initialSelectionIndex,
            mutableShow.findFeed(controlledFeedId).feed
        )
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        val controlledFeed = openContext.getFeed(controlledFeedId)
        val select = Select(title, options, initialSelectionIndex)
        showPlayer.registerGadget(id, select, controlledFeed)
        return OpenSelectControl(id, select, controlledFeed)
    }
}

data class MutableSelectControl(
    /** The name for this select. */
    override var title: String,

    val options: MutableList<Pair<Int, String>>,

    var initialSelectionIndex: Int,

    val controlledFeed: Feed
) : MutableControl {
    override var asBuiltId: String? = null

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> = listOf(
        GenericPropertiesEditorPanel(
            editableManager,
//            SelectPropsEditor(this)
        )
    )

    override fun buildControl(showBuilder: ShowBuilder): SelectControl {
        return SelectControl(
            title, options, initialSelectionIndex,
            showBuilder.idFor(controlledFeed)
        )
    }

    override fun previewOpen(): OpenSelectControl {
        val select = Select(title, options, initialSelectionIndex)
        return OpenSelectControl(randomId(title.camelize()), select, controlledFeed)
    }
}

class OpenSelectControl(
    override val id: String,
    val select: Select,
    override val controlledFeed: Feed
) : FeedOpenControl() {
    override val gadget: Select
        get() = select

    override fun getState(): Map<String, JsonElement> = select.state

    override fun applyState(state: Map<String, JsonElement>) = select.applyState(state)

    override fun resetToDefault() {
        select.selectionIndex = select.initialSelectionIndex
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl {
        return MutableSelectControl(
            select.title, select.options.toMutableList(), select.initialSelectionIndex, controlledFeed
        )
    }

    override fun controlledFeeds(): Set<Feed> =
        setOf(controlledFeed)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forSelect(this, controlProps)
}