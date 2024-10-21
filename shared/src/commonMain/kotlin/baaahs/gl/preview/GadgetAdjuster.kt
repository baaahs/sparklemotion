package baaahs.gl.preview

import baaahs.util.Clock
import baaahs.util.asDoubleSeconds

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
        gadgets.forEachIndexed() { _, gadgetData ->
            gadgetData.openControl.gadget?.adjustALittleBit()
        }
    }
}

class FullRangeGadgetAdjuster(
    val gadgets: List<ShaderBuilder.GadgetPreview>,
    val clock: Clock
) : GadgetAdjuster {
    override fun adjustGadgets() {
        val now = clock.now().asDoubleSeconds / 2
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
            control.gadget?.adjustInRange(myDegree)

            mask = mask.shl(1)
        }
    }
}