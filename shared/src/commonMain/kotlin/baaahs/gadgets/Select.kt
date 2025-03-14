package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.js.JsName
import kotlin.random.Random

@Serializable
@SerialName("baaahs.Core:Select")
data class Select(
    override val title: String,
    val options: List<Pair<Int, String>>,
    val initialSelectionIndex: Int
) : Gadget() {
    @JsName("selectionIndex")
    var selectionIndex: Int by updatable("selectionIndex", initialSelectionIndex, Int.serializer())

    override fun adjustALittleBit() {
        selectionIndex = Random.nextInt(options.size)
    }

    override fun adjustInRange(value: Float) {
        selectionIndex = (options.size * value).toInt()
    }
}
