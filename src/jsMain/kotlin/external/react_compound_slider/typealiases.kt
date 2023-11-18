package external.react_compound_slider

import baaahs.app.ui.gadgets.slider.HandleProps
import js.core.jso
import react.dom.events.*
import web.dom.Element

typealias CustomMode = (curr: Array<HandleItem>, next: Array<HandleItem>, step: Number, reversed: Boolean, getValue: (x: Double) -> Double) -> Array<HandleItem>
typealias GetEventData = (e: web.uievents.PointerEvent) -> EventData
typealias GetHandleProps = (id: String/*, props: HandlesProps*/) -> HandleProps
typealias GetRailProps = (/*OtherProps?*/) -> RailProps
typealias GetTrackProps = (/*props: OtherProps*/) -> TracksProps

typealias EmitKeyboard = (e: KeyboardEvent<Element>, id: String) -> Unit
typealias EmitMouse = (e: MouseEvent<Element, NativeMouseEvent>, id: String) -> Unit
typealias EmitTouch = (e: TouchEvent<Element>, id: String) -> Unit
typealias EmitPointer = (e: PointerEvent<Element>, location: Location, handleId: String?) -> Unit

typealias Range = ClosedFloatingPointRange<Double>

fun handleItem(key: String, value: Double) = jso<HandleItem> {
    this.key = key
    this.`val` = value
}
val HandleItem.value get() = `val`

enum class Location {
    Handle, Rail, Track, Ticks
}