package baaahs.control

import baaahs.ShowPlayer
import baaahs.app.ui.editor.ButtonPropsEditor
import baaahs.app.ui.editor.PropsEditor
import baaahs.camelize
import baaahs.gadgets.Switch
import baaahs.gl.shader.OpenShader
import baaahs.onlyOrNull
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
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap(),
    override val controlledDataSourceId: String? = null
) : PatchHolder, Control {

    enum class ActivationType {
        Toggle,
        Momentary
    }

    override fun suggestId(): String = title.camelize() + "Button"

    override fun createMutable(mutableShow: MutableShow): MutableButtonControl {
        return MutableButtonControl(
            this, mutableShow,
            controlledDataSourceId?.let { mutableShow.findDataSource(it) }?.dataSource
        )
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenButtonControl {
        val controlledDataSource = controlledDataSourceId?.let { openContext.getDataSource(it) }
        return OpenButtonControl(id, this, openContext, controlledDataSource)
            .also { showPlayer.registerGadget(id, it.switch, controlledDataSource) }
    }
}

class MutableButtonControl(
    baseButtonControl: ButtonControl,
    override val mutableShow: MutableShow,
    var controlledDataSource: DataSource? =
        baseButtonControl.controlledDataSourceId?.let { mutableShow.findDataSource(it) }?.dataSource
) : MutablePatchHolder(baseButtonControl), MutableControl {
    var activationType: ButtonControl.ActivationType = baseButtonControl.activationType

    override var asBuiltId: String? = null

    override fun getPropertiesComponents(): List<PropsEditor> {
        return super.getPropertiesComponents() + ButtonPropsEditor(this)
    }

    override fun build(showBuilder: ShowBuilder): ButtonControl {
        return ButtonControl(
            title,
            activationType,
            patches = patches.map { it.build(showBuilder) },
            eventBindings = eventBindings,
            controlLayout = buildControlLayout(showBuilder),
            controlledDataSourceId = controlledDataSource?.let { showBuilder.idFor(it) }
        )
    }

    override fun previewOpen(): OpenControl {
        val buttonControl = build(ShowBuilder())
        return OpenButtonControl(randomId(title.camelize()), buttonControl, EmptyOpenContext, controlledDataSource)
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
    val controlledDataSource: DataSource? = buttonControl.controlledDataSourceId?.let { openContext.getDataSource(it) }
) : OpenPatchHolder(buttonControl, openContext), OpenControl {
    val switch: Switch = Switch(buttonControl.title)

    val type get() = buttonControl.activationType

    var isPressed: Boolean
        get() = switch.enabled
        set(value) { switch.enabled = value }

    override fun isActive(): Boolean = isPressed

    override fun getState(): Map<String, JsonElement> = switch.state

    override fun applyState(state: Map<String, JsonElement>) = switch.applyState(state)

    override fun addTo(activePatchSetBuilder: ActivePatchSetBuilder, panel: Panel, depth: Int) {
        if (isPressed) {
            addTo(activePatchSetBuilder, depth)
        }
    }

    override fun toNewMutable(mutableShow: MutableShow): MutableControl =
        MutableButtonControl(buttonControl, mutableShow, controlledDataSource)

    override fun getView(controlProps: ControlProps): View =
        controlViews.forButton(this, controlProps)

    fun click() {
        isPressed = !isPressed
    }

    fun shaderForPreview(): OpenShader? =
        patches.onlyOrNull()
            ?.shaderInstances?.onlyOrNull()
            ?.shader
}