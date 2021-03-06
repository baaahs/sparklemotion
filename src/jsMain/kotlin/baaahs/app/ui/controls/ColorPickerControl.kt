package baaahs.app.ui.controls

import baaahs.control.OpenColorPickerControl
import baaahs.jsx.ColorPicker
import baaahs.jsx.ColorPickerProps
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinext.js.jsObject
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div

val ColorPickerControl = xComponent<ColorPickerControlProps>("ColorPickerControl") { props ->
    val colorPickerControl = props.colorPickerControl

    child(ColorPicker, jsObject<ColorPickerProps> {
        gadget = colorPickerControl.colorPicker
    }) {}

    div(+Styles.dataSourceTitle) { +colorPickerControl.colorPicker.title }
}

external interface ColorPickerControlProps : RProps {
    var controlProps: ControlProps
    var colorPickerControl: OpenColorPickerControl
}

fun RBuilder.colorPickerControl(handler: RHandler<ColorPickerControlProps>) =
    child(ColorPickerControl, handler = handler)