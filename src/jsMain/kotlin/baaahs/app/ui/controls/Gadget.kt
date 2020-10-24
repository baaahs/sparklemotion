package baaahs.app.ui.controls

import baaahs.gadgets.Slider
import baaahs.jsx.RangeSlider
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGadgetControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.RBuilder
import react.RHandler
import react.RProps
import react.child
import react.dom.div

private val Gadget = xComponent<GadgetProps>("Gadget") { props ->
    val gadget = props.gadgetControl.gadget
    val title = gadget.title

    when (gadget) {
        is Slider -> {
            RangeSlider {
                attrs.gadget = gadget
            }
            div(+Styles.dataSourceTitle) { +title }
        }

        else -> {
            div(+Styles.dataSourceLonelyTitle) { +title }
        }
    }
}

external interface GadgetProps : RProps {
    var controlProps: ControlProps
    var gadgetControl: OpenGadgetControl
}

fun RBuilder.gadget(handler: RHandler<GadgetProps>) =
    child(Gadget, handler = handler)