package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlin.js.JsName
import kotlin.random.Random

@Serializable
data class RadioButtonStrip(
    val name: String,
    val buttonTitles: List<String>,
    val initialSelectionIndex: Int
) : Gadget() {
    @JsName("selectionIndex")
    var selectionIndex: Int by updatable("selectionIndex", initialSelectionIndex, Int.serializer())

    override fun adjustALittleBit() {
        selectionIndex = Random.nextInt(buttonTitles.size)
    }
}
