package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.control.OpenColorPickerControl
import baaahs.show.live.ControlProps
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val ColorPickerControlView = xComponent<ColorPickerControlProps>("ColorPickerControl") { props ->
    val appContext = useContext(appContext)
    val controlsStyles = appContext.allStyles.controls

    val colorPickerControl = props.colorPickerControl

    div(+props.colorPickerControl.inUseStyle) {
        colorPicker {
            attrs.gadget = colorPickerControl.colorPicker
        }

        div(+controlsStyles.feedTitle) { +colorPickerControl.colorPicker.title }
    }
}

external interface ColorPickerControlProps : Props {
    var controlProps: ControlProps
    var colorPickerControl: OpenColorPickerControl
}

fun RBuilder.colorPickerControl(handler: RHandler<ColorPickerControlProps>) =
    child(ColorPickerControlView, handler = handler)