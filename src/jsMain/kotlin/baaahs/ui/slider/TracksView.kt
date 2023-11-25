package baaahs.ui.slider

import baaahs.app.ui.gadgets.slider.TrackProps
import baaahs.ui.xComponent
import js.core.jso
import react.*

private val Tracks = xComponent<TracksProps>("BetterTracks") { props ->
    val handles = props.handles

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
            +renderTrack.invoke(jso {
                this.id = "${fromHandle?.id ?: "_start_"}-${toHandle?.id ?: "_end_"}"
                this.fromHandle = fromHandle
                this.toHandle = toHandle
                this.scale = props.scale
            })
        }

        props.renderTrack2?.let { renderTrack2 ->
            +cloneElement(renderTrack2) {
                this.id = "${fromHandle?.id ?: "_start_"}-${toHandle?.id ?: "_end_"}"
                this.fromHandle = fromHandle
                this.toHandle = toHandle
                this.scale = props.scale
            }
        }
    }
}

fun RBuilder.tracks(handler: RHandler<TracksProps>) =
    child(Tracks, handler = handler)

external interface TracksProps : Props {
    var handles: List<ExtHandle>
    var domain: Range
    var scale: LinearScale
    var renderTrack: ((trackProps: TrackProps) -> ReactNode)?
    var renderTrack2 : ReactElement<TrackProps>?
    var emitPointer: EmitPointer?
//    var left: Boolean?
//    var right: Boolean?
//    var getEventData: GetEventData
//    var activeHandleId: String?
//    var scale: LinearScale?
//    var children: (tracksObject: TracksObject) -> ReactElement<*>
}