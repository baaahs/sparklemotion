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
import react.*
import react.dom.div
import styled.inlineStyles

private val track = xComponent<TrackProps>("Track") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.gadgetsSlider

    div(+styles.track) {
        inlineStyles {
            top = props.source.percent.pct
            height = (props.target.percent.toFloat() - props.source.percent.toFloat()).pct
        }

        mixin(props.getTrackProps())
    }
}

external interface TrackProps : RProps {
    var source: SliderItem
    var target: SliderItem
    var getTrackProps: GetTrackProps
}

fun RBuilder.track(handler: RHandler<TrackProps>) =
    child(track, handler = handler)