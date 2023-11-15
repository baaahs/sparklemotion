package baaahs.app.ui.gadgets.slider

import baaahs.app.ui.appContext
import baaahs.ui.mixin
import baaahs.ui.unaryPlus
import baaahs.ui.xComponent
import external.react_compound_slider.GetTrackProps
import external.react_compound_slider.SliderItem
import kotlinx.css.height
import kotlinx.css.pct
import kotlinx.css.top
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext
import styled.inlineStyles
import kotlin.math.max
import kotlin.math.min

private val track = xComponent<TrackProps>("Track") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    val upper = min(props.source.percent, props.target.percent)
    val lower = max(props.source.percent, props.target.percent)
    div(+styles.track) {
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