package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.Observable
import baaahs.ui.slider.Handle
import baaahs.ui.slider.LinearScale
import baaahs.ui.slider.sliderContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.top
import react.dom.div
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement
import kotlin.math.max
import kotlin.math.min

fun LinearScale.minMax(fromHandle: Handle?, toHandle: Handle?): Pair<Double, Double> {
    val fromPercent = fromHandle?.let { getValue(it.value) } ?: 0.0
    val toPercent = toHandle?.let { getValue(it.value) } ?: 100.0
    val lower = max(fromPercent, toPercent)
    val upper = min(fromPercent, toPercent)
    return lower to upper
}

val track = xComponent<TrackProps>("Track") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider
    val sliderContext = useContext(sliderContext)

    val trackRef = ref<HTMLElement>()
    val adjustTrack = callback(trackRef, sliderContext.scale, props.fromHandle, props.toHandle) {
        val (lower, upper) = sliderContext.scale.minMax(props.fromHandle, props.toHandle)
        trackRef.current?.let {
            it.style.top = upper.pct.toString()
            it.style.height = (lower.toFloat() - upper.toFloat()).pct.toString()
        }
    }
    observe(props.fromHandle ?: Observable()) { adjustTrack() }
    observe(props.toHandle ?: Observable()) { adjustTrack() }

    if (props.fromHandle != null && (props.toHandle != null || props.fillToZero == true)) {
        val (lower, upper) = sliderContext.scale.minMax(props.fromHandle, props.toHandle)

        div(+styles.track) {
            ref = trackRef

            inlineStyles {
                top = upper.pct
                height = (lower.toFloat() - upper.toFloat()).pct
            }
        }
    }
}

external interface TrackProps : baaahs.ui.slider.TrackProps {
    var fillToZero: Boolean?
}