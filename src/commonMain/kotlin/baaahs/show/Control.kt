package baaahs.show

import baaahs.Gadget
import baaahs.ShowPlayer
import baaahs.camelize
import baaahs.show.live.*
import baaahs.show.mutable.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic

interface Control {
    fun suggestId(): String = "control"

    fun toMutable(mutableShow: MutableShow): MutableControl
    fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl

    companion object {
        val serialModule = SerializersModule {
            polymorphic(Control::class) {
//        CorePlugin.NoOp::class with CorePlugin.NoOp.serializer()
                subclass(GadgetControl::class, GadgetControl.serializer())
                subclass(ButtonControl::class, ButtonControl.serializer())
                subclass(ButtonGroupControl::class, ButtonGroupControl.serializer())
            }
        }
    }
}

@Serializable
@SerialName("baaahs.Core:Gadget")
data class GadgetControl(
    val gadget: Gadget,
    val controlledDataSourceId: String
) : Control {
    override fun suggestId(): String = controlledDataSourceId + "Control"

    override fun toMutable(mutableShow: MutableShow): MutableGadgetControl {
        return MutableGadgetControl(gadget, mutableShow.findDataSource(controlledDataSourceId).dataSource)
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenControl {
        val controlledDataSource = openContext.getDataSource(controlledDataSourceId)
        showPlayer.registerGadget(id, gadget, controlledDataSource)
        return OpenGadgetControl(id, gadget, controlledDataSource)
    }
}

@Serializable
@SerialName("baaahs.Core:Button")
data class ButtonControl(
    override val title: String,
    override val patches: List<Patch> = emptyList(),
    override val eventBindings: List<EventBinding> = emptyList(),
    override val controlLayout: Map<String, List<String>> = emptyMap()
) : PatchHolder, Control {
    override fun suggestId(): String = title.camelize() + "Button"

    override fun toMutable(mutableShow: MutableShow): MutableButtonControl {
        return MutableButtonControl(this, mutableShow)
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenButtonControl {
        return OpenButtonControl(id, this, openContext)
            .also { showPlayer.registerGadget(id, it.gadget) }
    }
}

@Serializable
@SerialName("baaahs.Core:ButtonGroup")
data class ButtonGroupControl(
    val title: String,
    val direction: Direction,
    val buttonIds: List<String>
) : Control {

    enum class Direction {
        Horizontal,
        Vertical
    }

    override fun suggestId(): String = title.camelize() + "ButtonGroup"

    override fun toMutable(mutableShow: MutableShow): MutableButtonGroupControl {
        return MutableButtonGroupControl(title, direction, buttonIds.map {
            mutableShow.findControl(it) as MutableButtonControl
        }.toMutableList(), mutableShow)
    }

    override fun open(id: String, openContext: OpenContext, showPlayer: ShowPlayer): OpenButtonGroupControl {
        return OpenButtonGroupControl(id, this, openContext)
    }
}
