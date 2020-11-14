package baaahs.show.live

import baaahs.Gadget
import baaahs.app.ui.ControlEditIntent
import baaahs.app.ui.EditIntent
import baaahs.gadgets.Switch
import baaahs.show.ButtonControl
import baaahs.show.ButtonGroupControl
import baaahs.show.DataSource
import baaahs.show.VisualizerControl
import baaahs.show.mutable.*
import baaahs.ui.Draggable
import baaahs.ui.DropTarget
import baaahs.ui.Renderer
import kotlinx.serialization.json.JsonElement

interface OpenControl {
    val id: String
    val gadget: Gadget?
    fun isActive(): Boolean = true
    fun getState(): Map<String, JsonElement>? = gadget?.state
    fun applyState(state: Map<String, JsonElement>) = gadget?.applyState(state)
    fun controlledDataSources(): Set<DataSource> = emptySet()
    fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, panelId: String, depth: Int) {}
    fun applyConstraints() {}
    fun toNewMutable(mutableShow: MutableShow): MutableControl
    fun getRenderer(controlProps: ControlProps): Renderer
    fun getEditIntent(): EditIntent? = ControlEditIntent(id)
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

    override fun getRenderer(controlProps: ControlProps): Renderer =
        controlViews.forGadget(this, controlProps)

    override fun getEditIntent(): EditIntent? = null
}

class OpenButtonControl(
    override val id: String,
    private val buttonControl: ButtonControl,
    openContext: OpenContext
) : OpenPatchHolder(buttonControl, openContext), OpenControl {
    override val gadget: Switch = Switch(buttonControl.title)

    val type get() = buttonControl.activationType

    var isPressed: Boolean
        get() = gadget.enabled
        set(value) { gadget.enabled = value }

    override fun isActive(): Boolean = isPressed

    override fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, panelId: String, depth: Int) {
        if (isPressed) {
            addTo(activePatchSetBuilder, depth)
        }
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        MutableButtonControl(buttonControl, mutableShow)

    override fun getRenderer(controlProps: ControlProps): Renderer =
        controlViews.forButton(this, controlProps)

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

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        error("not implemented for button groups")

    override fun getRenderer(controlProps: ControlProps): Renderer =
        controlViews.forButtonGroup(this, controlProps)

    fun clickOn(buttonIndex: Int) {
        buttons.forEachIndexed { index, openButtonControl ->
            openButtonControl.isPressed = index == buttonIndex
        }
    }

    override fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, panelId: String, depth: Int) {
        buttons.forEach { it.addTo(activePatchSetBuilder, panelId, depth + 1) }
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

class OpenVisualizerControl(
    override val id: String,
    private val visualizerControl: VisualizerControl
) : OpenControl {
    override val gadget: Gadget? get() = null

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        visualizerControl.createMutable(mutableShow)

    override fun getRenderer(controlProps: ControlProps): Renderer =
        controlViews.forVisualizer(this, controlProps)

    override fun getEditIntent(): EditIntent =
        ControlEditIntent(id)

    val rotate get() = visualizerControl.rotate
}

interface ControlContainer {
    fun containedControls() : List<OpenControl>
}

interface ControlViews {
    fun forGadget(openGadgetControl: OpenGadgetControl, controlProps: ControlProps): Renderer
    fun forButton(openButtonControl: OpenButtonControl, controlProps: ControlProps): Renderer
    fun forButtonGroup(openButtonGroupControl: OpenButtonGroupControl, controlProps: ControlProps): Renderer
    fun forVisualizer(openVisualizerControl: OpenVisualizerControl, controlProps: ControlProps): Renderer
}

data class ControlProps(
    val onShowStateChange: () -> Unit,
    val editMode: Boolean,
    val controlDisplay: ControlDisplay?
)

val controlViews by lazy { getControlViews() }
expect fun getControlViews(): ControlViews