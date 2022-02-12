package baaahs.app.ui.controls

import baaahs.control.OpenColorPickerControl
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div

private val ColorPickerControlView = xComponent<ColorPickerControlProps>("ColorPickerControl") { props ->
    val colorPickerControl = props.colorPickerControl

    div(+props.colorPickerControl.inUseStyle) {
        colorPicker {
            attrs.gadget = colorPickerControl.colorPicker
        }

        div(+Styles.dataSourceTitle) { +colorPickerControl.colorPicker.title }
    }
}

external interface ColorPickerControlProps : Props {
    var controlProps: ControlProps
    var colorPickerControl: OpenColorPickerControl
}

fun RBuilder.colorPickerControl(handler: RHandler<ColorPickerControlProps>) =
    child(ColorPickerControlView, handler = handler)