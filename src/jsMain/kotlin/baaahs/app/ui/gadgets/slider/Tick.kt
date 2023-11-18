package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.slider.SliderItem
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.bottom
import kotlinx.css.pct
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles

private val tick = xComponent<TickProps>("Tick") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div {
        div(+styles.tickMark) {
            inlineStyles {
                bottom = (100 - props.tick.percent.toFloat()).pct
            }
        }

        div(+styles.tickText) {
            inlineStyles {
                bottom = (100 - props.tick.percent.toFloat()).pct
            }

            +props.format(props.tick)
        }
    }
}

external interface TickProps: Props {
    var tick: SliderItem
    var format: (SliderItem) -> String
}

fun RBuilder.tick(handler: RHandler<TickProps>) =
    child(tick, handler = handler)