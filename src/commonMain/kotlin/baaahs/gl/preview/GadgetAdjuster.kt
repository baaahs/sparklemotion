package baaahs.gl.preview

import baaahs.Clock
import baaahs.GadgetData
import baaahs.gadgets.Slider

class GadgetAdjuster(val gadgets: List<GadgetData>, val clock: Clock) {
    fun adjustGadgets() {
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

            val gadget = gadgetData.gadget
            if (gadget is Slider) {
                val range = gadget.maxValue - gadget.minValue
                val scaled = range * myDegree + gadget.minValue
                gadget.value = scaled

            }

            mask = mask.shl(1)
        }
    }
}