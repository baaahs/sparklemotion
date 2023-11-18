@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package baaahs.app.ui.gadgets.slider

import react.ElementType
import react.Props
import react.ReactElement

external val Handles : ElementType<HandlesProps>

external interface HandlesObject {
    var handles: Array<BetterSliderItem>
    var activeHandleID: String?
    var getHandleProps: GetHandleProps
}

external interface HandlesProps : Props, StandardEventHandlers, StandardEventEmitters {
    var handles: Array<BetterSliderItem>
    var activeHandleID: String?
    var children: (handlesObject: HandlesObject) -> ReactElement<*>
}

external interface HandleItem {
    var key: String
    var `val`: Double
}
