package baaahs.gadgets

import baaahs.Gadget
import baaahs.geom.Vector2F
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.random.Random

/** A gadget for adjusting a scalar numeric value using an XY pad. */
@Serializable
@SerialName("baaahs.Core:XyPad")
data class XyPad(
    /** The name for this xyPad. */
    override val title: String,

    /** The initial value for this XY pad. */
    val initialValue: Vector2F = Vector2F.origin,

    /** The minimum value for this XY pad. */
    val minValue: Vector2F = Vector2F.origin - Vector2F.unit2d,

    /** The maximum value for this XY pad. */
    val maxValue: Vector2F = Vector2F.unit2d
) : Gadget() {
    /** The selected value. */
    var position: Vector2F by updatable("position", initialValue, Vector2F.serializer())

    var clampedPosition: Vector2F
        get() = position.clamp(minValue, maxValue)
        set(value) { position = value.clamp(minValue, maxValue) }

    override fun adjustALittleBit() {
        val spread = maxValue - minValue
        val adjustment = Vector2F(
            (Random.nextFloat() - .5f) * spread.x,
            (Random.nextFloat() - .5f) * spread.y
        ) * adjustmentFactor

        position = (position + adjustment)
            .clamp(minValue, maxValue)
    }
}