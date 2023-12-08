package baaahs.ui.slider

import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent
import web.dom.Element

typealias EmitKeyboard = (e: KeyboardEvent<Element>, id: String) -> Unit
typealias EmitPointer = (e: PointerEvent<Element>, location: Location, handleId: String?) -> Unit

typealias Range = ClosedFloatingPointRange<Double>

enum class Location {
    Handle, Rail, Track, Tick
}