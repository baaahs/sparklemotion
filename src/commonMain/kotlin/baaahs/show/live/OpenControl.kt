package baaahs.show.live

import baaahs.Gadget
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.gadgets.Switch
import baaahs.show.ButtonControl
import baaahs.show.ButtonGroupControl
import baaahs.show.DataSource
import baaahs.show.ShowContext
import baaahs.show.mutable.EditHandler
import baaahs.show.mutable.MutableButtonControl
import baaahs.show.mutable.MutableButtonGroupControl
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull

interface OpenControl {
    val id: String
    val gadget: Gadget?
    fun isActive(): Boolean = true
    fun getState(): Map<String, JsonElement>? = gadget?.state
    fun applyState(state: Map<String, JsonElement>) = gadget?.applyState(state)
    fun controlledDataSources(): Set<DataSource> = emptySet()
    fun addTo(activeSetBuilder: ActiveSetBuilder, panelId: String, depth: Int) {}
    fun applyConstraints() {}
}

class OpenGadgetControl(
    override val id: String,
    override val gadget: Gadget,
    val controlledDataSource: DataSource
) : OpenControl {
    override fun controlledDataSources(): Set<DataSource> = setOf(controlledDataSource)
}

class OpenButtonControl(
    override val id: String,
    buttonControl: ButtonControl,
    openContext: OpenContext
) : OpenPatchHolder(buttonControl, openContext), OpenControl {
    override val gadget: Switch = Switch(buttonControl.title)

    var isPressed: Boolean
        get() = gadget.enabled
        set(value) { gadget.enabled = value }

    override fun isActive(): Boolean = isPressed

    override fun addTo(activeSetBuilder: ActiveSetBuilder, panelId: String, depth: Int) {
        if (isPressed) {
            addTo(activeSetBuilder, depth)
        }
    }

    fun click() {
        isPressed = !isPressed
    }
}

class OpenButtonGroupControl(
    override val id: String,
    private val buttonGroupControl: ButtonGroupControl,
    openContext: OpenContext
) : OpenControl, ControlContainer {
    val title: String
        get() = buttonGroupControl.title

    override val gadget: Gadget?
        get() = null

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

    fun clickOn(buttonIndex: Int) {
        buttons.forEachIndexed { index, openButtonControl ->
            openButtonControl.isPressed = index == buttonIndex
        }
    }

    override fun addTo(activeSetBuilder: ActiveSetBuilder, panelId: String, depth: Int) {
        buttons.forEach { it.addTo(activeSetBuilder, panelId, depth + 1) }
    }
}

class ButtonGroupDropTarget(
    private val show: OpenShow,
    private val buttonGroupControl: OpenButtonGroupControl,
    private val editHandler: EditHandler
) : DropTarget {
    override val type: String get() = "ControlContainer"
    private val myDraggable = object : Draggable {}

    override fun moveDraggable(fromIndex: Int, toIndex: Int) {
        show.edit {
            val mutableControl = findControl(buttonGroupControl.id) as MutableButtonGroupControl
            mutableControl.moveButton(fromIndex, toIndex)
        }.also { editor ->
            editHandler.onShowEdit(editor)
        }
    }

    override fun willAccept(draggable: Draggable): Boolean {
        return draggable == myDraggable
    }

    // Scenes can only be moved within a single SceneList.
    override fun getDraggable(index: Int): Draggable = error("not implemented")

    // Scenes can only be moved within a single SceneList.
    override fun insertDraggable(draggable: Draggable, index: Int): Unit = error("not implemented")

    // Scenes can only be moved within a single SceneList.
    override fun removeDraggable(draggable: Draggable): Unit = error("not implemented")
}

interface ControlContainer {
    fun containedControls() : List<OpenControl>
}