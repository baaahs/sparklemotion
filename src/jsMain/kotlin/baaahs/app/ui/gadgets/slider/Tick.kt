package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
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

val tick = xComponent<TickProps>("Tick") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val percent = if (props.reversed) props.percent else 100 - props.percent
    val formatter = props.formatter ?: { value: Double -> value.toString() }

    div {
        div(+styles.tickMark) {
            inlineStyles {
                bottom = percent.pct
            }
        }

        div(+styles.tickText) {
            inlineStyles {
                bottom = percent.pct
            }

            +formatter(props.value)
        }
    }
}

external interface TickProps: Props {
    var value: Double
    var percent: Double
    var formatter: ((Double) -> String)?
    var reversed: Boolean
}

fun RBuilder.tick(handler: RHandler<TickProps>) =
    child(tick, handler = handler)