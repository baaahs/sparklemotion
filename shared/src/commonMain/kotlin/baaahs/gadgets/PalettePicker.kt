package baaahs.gadgets

import baaahs.Color
import baaahs.Gadget
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer

/** A gadget for picking multiple colors. */
@Serializable
@SerialName("baaahs.Core:PalettePicker")
data class PalettePicker(
    /** The name for the palette picker. */
    override val title: String,

    val initialColors: List<Color> = emptyList()
) : Gadget() {
    var colors: List<Color> by updatable("colors", initialColors, ListSerializer(Color.serializer()))
}
