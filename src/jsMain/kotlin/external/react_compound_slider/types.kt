@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent
import react.dom.events.PointerEvent
import react.dom.events.TouchEvent

external interface StandardEventHandlers {
    var onKeyDown: ((event: KeyboardEvent<*>) -> Unit)?
    var onMouseDown: ((event: MouseEvent<*, *>) -> Unit)?
    var onTouchStart: ((event: TouchEvent<*>) -> Unit)?
    var onPointerDown: ((event: PointerEvent<*>) -> Unit)?
}

external interface StandardEventEmitters {
    var emitMouse: EmitMouse?
    var emitKeyboard: EmitKeyboard?
    var emitTouch: EmitTouch?
    var emitPointer: EmitPointer?
}

external interface EventData {
    var value: Double
    var percent: Double
}

external interface OtherProps {
    @nativeGetter
    operator fun get(key: String): Any?
    @nativeSetter
    operator fun set(key: String, value: Any)
}