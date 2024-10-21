package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.random.Random

/** A switch. */
@Serializable
@SerialName("baaahs.Core:Switch")
data class Switch(
    /** The label for this switch. */
    override val title: String,

    /** The initial value for this switch. */
    val initiallyEnabled: Boolean = false
) : Gadget() {

    var enabled: Boolean by updatable("enabled", initiallyEnabled, Boolean.serializer())

    override fun adjustALittleBit() {
        if (Random.nextFloat() < .05) {
            enabled = !enabled
        }
    }

    override fun adjustInRange(value: Float) {
        enabled = (value >= .5f)
    }
}