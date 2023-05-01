package baaahs.gadgets

import baaahs.Gadget
import baaahs.clamp
import baaahs.util.Time
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.jvm.JvmInline
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
    var floor: Float by updatable("floor", minValue, Float.serializer())
    var beatLinked: Boolean by updatable("beatLinked", false, Boolean.serializer())
    var sliding: Sliding? = null
    private val spread = maxValue - minValue

    override fun adjustALittleBit() {
        val amount = (Random.nextFloat() - .5f) * spread * adjustmentFactor
        position = (position + amount).clamp(minValue, maxValue)
    }

    override fun adjustInRange(value: Float) {
        position = minValue + spread * value
    }
}

@Serializable
data class Sliding(
    val fromPosition: Float,
    val toPosition: Float,
    val startTime: NetworkTime,
    val endTime: NetworkTime
) {
    private fun progress(now: Time): Double {
        val elapsed = now - startTime.time
        val duration = endTime.time - startTime.time
        return (elapsed / duration).clamp(0.0, 1.0)
    }

    private fun easeInOut(progress: Double): Double {
        return progress * progress * (3 - 2 * progress)
    }

    fun getCurrentPosition(now: Time): Float {
        return (
                easeInOut(progress(now))
                        * (toPosition - fromPosition)
                        + fromPosition
                ).toFloat()
    }

    fun hasFinished(now: Time): Boolean =
        now >= endTime.time
}

@Serializable @JvmInline
value class NetworkTime(private val networkTime: Time) {
    val time get() = networkTime
}