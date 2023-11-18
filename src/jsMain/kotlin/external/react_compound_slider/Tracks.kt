@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent
import react.ElementType
import react.Props
import react.ReactElement

external val Tracks : ElementType<TracksProps>

external interface TrackItem {
    var id: String
    var source: SliderItem
    var target: SliderItem
}

external interface TrackEventHandlers {
    var onMouseDown: (event: MouseEvent) -> Unit
    var onTouchStart: (event: TouchEvent) -> Unit
}

external interface TracksObject {
    var tracks: Array<TrackItem>
    var activeHandleId: String?
    var getEventData: GetEventData
    var getTrackProps: GetTrackProps
}

external interface TracksProps : Props, StandardEventHandlers, StandardEventEmitters {
    var left: Boolean?
    var right: Boolean?
    var getEventData: GetEventData
    var activeHandleId: String?
    var scale: LinearScale?
    var handles: Array<SliderItem>?
    var children: (tracksObject: TracksObject) -> ReactElement<*>
}