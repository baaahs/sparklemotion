@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

import react.ElementType
import react.Props
import react.ReactElement

external interface `T$15` {
//    var onKeyDown: (e: React.KeyboardEvent<Element>) -> Unit
//    var onMouseDown: (e: React.MouseEvent<Element, MouseEvent>) -> Unit
//    var onTouchStart: (e: React.TouchEvent<Element>) -> Unit
}

external val Handles : ElementType<HandlesProps>

external interface HandlesObject {
    var handles: Array<SliderItem>
    var activeHandleID: String
    var getHandleProps: GetHandleProps
}

external interface HandlesProps : Props {
    var activeHandleID: String?
        get() = definedExternally
        set(value) = definedExternally
    var handles: Array<SliderItem>?
        get() = definedExternally
        set(value) = definedExternally
    //    var emitMouse: EmitMouse?
//        get() = definedExternally
//        set(value) = definedExternally
//    var emitKeyboard: EmitKeyboard?
//        get() = definedExternally
//        set(value) = definedExternally
//    var emitTouch: EmitTouch?
//        get() = definedExternally
//        set(value) = definedExternally
    var children: (handlesObject: HandlesObject) -> ReactElement<*>
}

external interface HandleItem {
    var key: String
    var `val`: Double
}
