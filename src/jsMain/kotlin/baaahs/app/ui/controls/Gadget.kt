package baaahs.app.ui.controls

import baaahs.app.ui.appContext
import baaahs.jsx.RangeSlider
import baaahs.plugin.CorePlugin
import baaahs.show.live.ControlProps
import baaahs.show.live.ControlView
import baaahs.show.live.OpenControl
import baaahs.show.live.OpenGadgetControl
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import react.*
import react.dom.div

class GadgetControlView(val openControl: OpenGadgetControl) : ControlView {
    override fun <P : ControlProps<in OpenControl>> getReactElement(): FunctionalComponent<P> {
        return Gadget.unsafeCast<FunctionalComponent<P>>()
    }
}

val Gadget = xComponent<GadgetProps>("Gadget") { props ->
    val appContext = useContext(appContext)
    val dataSource = props.control.controlledDataSource
    val dataFeed = appContext.showPlayer.useDataFeed(dataSource)
    val title = props.control.gadget.title

    when (dataSource.getRenderType()) {
        "Slider" -> {
            RangeSlider {
                attrs.gadget = (dataFeed as CorePlugin.GadgetDataFeed).gadget
            }
            div(+Styles.dataSourceTitle) { +title }
        }

        else -> {
            div(+Styles.dataSourceLonelyTitle) { +title }
        }
    }
}

external interface GadgetProps : ControlProps<OpenGadgetControl>

fun RBuilder.gadget(handler: RHandler<GadgetProps>) =
    child(Gadget, handler = handler)