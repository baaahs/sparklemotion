package baaahs.gadgets

import baaahs.Color
import baaahs.Gadget
import baaahs.array
import kotlinx.serialization.Serializable
import kotlinx.serialization.internal.ReferenceArraySerializer
import kotlinx.serialization.list

/** A gadget for picking multiple colors. */
@Serializable
data class PalettePicker(
    /** The name for the palette picker. */
    val name: String,

    val initialColors: Array<Color> = emptyArray()
) : Gadget() {
    var colors: Array<Color> by updatable("colors", initialColors, Color.serializer().array(Color::class))
}
