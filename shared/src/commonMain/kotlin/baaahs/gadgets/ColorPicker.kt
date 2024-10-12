package baaahs.gadgets

import baaahs.Color
import baaahs.Gadget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.js.JsName
import kotlin.math.PI
import kotlin.math.sin
import kotlin.random.Random

/** A gadget for picking a single color for a color palette. */
@Serializable
@SerialName("baaahs.Core:ColorPicker")
data class ColorPicker(
    /** The name for this color picker. */
    override val title: String,

    /** The initial value for this color picker. */
    val initialValue: Color = Color.WHITE
) : Gadget() {

    /** The selected color. */
    @JsName("color")
    var color: Color by updatable("color", initialValue, Color.serializer())

    override fun adjustALittleBit() {
        fun randomAmount() = Random.nextFloat() * adjustmentFactor - .05f
        color = Color(
            color.redF + randomAmount(),
            color.greenF + randomAmount(),
            color.blueF + randomAmount()
        )
    }

    override fun adjustInRange(value: Float) {
        val v = (value * 2 * PI).toFloat()
        color = Color(
            sin(v) * .5f + .5f,
            sin(v + PI / 2).toFloat() * .5f + .5f,
            sin(v + PI).toFloat() * .5f + .5f
        )
    }
}