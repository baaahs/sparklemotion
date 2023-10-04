package baaahs.control

import baaahs.app.ui.editor.ButtonPropsEditor
import baaahs.app.ui.editor.PropsEditor
import baaahs.camelize
import baaahs.gadgets.Switch
import baaahs.randomId
import baaahs.show.*
import baaahs.show.live.*
import baaahs.show.mutable.*
import baaahs.ui.View
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
@SerialName("baaahs.Core:Button")
data class ButtonControl(
    override val title: String,
    val activationType: ActivationType = ActivationType.Toggle,
    override val patchIds: List<String> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    @Deprecated("Only used for legacy layout.")
    override val controlLayout: Map<String, List<String>> = emptyMap(),
    override val controlledFeedId: String? = null
) : PatchHolder, Control {

    enum class ActivationType {
        Toggle,
        Momentary
    }

    init { validatePatchHolder() }

    override fun createMutable(mutableShow: MutableShow): MutableButtonControl {
        return MutableButtonControl(
            this, mutableShow,
            controlledFeedId?.let { mutableShow.findFeed(it) }?.feed
        )
    }

    override fun open(id: String, openContext: OpenContext): OpenButtonControl {
        val controlledFeed = controlledFeedId?.let { openContext.getFeed(it) }
        return OpenButtonControl(id, this, openContext, controlledFeed)
            .also { openContext.registerGadget(id, it.switch, controlledFeed) }
    }
}

class MutableButtonControl(
    baseButtonControl: ButtonControl,
    override val mutableShow: MutableShow,
    var controlledFeed: Feed? =
        baseButtonControl.controlledFeedId?.let { mutableShow.findFeed(it) }?.feed
) : MutablePatchHolder(baseButtonControl), MutableControl {
    var activationType: ButtonControl.ActivationType = baseButtonControl.activationType

    override var asBuiltId: String? = null

    override fun getPropertiesComponents(): List<PropsEditor> {
        return super.getPropertiesComponents() + ButtonPropsEditor(this)
    }

    override fun buildControl(showBuilder: ShowBuilder): ButtonControl {
        return ButtonControl(
            title,
            activationType,
            patchIds = patches.map { showBuilder.idFor(it.build(showBuilder)) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder),
            controlledFeedId = controlledFeed?.let { showBuilder.idFor(it) }
        )
    }

    override fun previewOpen(): OpenControl {
        val buttonControl = buildControl(ShowBuilder())
        return OpenButtonControl(randomId(title.camelize()), buttonControl, EmptyOpenContext, controlledFeed)
    }

    override fun accept(visitor: MutableShowVisitor, log: VisitationLog) {
        super<MutablePatchHolder>.accept(visitor, log)
        super<MutableControl>.accept(visitor, log)
    }
}

class OpenButtonControl(
    override val id: String,
    private val buttonControl: ButtonControl,
    openContext: OpenContext,
    val controlledFeed: Feed? = buttonControl.controlledFeedId?.let { openContext.getFeed(it) }
) : OpenPatchHolder(buttonControl, openContext), OpenControl {
    val switch: Switch = Switch(buttonControl.title)
    override val gadget: Switch
        get() = switch

    val type get() = buttonControl.activationType

    var isPressed: Boolean
        get() = switch.enabled
        set(value) { switch.enabled = value }

    val expandsOnLongPress: Boolean get() = type != ButtonControl.ActivationType.Momentary

    override fun isActive(): Boolean = isPressed

    override fun getState(): Map<String, JsonElement> = switch.state

    override fun applyState(state: Map<String, JsonElement>) = switch.applyState(state)

    override fun addTo(builder: ActivePatchSet.Builder, depth: Int, layout: OpenGridLayout?) {
        if (isPressed) super<OpenPatchHolder>.addTo(builder, depth)
    }

    override fun legacyAddTo(builder: ActivePatchSet.Builder, panel: Panel, depth: Int) {
        if (isPressed) super<OpenPatchHolder>.legacyAddTo(builder, panel, depth)
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        MutableButtonControl(buttonControl, mutableShow, controlledFeed)

    override fun controlledFeeds(): Set<Feed> =
        setOfNotNull(controlledFeed)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forButton(this, controlProps)

    fun click() {
        isPressed = !isPressed
    }
}