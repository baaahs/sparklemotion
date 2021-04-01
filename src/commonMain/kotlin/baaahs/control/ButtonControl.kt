package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.editor.ButtonPropsEditor
import baaahs.app.ui.editor.PropsEditor
import baaahs.camelize
import baaahs.gadgets.Switch
import baaahs.show.*
import baaahs.show.live.*
import baaahs.show.mutable.*
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("baaahs.Core:Button")
data class ButtonControl(
    override val title: String,
    val activationType: ActivationType = ActivationType.Toggle,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap()
) : PatchHolder, Control {

    enum class ActivationType {
        Toggle,
        Momentary
    }

    override fun suggestId(): String = title.camelize() + "Button"

    override fun createMutable(mutableShow: MutableShow): MutableButtonControl {
        return MutableButtonControl(this, mutableShow)
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenButtonControl {
        return OpenButtonControl(id, this, openContext)
            .also { showPlayer.registerGadget(id, it.gadget) }
    }
}

class MutableButtonControl(
    baseButtonControl: ButtonControl,
    override val mutableShow: MutableShow
) : MutablePatchHolder(baseButtonControl), MutableControl {
    var activationType: ButtonControl.ActivationType = baseButtonControl.activationType

    override var asBuiltId: String? = null

    override fun getPropertiesComponents(): List<PropsEditor> {
        return super.getPropertiesComponents() + ButtonPropsEditor(this)
    }

    override fun build(showBuilder: ShowBuilder): Control {
        return ButtonControl(
            title,
            activationType,
            patches = patches.map { it.build(showBuilder) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder)
        )
    }

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        super<MutablePatchHolder>.accept(visitor, log)
        super<MutableControl>.accept(visitor, log)
    }
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

    override fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, panel: Panel, depth: Int) {
        if (isPressed) {
            addTo(activePatchSetBuilder, depth)
        }
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        MutableButtonControl(buttonControl, mutableShow)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forButton(this, controlProps)

    fun click() {
        isPressed = !isPressed
    }
}
