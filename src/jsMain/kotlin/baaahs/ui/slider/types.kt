package baaahs.ui.slider

import react.dom.events.KeyboardEvent
import react.dom.events.PointerEvent

external interface StandardEventHandlers {
    var onKeyDown: ((event: KeyboardEvent<*>) -> Unit)?
    var onPointerDown: ((event: PointerEvent<*>) -> Unit)?
}

external interface StandardEventEmitters {
    var emitKeyboard: EmitKeyboard?
    var emitPointer: EmitPointer?
}

external interface EventData {
    var value: Double
    var percent: Double
}

