package baaahs.show.live

import baaahs.Gadget
import baaahs.app.ui.Draggable
import baaahs.app.ui.DropTarget
import baaahs.gadgets.Switch
import baaahs.show.ButtonControl
import baaahs.show.ButtonGroupControl
import baaahs.show.DataSource
import baaahs.show.mutable.MutableButtonControl
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutableControl
import kotlinx.serialization.json.JsonElement

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

    fun createDropTarget(controlDisplay: ControlDisplay) =
        ButtonGroupDropTarget(controlDisplay)

    inner class ButtonGroupDropTarget(
        private val controlDisplay: ControlDisplay
    ) : DropTarget {
        val dropTargetId = controlDisplay.dragNDrop.addDropTarget(this)
        override val type: String get() = "ControlContainer"

        override fun moveDraggable(fromIndex: Int, toIndex: Int) {
            controlDisplay.mutableShow!!
            findMutableControl().moveButton(fromIndex, toIndex)
            commitEdit()
        }

        private fun findMutableControl() =
            controlDisplay.mutableShow!!.findControl(id) as MutableButtonGroupControl

        private fun commitEdit() {
            controlDisplay.commitEdit()
        }

        override fun willAccept(draggable: Draggable): Boolean {
            return draggable is ControlDisplay.PlaceableControl
        }

        override fun getDraggable(index: Int): Draggable = object : ControlDisplay.PlaceableControl {
            private val mutableButtonGroupControl = findMutableControl()
            override val mutableControl: MutableControl = mutableButtonGroupControl.buttons[index]

            override fun willMoveTo(destination: DropTarget): Boolean = true

            override fun remove() {
                mutableButtonGroupControl.buttons.removeAt(index)
            }

            override fun onMove() {
                commitEdit()
            }
        }

        override fun insertDraggable(draggable: Draggable, index: Int) {
            draggable as ControlDisplay.PlaceableControl
            findMutableControl().buttons.add(index, draggable.mutableControl as MutableButtonControl)
        }

        override fun removeDraggable(draggable: Draggable) {
            draggable as ControlDisplay.PlaceableControl
            draggable.remove()
        }

        fun release() {
            controlDisplay.dragNDrop.removeDropTarget(this)
        }
    }
}

interface ControlContainer {
    fun containedControls() : List<OpenControl>
}