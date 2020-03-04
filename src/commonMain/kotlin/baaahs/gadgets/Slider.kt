package baaahs.gadgets

import baaahs.Gadget
import baaahs.constrain
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.js.JsName
import kotlin.random.Random

/** A gadget for adjusting a value using a slider. */
@Serializable
data class Slider(
    /** The name for this slider. */
    val name: String,

    /** The initial value for this slider. */
    val initialValue: Float = 1f,

    /** The minimum value for this slider. */
    val minValue: Float = 0f,

    /** The maximum value for this slider. */
    val maxValue: Float = 1f,

    /** The step value for the slider. Only used if minValue and maxValue are used. */
    val stepValue: Float = 0.01f
) : Gadget() {
    /** The selected value. */
    @JsName("value")
    var value: Float by updatable("value", initialValue, Float.serializer())

    override fun adjustALittleBit() {
        val spread = maxValue - minValue
        val amount = Random.nextFloat() * spread * .25f - spread * .125f
        value = constrain(value + amount, minValue, maxValue)
    }
}