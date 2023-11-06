@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

import react.dom.events.KeyboardEvent
import react.dom.events.MouseEvent
import react.dom.events.TouchEvent

external interface HandleEventHandlers {
    var onKeyDown: ((event: KeyboardEvent<*>) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onMouseDown: ((event: MouseEvent<*, *>) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
    var onTouchStart: ((event: TouchEvent<*>) -> Unit)?
        get() = definedExternally
        set(value) = definedExternally
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

external interface Scale {
    var domain: Array<Double>
    var range: Array<Double>
}