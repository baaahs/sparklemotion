package baaahs

import baaahs.gadgets.ColorPicker
import baaahs.gadgets.PalettePicker
import baaahs.gadgets.Slider

object Plugins {
    val gadgets = listOf<GadgetPlugin<*>>(
        ColorPicker.Plugin,
        PalettePicker.Plugin,
        Slider.Plugin
    ).associateBy { it.name }
}