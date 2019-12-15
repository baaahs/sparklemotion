package baaahs.gadgets

import baaahs.Color
import baaahs.Gadget
import baaahs.GadgetPlugin
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlin.js.JsName
import kotlin.random.Random
import kotlin.reflect.KClass

/** A gadget for picking a single color for a color palette. */
@Serializable
data class ColorPicker(
    /** The name for this color picker. */
    val name: String,

    /** The initial value for this color picker. */
    val initialValue: Color = Color.WHITE
) : Gadget() {

    /** The selected color. */
    @JsName("color")
    var color: Color by updatable("color", initialValue, Color.serializer())

    override fun adjustALittleBit() {
        fun randomAmount() = Random.nextFloat() * .1f - .05f
        color = Color(color.redF + randomAmount(), color.greenF + randomAmount(), color.blueF + randomAmount())
    }

    object Plugin : GadgetPlugin<ColorPicker> {
        override val name = "ColorPicker"
        override val gadgetClass = ColorPicker::class
        override val serializer = serializer()
        override fun create(name: String, config: JsonObject) = ColorPicker(name)
        override fun getValue(gadget: ColorPicker) = gadget.color
    }
}