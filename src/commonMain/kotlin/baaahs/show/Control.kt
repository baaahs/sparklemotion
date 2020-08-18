package baaahs.show

import baaahs.Gadget
import baaahs.getBang
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableGadgetControl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

interface Control {
    fun suggestId(): String = "control"

    fun toMutable(dataSources: Map<String, DataSource>): MutableControl

    companion object {
        val serialModule = SerializersModule {
            polymorphic(Control::class) {
//        CorePlugin.NoOp::class with CorePlugin.NoOp.serializer()
                GadgetControl::class with GadgetControl.serializer()
                ButtonGroupControl::class with ButtonGroupControl.serializer()
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

    override fun toMutable(dataSources: Map<String, DataSource>): MutableGadgetControl {
        return MutableGadgetControl(gadget, dataSources.getBang(controlledDataSourceId, "data source"))
    }
}

@Serializable
@SerialName("baaahs.Core:ButtonGroup")
data class ButtonGroupControl(val title: String) : Control {
    override fun toMutable(dataSources: Map<String, DataSource>): MutableControl {
        return MutableButtonGroupControl(title)
    }
}
