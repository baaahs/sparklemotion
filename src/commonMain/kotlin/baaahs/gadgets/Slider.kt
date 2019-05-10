package baaahs.gadgets

import baaahs.Gadget
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.js.JsName

/** A gadget for adjusting a value using a slider. */
@Serializable
class Slider(
    /** The name for this slider. */
    val name: String,

    /** The inial value for this slider. */
    val initialValue: Float = 1f
) : Gadget() {
    /** The selected value. */
    @JsName("value")
    var value: Float by watchForChanges(initialValue)

    override fun toJson() = JsonObject(
        mapOf(
            "value" to JsonPrimitive(value)
        )
    )

    override fun setFromJson(jsonElement: JsonElement) {
        val jsonObject = jsonElement.jsonObject
        value = jsonObject["value"]!!.primitive.float
    }
}