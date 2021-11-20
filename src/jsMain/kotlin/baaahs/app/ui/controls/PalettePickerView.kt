package baaahs.app.ui.controls

import baaahs.Color
import baaahs.Gadget
import baaahs.app.ui.gadgets.color.ColorWheelView
import baaahs.gadgets.PalettePicker
import baaahs.ui.xComponent
import react.Props
import react.RBuilder
import react.RHandler

private val PalettePickerView = xComponent<PalettePickerProps>("PalettePicker") { props ->
    var colors by state { props.gadget.colors }

    val handleChangeFromUi by handler { newColors: Array<Color> ->
        colors = newColors.toList()
        props.gadget.colors = newColors.toList()
    }

    val handleChangeFromServer by handler { _: Gadget ->
        colors = props.gadget.colors
    }


    onMount {
        props.gadget.listen(handleChangeFromServer)

        withCleanup {
            props.gadget.unlisten(handleChangeFromServer)
        }
    }

    ColorWheelView {
        attrs.colors = colors.toTypedArray()
        attrs.isPalette = true
        attrs.onChange = handleChangeFromUi
    }
}

external interface PalettePickerProps : Props {
    var gadget: PalettePicker
}

fun RBuilder.palettePicker(handler: RHandler<PalettePickerProps>) =
    child(PalettePickerView, handler = handler)