package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.mixin
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.top
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles
import web.html.HTMLElement
import kotlin.math.max
import kotlin.math.min

private val track = xComponent<TrackProps>("Track") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val source = props.source
    val target = props.target
    val trackRef = ref<HTMLElement>()
    val drawTrack = callback(source, target) {
        trackRef.current?.let {
            val upper = min(source.percent, target.percent)
            val lower = max(source.percent, target.percent)
            it.style.top = upper.pct.toString()
            it.style.height = (lower.toFloat() - upper.toFloat()).pct.toString()
        }
    }
    if (source is BetterSliderItem) observe(source) { drawTrack() }
    if (target is BetterSliderItem) observe(target) { drawTrack() }

    val upper = min(source.percent, target.percent)
    val lower = max(source.percent, target.percent)
    div(+styles.track) {
        ref = trackRef

        inlineStyles {
            top = upper.pct
            height = (lower.toFloat() - upper.toFloat()).pct
        }

        mixin(props.getTrackProps())
    }
}

external interface TrackProps : Props {
    var source: SliderItem
    var target: SliderItem
    var getTrackProps: GetTrackProps
}

fun RBuilder.track(handler: RHandler<TrackProps>) =
    child(track, handler = handler)