@file:JsModule("js/app/components/gadgets/Slider/index.jsx")

package baaahs.jsx

import baaahs.Gadget
import react.RClass
import react.RProps

@JsName("default")
external val RangeSlider: RClass<RangeSliderProps>

external interface RangeSliderProps: RProps {
    var gadget: Gadget
    var reversed: Boolean
    var showTicks: Boolean
}

