package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.top
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement
import kotlin.math.max
import kotlin.math.min

val track = xComponent<TrackProps>("Track") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val fromPercent = props.fromHandle?.let { props.scale.getValue(it.value) } ?: 0.0
    val toPercent = props.toHandle?.let { props.scale.getValue(it.value) } ?: 100.0
    val trackRef = ref<HTMLElement>()
    val adjustTrack = callback(trackRef, fromPercent, toPercent) {
        trackRef.current?.let {
            val upper = min(fromPercent, toPercent)
            val lower = max(fromPercent, toPercent)
            it.style.top = upper.pct.toString()
            it.style.height = (lower.toFloat() - upper.toFloat()).pct.toString()
        }
    }
    props.fromHandle?.let { observe(it) { adjustTrack() } }
    props.toHandle?.let { observe(it) { adjustTrack() } }

    val upper = min(fromPercent, toPercent)
    val lower = max(fromPercent, toPercent)
    div(+styles.track) {
        ref = trackRef

        inlineStyles {
            top = upper.pct
            height = (lower.toFloat() - upper.toFloat()).pct
        }
    }
}

external interface TrackProps : baaahs.ui.slider.TrackProps

fun RBuilder.track(handler: RHandler<TrackProps>) =
    child(track, handler = handler)