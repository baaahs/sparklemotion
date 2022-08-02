package baaahs.gadgets

import baaahs.Gadget
import baaahs.clamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.random.Random

/** A gadget for entering a text value. */
@Serializable
@SerialName("baaahs.Core:TextInput")
data class TextInput(
    /** The name for this text input. */
    override val title: String,

    /** The value for this text input. */
    val initialValue: String = "",
) : Gadget() {
    /** The selected value. */
    var value: String by updatable("value", initialValue, String.serializer())
}
