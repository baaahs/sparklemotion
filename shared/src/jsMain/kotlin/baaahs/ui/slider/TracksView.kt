package baaahs.ui.slider

import baaahs.ui.xComponent
import react.*

private val Tracks = xComponent<TracksProps>("BetterTracks") { props ->
    val sliderContext = useContext(sliderContext)
    val handles = sliderContext.handles

    // render():
    for (i in 0 until handles.size + 1) {
        val fromHandle = when {
            i == 0 -> null
            else -> handles[i - 1]
        }

        val toHandle = when {
            i == handles.size -> null
            else -> handles[i]
        }

        props.renderTrack?.let { renderTrack ->
            +cloneElement(renderTrack) {
                this.key = "${fromHandle?.id ?: "_start_"}-${toHandle?.id ?: "_end_"}"
                this.fromHandle = fromHandle
                this.toHandle = toHandle
            }
        }
    }
}

external interface TracksProps : Props {
    var renderTrack : ReactElement<out TrackProps>?
}

external interface TrackProps : Props {
    var fromHandle: Handle?
    var toHandle: Handle?
}

fun RBuilder.tracks(handler: RHandler<TracksProps>) =
    child(Tracks, handler = handler)
