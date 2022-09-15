package baaahs.gadgets

import baaahs.Gadget
import baaahs.geom.Vector2F
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
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

    private val spread = maxValue - minValue

    override fun adjustALittleBit() {
        val adjustment = Vector2F(
            (Random.nextFloat() - .5f) * spread.x,
            (Random.nextFloat() - .5f) * spread.y
        ) * adjustmentFactor

        position = (position + adjustment)
            .clamp(minValue, maxValue)
    }

    override fun adjustInRange(value: Float) {
        val v = (value * 2 * PI).toFloat()
        position = minValue +
                Vector2F(
                    sin(v) * .5f + .5f,
                    cos(v) * .5f + .5f
                ) * spread
    }

    fun getHelper(padSize: Vector2F, knobSize: Vector2F) = Helper(this, padSize, knobSize)

    class Helper(
        private val xyPad: XyPad,
        private val padSize: Vector2F,
        private val knobSize: Vector2F
    ) {
        private val padUsableSize = padSize - knobSize
        private val range = xyPad.maxValue - xyPad.minValue

        val knobPositionPx: Vector2F
            get() = run {
                ((xyPad.clampedPosition - xyPad.minValue) / range * padUsableSize)
                    .flipY()
            }

        val crosshairPositionPx: Vector2F
            get() = knobPositionPx + knobSize / 2f

        fun positionFromPx(px: Vector2F): Vector2F {
            val halfKnob = knobSize / 2f
            val withinUsable = px.clamp(halfKnob, padSize - halfKnob) - halfKnob
            return withinUsable.flipY() / padUsableSize * range + xyPad.minValue
        }

        private fun Vector2F.flipY() = Vector2F(x, padUsableSize.y - y)
    }
}