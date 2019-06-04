package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.list
import kotlinx.serialization.serializer
import kotlin.js.JsName

/** A gadget for adjusting a value using a slider. */
@Serializable
class RadioButtons(
    /** The name for this slider. */
    val name: String,

    @JsName("options")
    val options: List<String>,

    /** The initial selection for this slider. */
    val initialValue: String
) : Gadget() {
    /** The selected value. */
    @JsName("value")
    var value: String by updatable("value", initialValue, String.serializer())
}