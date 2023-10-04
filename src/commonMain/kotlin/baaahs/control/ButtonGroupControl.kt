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
import baaahs.show.live.*
import baaahs.show.mutable.*
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:ButtonGroup")
data class ButtonGroupControl(
    override val title: String,
    val direction: Direction = Direction.Horizontal,
    val showTitle: Boolean? = false,
    val allowMultiple: Boolean? = false,
    val buttonIds: List<String>
) : Control {

    enum class Direction {
        Horizontal,
        Vertical
    }

    override fun createMutable(mutableShow: MutableShow): MutableButtonGroupControl =
        MutableButtonGroupControl(
            title, direction, showTitle, allowMultiple,
            buttonIds.map {
                mutableShow.findControl(it) as MutableButtonControl
            }.toMutableList(), mutableShow
        )

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenButtonGroupControl {
        return OpenButtonGroupControl(id, this, openContext)
    }
}

data class MutableButtonGroupControl(
    override var title: String,
    var direction: ButtonGroupControl.Direction = ButtonGroupControl.Direction.Vertical,
    var showTitle: Boolean? = false,
    var allowMultiple: Boolean? = false,
    val buttons: MutableList<MutableButtonControl> = arrayListOf(),
    val mutableShow: MutableShow
) : MutableControl {
    override var asBuiltId: String? = null
    override val hasInternalLayout: Boolean
        get() = true

    fun addButton(title: String, block: MutableButtonControl.() -> Unit): MutableButtonControl {
        val control = MutableButtonControl(ButtonControl(title), mutableShow)
        control.block()
        buttons.add(control)
        return control
    }

    override fun getEditorPanels(editableManager: EditableManager<*>): List<DialogPanel> {
        return listOf(
            GenericPropertiesEditorPanel(
                editableManager,
                ButtonGroupPropsEditor(this)
            )
        )
    }

    override fun buildControl(showBuilder: ShowBuilder): ButtonGroupControl =
        ButtonGroupControl(title, direction, showTitle, allowMultiple,
            buttons.map { mutableButtonControl ->
                mutableButtonControl.buildAndStashId(showBuilder)
            }
        )

    override fun previewOpen(): OpenControl {
        val buttonGroupControl = buildControl(ShowBuilder())
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

    val showTitle: Boolean = buttonGroupControl.showTitle ?: false

    override fun getState(): Map<String, JsonElement>? = null

    override fun applyState(state: Map<String, JsonElement>) {}

    val direction = buttonGroupControl.direction

    val buttons = buttonGroupControl.buttonIds.map {
        openContext.getControl(it) as OpenButtonControl
    }

    val allowMultiple get() = buttonGroupControl.allowMultiple == true

    override fun containedControls(): List<OpenControl> = buttons

    override fun applyConstraints() {
        if (allowMultiple) return

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

    override fun legacyAddTo(builder: ActivePatchSet.Builder, panel: Panel, depth: Int) {
        buttons.forEach { it.legacyAddTo(builder, panel, depth + 1) }
    }

    override fun addTo(builder: ActivePatchSet.Builder, depth: Int, layout: OpenGridLayout?) {
        layout?.items?.forEach { item ->
            item.control.addTo(builder, depth + 1, item.layout)
        }
    }
}