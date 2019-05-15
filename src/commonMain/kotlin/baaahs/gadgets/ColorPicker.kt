package baaahs.gadgets

import baaahs.Color
import baaahs.Gadget
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import kotlin.js.JsName

/** A gadget for picking a single color for a color palette. */
@Serializable
class ColorPicker(
    /** The name for this color picker. */
    val name: String,

    /** The initial value for this color picker. */
    val initialValue: Color = Color.WHITE
) : Gadget() {
    /** The selected color. */
    @JsName("color")
    var color: Color by watchForChanges(initialValue)

    override fun toJson(): JsonElement =
        JsonObject(
            mapOf(
                "color" to JsonPrimitive(color.toInt())
            )
        )

    override fun setFromJson(jsonElement: JsonElement) {
        val jsonObject = jsonElement.jsonObject
        color = Color.from(jsonObject["color"]!!.primitive.int)
    }
}