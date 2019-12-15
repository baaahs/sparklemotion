package baaahs.gadgets

import baaahs.Color
import baaahs.Gadget
import baaahs.GadgetPlugin
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.list
import kotlinx.serialization.json.JsonObject

/** A gadget for picking multiple colors. */
@Serializable
data class PalettePicker(
    /** The name for the palette picker. */
    val name: String,

    val initialColors: List<Color> = emptyList()
) : Gadget() {
    var colors: List<Color> by updatable("colors", initialColors, Color.serializer().list)

    object Plugin : GadgetPlugin<PalettePicker> {
        override val name = "PalettePicker"
        override val gadgetClass = PalettePicker::class
        override val serializer = serializer()
        override fun create(name: String, config: JsonObject) = PalettePicker(name)
        override fun getValue(gadget: PalettePicker) = gadget.colors
    }
}
