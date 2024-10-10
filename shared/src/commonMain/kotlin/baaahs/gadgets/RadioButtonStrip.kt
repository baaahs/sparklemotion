package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.js.JsName
import kotlin.random.Random

@Serializable
@SerialName("baaahs.Core:RadioButtonStrip")
data class RadioButtonStrip(
    override val title: String,
    val buttonTitles: List<String>,
    val initialSelectionIndex: Int
) : Gadget() {
    @JsName("selectionIndex")
    var selectionIndex: Int by updatable("selectionIndex", initialSelectionIndex, Int.serializer())

    override fun adjustALittleBit() {
        selectionIndex = Random.nextInt(buttonTitles.size)
    }

    override fun adjustInRange(value: Float) {
        selectionIndex = (buttonTitles.size * value).toInt()
    }
}
