package baaahs.gl.preview

import baaahs.control.OpenButtonControl
import baaahs.control.OpenColorPickerControl
import baaahs.control.OpenSliderControl
import baaahs.util.Clock

interface GadgetAdjuster {
    enum class Mode {
        INCREMENTAL {
            override fun build(gadgets: List<ShaderBuilder.GadgetPreview>, clock: Clock) =
                IncrementalGadgetAdjuster(gadgets, clock)
        },
        FULL_RANGE {
            override fun build(gadgets: List<ShaderBuilder.GadgetPreview>, clock: Clock) =
                FullRangeGadgetAdjuster(gadgets, clock)
        };

        abstract fun build(gadgets: List<ShaderBuilder.GadgetPreview>, clock: Clock): GadgetAdjuster
    }

    fun adjustGadgets()
}

class IncrementalGadgetAdjuster(
    val gadgets: List<ShaderBuilder.GadgetPreview>,
    val clock: Clock
) : GadgetAdjuster {
    override fun adjustGadgets() {
        gadgets.forEachIndexed() { index, gadgetData ->
            val gadget = when (val control = gadgetData.openControl) {
                is OpenButtonControl -> control.switch
                is OpenColorPickerControl -> control.colorPicker
                is OpenSliderControl -> control.slider
                else -> null
            }
            gadget?.adjustALittleBit()
        }
    }
}

class FullRangeGadgetAdjuster(
    val gadgets: List<ShaderBuilder.GadgetPreview>,
    val clock: Clock
) : GadgetAdjuster {
    override fun adjustGadgets() {
        val now = clock.now() / 2
        val count = gadgets.size

        val activeGadget = now.rem(count).toInt()
        val rem = now.rem(1f).toFloat()
        val degree = (if (rem >= .5f) (1f - rem) else rem) * 2

        var mask = 0x01
        gadgets.forEachIndexed() { index, gadgetData ->
            val isHigh = (index and mask) != 0

            val myDegree = if (activeGadget == index) {
                if (isHigh) 1f - degree else degree
            } else {
                if (isHigh) 1f else 0f
            }

            val control = gadgetData.openControl
            if (control is OpenSliderControl) {
                val gadget = control.slider

                val range = gadget.maxValue - gadget.minValue
                val scaled = range * myDegree + gadget.minValue
                gadget.position = scaled

            }

            mask = mask.shl(1)
        }
    }
}