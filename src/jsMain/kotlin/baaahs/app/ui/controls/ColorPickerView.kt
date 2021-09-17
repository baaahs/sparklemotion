package baaahs.app.ui.controls

import baaahs.Color
import baaahs.Gadget
import baaahs.gadgets.ColorPicker
import baaahs.jsx.ColorWheel
import baaahs.ui.xComponent
import react.RBuilder
import react.RHandler
import react.RProps
import react.child

private val ColorPickerView = xComponent<ColorPickerProps>("ColorPicker") { props ->
    var colors by state { arrayOf(props.gadget.color) }

    val handleChangeFromUi by handler { newColors: Array<Color> ->
        colors = newColors
        props.gadget.color = newColors[0]
    }

    val handleChangeFromServer by handler { gadget: Gadget ->
        colors = arrayOf(props.gadget.color)
    }


    onMount {
        props.gadget.listen(handleChangeFromServer)

        withCleanup {
            props.gadget.unlisten(handleChangeFromServer)
        }
    }

    ColorWheel {
        attrs.colors = colors
        attrs.onChange = handleChangeFromUi
    }
}

external interface ColorPickerProps : RProps {
    var gadget: ColorPicker
}

fun RBuilder.colorPicker(handler: RHandler<ColorPickerProps>) =
    child(ColorPickerView, handler = handler)