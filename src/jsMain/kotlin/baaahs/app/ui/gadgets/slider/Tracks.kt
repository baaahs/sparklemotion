package baaahs.app.ui.gadgets.slider

import baaahs.ui.xComponent
import js.core.jso
import react.*

external interface TrackItem {
    var id: String
    var source: SliderItem
    var target: SliderItem
}

external interface TracksObject {
    var tracks: Array<TrackItem>
    var activeHandleId: String?
    var getEventData: GetEventData
    var getTrackProps: GetTrackProps
}

val BetterTracks = xComponent<TracksProps>("BetterTracks") { props ->
    val domain = props.scale?.domain ?: 0.0..1.0
    val handles = props.handles ?: emptyArray()
    val left = props.left != false
    val right = props.right != false

    val tracks = memo(left, right, domain, handles) {
        buildList<TrackItem> {
            for (i in 0 until handles.size + 1) {
                val source = when {
                    i == 0 && left -> jso { id = "$"; value = domain.start; percent = 0.0 }
                    i == 0 -> null
                    else -> handles[i - 1]
                }

                val target = when {
                    i == handles.size && right -> jso { id = "$"; value = domain.endInclusive; percent = 100.0 }
                    i == handles.size -> null
                    else -> handles[i]
                }

                if (source != null && target != null) {
                    add(jso {
                        this.id = "${source.id}-${target.id}"
                        this.source = source
                        this.target = target
                    })
                }
            }
        }.toTypedArray()
    }

    val getTrackProps = callback(props.onPointerDown, props.emitPointer) {
        jso<TracksProps> {
            onPointerDown = { e ->
                props.onPointerDown?.invoke(e)
                props.emitPointer?.invoke(e, Location.Track, null)
            }
        }
    }

    // render():
    val tracksObject = jso<TracksObject> {
        this.tracks = tracks
        this.activeHandleId = props.activeHandleId
        this.getEventData = props.getEventData
        this.getTrackProps = getTrackProps
    }

    +Children.only(props.children.invoke(tracksObject))
}

fun RBuilder.betterTracks(handler: RHandler<TracksProps>) =
    child(BetterTracks, handler = handler)

external interface TracksProps : Props, StandardEventHandlers, StandardEventEmitters {
    var left: Boolean?
    var right: Boolean?
    var getEventData: GetEventData
    var activeHandleId: String?
    var scale: LinearScale?
    var handles: Array<SliderItem>?
    var children: (tracksObject: TracksObject) -> ReactElement<*>
}