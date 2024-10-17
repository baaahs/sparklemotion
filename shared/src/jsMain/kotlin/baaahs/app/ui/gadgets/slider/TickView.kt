package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.slider.Location
import baaahs.ui.slider.sliderContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.bottom
import kotlinx.css.pct
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.dom.events.PointerEvent
import react.dom.onPointerDown
import react.useContext
import styled.inlineStyles

val tick = xComponent<TickProps>("Tick") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider
    val sliderContext = useContext(sliderContext)

    val percent = 100 - props.percent
    val formatter = props.formatter ?: { value: Double -> value.toString() }

    val handlePointerDown = callback(sliderContext.emitPointer) { e: PointerEvent<*> ->
        sliderContext.emitPointer(e, Location.Tick, null)
    }

    div(+if (props.isDefaultValue) styles.defaultTickMark else styles.tickMark) {
        attrs.onPointerDown = handlePointerDown
        inlineStyles {
            bottom = percent.pct
        }
    }

    if (!props.isDefaultValue) {
        div(+styles.tickText) {
            attrs.onPointerDown = handlePointerDown
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
    var isDefaultValue: Boolean
}

fun RBuilder.tick(handler: RHandler<TickProps>) =
    child(tick, handler = handler)