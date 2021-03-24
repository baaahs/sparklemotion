@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent
import react.RClass
import react.RProps

external val Tracks : RClass<TracksProps>

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
    var activeHandleID: String
    var getEventData: GetEventData
    var getTrackProps: GetTrackProps
}

external interface TracksProps : RProps {
    var left: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var right: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var getEventData: GetEventData?
        get() = definedExternally
        set(value) = definedExternally
    var activeHandleID: String?
        get() = definedExternally
        set(value) = definedExternally
    var scale: LinearScale?
        get() = definedExternally
        set(value) = definedExternally
    var handles: Array<SliderItem>?
        get() = definedExternally
        set(value) = definedExternally
//    var emitMouse: EmitMouse?
//        get() = definedExternally
//        set(value) = definedExternally
//    var emitTouch: EmitTouch?
//        get() = definedExternally
//        set(value) = definedExternally
    var children: (tracksObject: TracksObject) -> dynamic
}