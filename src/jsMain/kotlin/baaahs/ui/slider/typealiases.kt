package baaahs.ui.slider

import baaahs.app.ui.gadgets.slider.HandleProps
import js.core.jso
import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import web.dom.Element

typealias GetEventData = (e: web.uievents.PointerEvent) -> EventData
typealias GetHandleProps = (id: String/*, props: HandlesProps*/) -> HandleProps
typealias GetRailProps = (/*OtherProps?*/) -> RailProps
typealias GetTrackProps = (/*props: OtherProps*/) -> TracksProps

typealias EmitKeyboard = (e: KeyboardEvent<Element>, id: String) -> Unit
typealias EmitPointer = (e: PointerEvent<Element>, location: Location, handleId: String?) -> Unit

typealias Range = ClosedFloatingPointRange<Double>

fun handleItem(key: String, value: Double) = jso<HandleItem> {
    this.key = key
    this.value = value
}

enum class Location {
    Handle, Rail, Track, Ticks
}