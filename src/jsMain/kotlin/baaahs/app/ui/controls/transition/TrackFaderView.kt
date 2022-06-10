package baaahs.app.ui.controls.transition

import baaahs.app.ui.appContext
import baaahs.app.ui.gadgets.slider.slider
import baaahs.ui.*
import kotlinx.js.jso
import mui.material.ToggleButton
import react.Props
import react.RBuilder
import react.RHandler
import react.dom.div
import react.useContext

private val TrackFaderView = xComponent<TrackFaderProps>("TrackFader") { props ->
    val appContext = useContext(appContext)
    val styles = appContext.allStyles.controls

    val transitionTrack = observe(props.transitionTrack)

    val handlePositionChange by handler(transitionTrack) { newPosition: Float ->
        transitionTrack.position = newPosition
    }

    div(+styles.transitionTrackFader) {
        div(+styles.transitionTrackOnAir and
                if (props.transitionTrack.onAir) styles.transitionTrackOnAirNow else null) {
            +"On Air"
        }

        slider {
            attrs.position = transitionTrack.position
            attrs.contextPosition = null
            attrs.minValue = 0f
            attrs.maxValue = 1f
            attrs.reversed = true
            attrs.showTicks = true

            attrs.onPositionChange = handlePositionChange
        }

//        div(+styles.transitionTrackName) {
//            +transitionTrack.title
//        }

        ToggleButton {
            attrs.classes = jso { this.root = -styles.transitionTrackOnScreenButton }
            attrs.selected = transitionTrack.onScreen
            attrs.onClick = props.onActivate.withTMouseEvent()

            +transitionTrack.title
        }
    }
}

external interface TrackFaderProps : Props {
    var transitionTrack: TransitionTrack
    var onActivate: (TransitionTrack) -> Unit
}

fun RBuilder.trackFader(handler: RHandler<TrackFaderProps>) =
    child(TrackFaderView, handler = handler)