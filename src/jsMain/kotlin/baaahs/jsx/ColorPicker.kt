@file:JsModule("js/app/components/gadgets/ColorPicker/index.jsx")
package baaahs.jsx

import baaahs.Gadget
import react.RClass
import react.RProps

@JsName("default")
external val ColorPicker: RClass<ColorPickerProps>

external interface ColorPickerProps: RProps {
    var gadget: Gadget
}

