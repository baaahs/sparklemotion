package baaahs.gadgets

import baaahs.Gadget
import baaahs.clamp
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.random.Random

/** A gadget for adjusting a scalar numeric value using a slider. */
@Serializable
@SerialName("baaahs.Core:Slider")
data class Slider(
    /** The name for this slider. */
    override val title: String,

    /** The initial value for this slider. */
    val initialValue: Float = 1f,

    /** The minimum value for this slider. */
    val minValue: Float = 0f,

    /** The maximum value for this slider. */
    val maxValue: Float = 1f,

    /** The step value for the slider. */
    val stepValue: Float? = null
) : Gadget() {
    /** The selected value. */
    var position: Float by updatable("position", initialValue, Float.serializer())

    override fun adjustALittleBit() {
        val factor = .125f
        val spread = maxValue - minValue
        val amount = (Random.nextFloat() - .5f) * spread * factor
        position = (position + amount).clamp(minValue, maxValue)
    }
}