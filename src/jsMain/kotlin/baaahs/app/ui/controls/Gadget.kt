package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.jsx.RangeSlider
import baaahs.plugin.CorePlugin
import baaahs.show.live.ControlProps
import baaahs.show.live.OpenGadgetControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.*
import react.dom.div

private val Gadget = xComponent<GadgetProps>("Gadget") { props ->
    val appContext = useContext(appContext)
    val dataSource = props.gadgetControl.controlledDataSource
    val dataFeed = appContext.showPlayer.useDataFeed(dataSource)
    val gadget = when (dataFeed) {
        is CorePlugin.GadgetDataFeed -> dataFeed.gadget
        else -> dataSource.buildControl()!!.gadget
    }
    val title = props.gadgetControl.gadget.title

    when (dataSource.getRenderType()) {
        "Slider" -> {
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