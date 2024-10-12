package baaahs.app.ui.controls

import baaahs.Color
import baaahs.Gadget
import baaahs.app.ui.gadgets.color.ColorWheelView
import baaahs.gadgets.ColorPicker
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler

private val ColorPickerView = xComponent<ColorPickerProps>("ColorPicker") { props ->
    var colors by state { arrayOf(props.gadget.color) }

    val handleChangeFromUi by handler { newColors: Array<Color> ->
        colors = newColors
        props.gadget.color = newColors[0]
    }

    val handleChangeFromServer by handler { _: Gadget ->
        colors = arrayOf(props.gadget.color)
    }


    onMount {
        props.gadget.listen(handleChangeFromServer)

        withCleanup {
            props.gadget.unlisten(handleChangeFromServer)
        }
    }

    ColorWheelView {
        attrs.colors = colors
        attrs.onChange = handleChangeFromUi
    }
}

external interface ColorPickerProps : Props {
    var gadget: ColorPicker
}

fun RBuilder.colorPicker(handler: RHandler<ColorPickerProps>) =
    child(ColorPickerView, handler = handler)