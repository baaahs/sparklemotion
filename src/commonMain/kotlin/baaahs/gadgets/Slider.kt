package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer
import kotlin.js.JsName

/** A gadget for adjusting a value using a slider. */
@Serializable
data class Slider(
    /** The name for this slider. */
    val name: String,

    /** The initial value for this slider. */
    val initialValue: Float = 1f,

    /** The minimum value for this slider. */
    val minValue: Float? = null,

    /** The maximum value for this slider. */
    val maxValue: Float? = null
) : Gadget() {
    /** The selected value. */
    @JsName("value")
    var value: Float by updatable("value", initialValue, Float.serializer())
}