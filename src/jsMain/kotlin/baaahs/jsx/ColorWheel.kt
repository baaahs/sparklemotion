@file:JsModule("js/app/components/ColorWheel/index.jsx")
package baaahs.jsx

import baaahs.Color
import react.RClass
import react.RProps

@JsName("default")
external val ColorWheel: RClass<ColorWheelProps>

external interface ColorWheelProps: RProps {
    var colors: Array<Color>
    var onChange: (Array<Color>) -> Unit
    var isPalette: Boolean?
}
