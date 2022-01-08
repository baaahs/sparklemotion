package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.dialog.DialogPanel
import baaahs.app.ui.editor.ButtonGroupPropsEditor
import baaahs.app.ui.editor.EditableManager
import baaahs.app.ui.editor.GenericPropertiesEditorPanel
import baaahs.camelize
import baaahs.randomId
import baaahs.show.Control
import baaahs.show.Panel
import baaahs.show.Show
import baaahs.show.live.*
import baaahs.show.mutable.*
import baaahs.ui.Draggable
import baaahs.ui.DropTarget
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:ButtonGroup")
data class ButtonGroupControl(
    override val title: String,
    val direction: Direction = Direction.Horizontal,
    val buttonIds: List<String>
) : Control {

    enum class Direction {
        Horizontal,
        Vertical
    }

    override fun suggestId(): String = title.camelize() + "ButtonGroup"

    override fun createMutable(mutableShow: MutableShow): MutableButtonGroupControl {
        return MutableButtonGroupControl(title, direction, buttonIds.map {
            mutableShow.findControl(it) as MutableButtonControl
        }.toMutableList(), mutableShow)
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenButtonGroupControl {
        return OpenButtonGroupControl(id, this, openContext)
    }
}

data class MutableButtonGroupControl(
    override var title: String,
    var direction: ButtonGroupControl.Direction,
    val buttons: MutableList<MutableButtonControl> = arrayListOf(),
    val mutableShow: MutableShow
) : MutableControl {
    override var asBuiltId: String? = null

    fun addButton(title: String, block: MutableButtonControl.() -> Unit): MutableButtonControl {
        val control = MutableButtonControl(ButtonControl(title), mutableShow)
        control.block()
        buttons.add(control)
        return control
    }

    override fun getEditorPanels(editableManager: EditableManager<Show>): List<DialogPanel> {
        return listOf(
            GenericPropertiesEditorPanel(
                editableManager,
                ButtonGroupPropsEditor(this)
            )
        )
    }

    override fun build(showBuilder: ShowBuilder): ButtonGroupControl {
        return ButtonGroupControl(title, direction, buttons.map { mutableButtonControl ->
            mutableButtonControl.buildAndStashId(showBuilder)
        })
    }

    override fun previewOpen(): OpenControl {
        val buttonGroupControl = build(ShowBuilder())
        return OpenButtonGroupControl(randomId(title.camelize()), buttonGroupControl, EmptyOpenContext)
    }

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        super.accept(visitor, log)
        buttons.forEach { it.accept(visitor, log) }
    }

    fun moveButton(fromIndex: Int, toIndex: Int) {
        buttons.add(toIndex, buttons.removeAt(fromIndex))
    }
}

class OpenButtonGroupControl(
    override val id: String,
    private val buttonGroupControl: ButtonGroupControl,
    openContext: OpenContext
) : OpenControl, ControlContainer {
    val title: String
        get() = buttonGroupControl.title

    override fun getState(): Map<String, JsonElement>? = null

    override fun applyState(state: Map<String, JsonElement>) {}

    val direction = buttonGroupControl.direction

    val buttons = buttonGroupControl.buttonIds.map {
        openContext.getControl(it) as OpenButtonControl
    }

    override fun containedControls(): List<OpenControl> = buttons

    override fun applyConstraints() {
        val active = buttons.map { it.isPressed }
        val countActive = active.count { it }
        if (countActive == 0) {
            buttons.firstOrNull()?.isPressed = true
        } else if (countActive > 1) {
            val firstActive = active.indexOfFirst { it }
            buttons.forEachIndexed { index, openButtonControl ->
                openButtonControl.isPressed = index == firstActive
            }
        }
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        error("not implemented for button groups")

    override fun getView(controlProps: ControlProps): View =
        controlViews.forButtonGroup(this, controlProps)

    fun clickOn(buttonIndex: Int) {
        buttons.forEachIndexed { index, openButtonControl ->
            openButtonControl.isPressed = index == buttonIndex
        }
    }

    override fun addTo(activePatchSetBuilder: ActivePatchSet.Builder, panel: Panel, depth: Int) {
        buttons.forEach { it.addTo(activePatchSetBuilder, panel, depth + 1) }
    }

    fun createDropTarget(controlDisplay: ControlDisplay) =
        ButtonGroupDropTarget(controlDisplay)

    inner class ButtonGroupDropTarget(
        private val controlDisplay: ControlDisplay
    ) : DropTarget {
        override val dropTargetId = controlDisplay.dragNDrop.addDropTarget(this)
        override val type: String get() = "ControlContainer"

        override fun moveDraggable(fromIndex: Int, toIndex: Int) {
            controlDisplay.show.edit {
                findButtonGroup()
                    .moveButton(fromIndex, toIndex)
            }.commit(controlDisplay.editHandler)
        }

        override fun willAccept(draggable: Draggable): Boolean {
            return draggable is ControlDisplay.PlaceableControl
        }

        override fun getDraggable(index: Int): Draggable = object : ControlDisplay.PlaceableControl {
            override val mutableShow: MutableShow by lazy { controlDisplay.show.edit() }
            override lateinit var mutableControl: MutableControl

            override fun willMoveTo(destination: DropTarget): Boolean = true

            override fun remove() {
                mutableControl = mutableShow.findButtonGroup()
                    .buttons
                    .removeAt(index)
            }

            override fun onMove() {
                mutableShow.commit(controlDisplay.editHandler)
            }
        }

        override fun insertDraggable(draggable: Draggable, index: Int) {
            draggable as ControlDisplay.PlaceableControl
            draggable.mutableShow.findButtonGroup()
                .buttons
                .add(index, draggable.mutableControl as MutableButtonControl)
        }

        override fun removeDraggable(draggable: Draggable) {
            draggable as ControlDisplay.PlaceableControl
            draggable.remove()
        }

        private fun MutableShow.findButtonGroup() =
            findControl(id) as MutableButtonGroupControl

        fun release() {
            controlDisplay.dragNDrop.removeDropTarget(this)
        }
    }
}
