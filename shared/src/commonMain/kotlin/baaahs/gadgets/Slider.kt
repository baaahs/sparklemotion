package baaahs.gadgets

import baaahs.Gadget
import baaahs.clamp
import baaahs.client.MidiEventLatch
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.builtins.nullable
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
    var floor: Float by updatable("floor", minValue, Float.serializer())
    var beatLinked: Boolean by updatable("beatLinked", false, Boolean.serializer())
    var midiStatus by updatable("midiStatus", null, MidiStatus.serializer().nullable)

    private val spread = maxValue - minValue

    val domain get() = minValue..maxValue

    @Transient
    val latch = MidiEventLatch(this)

    override fun adjustALittleBit() {
        val amount = (Random.nextFloat() - .5f) * spread * adjustmentFactor
        position = (position + amount).clamp(minValue, maxValue)
    }

    override fun adjustInRange(value: Float) {
        position = minValue + spread * value
    }
}

@Serializable
data class MidiStatus(
    val lastEventAt: Instant,
    val targetValue: Float,
)

fun ClosedFloatingPointRange<Float>.toDoubles(): ClosedFloatingPointRange<Double> {
    return start.toDouble()..endInclusive.toDouble()
}