package baaahs.ui.slider

import react.dom.events.KeyboardEventHandler
import react.dom.events.PointerEventHandler

external interface StandardEventHandlers {
    var onKeyDown: KeyboardEventHandler<*>?
    var onPointerDown: PointerEventHandler<*>?
}

external interface EventData {
    var value: Double
    var percent: Double
}

