package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.gadgets.Slider
import baaahs.jsx.RangeSlider
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGadgetControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.*
import react.dom.div

private val Gadget = xComponent<GadgetProps>("Gadget") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.appUiControls

    val gadget = props.gadgetControl.gadget
    val title = gadget.title

    when (gadget) {
        is Slider -> {
            RangeSlider {
                attrs.gadget = gadget
            }
            div(+styles.dataSourceTitle) { +title }
        }

        else -> {
            div(+styles.dataSourceLonelyTitle) { +title }
        }
    }
}

external interface GadgetProps : RProps {
    var controlProps: ControlProps
    var gadgetControl: OpenGadgetControl
}

fun RBuilder.gadget(handler: RHandler<GadgetProps>) =
    child(Gadget, handler = handler)