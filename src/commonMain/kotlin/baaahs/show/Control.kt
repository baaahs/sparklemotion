package baaahs.show

import baaahs.Gadget
import baaahs.show.live.OpenButtonGroupControl
import baaahs.show.live.ShowContext
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenGadgetControl
import baaahs.show.mutable.EditContext
import baaahs.show.mutable.MutableButtonGroupControl
import baaahs.show.mutable.MutableControl
import baaahs.show.mutable.MutableGadgetControl
import baaahs.util.ReferableWithId
import baaahs.util.ReferableWithIdImpl
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.modules.SerializersModule

interface Control : ReferableWithId {
    override fun suggestId(): String = "control"

    fun toMutable(editContext: EditContext): MutableControl
    fun open(showContext: ShowContext): OpenControl

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
    val controlledDataSource: DataSource
) : Control, ReferableWithIdImpl() {
    override fun suggestId(): String = controlledDataSource.suggestId() + "Control"

    override fun toMutable(editContext: EditContext): MutableGadgetControl {
        return MutableGadgetControl(gadget, controlledDataSource)
    }

    override fun open(showContext: ShowContext): OpenControl =
        OpenGadgetControl(gadget, controlledDataSource)
}

@Serializable
@SerialName("baaahs.Core:ButtonGroup")
data class ButtonGroupControl(val title: String) : Control, ReferableWithIdImpl() {
    override fun toMutable(editContext: EditContext): MutableControl {
        return MutableButtonGroupControl(title)
    }

    override fun open(showContext: ShowContext): OpenControl {
        return OpenButtonGroupControl(title)
    }
}
