@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package baaahs.app.ui.gadgets.slider

import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent
import react.ElementType
import react.Props
import react.ReactElement

external interface `T$14` {
    var onMouseDown: (e: MouseEvent) -> Unit
    var onTouchStart: (e: TouchEvent) -> Unit
}

external val Rail: ElementType<RailProps>

external interface RailObject {
    var activeHandleID: String
    var getEventData: GetEventData
    var getRailProps: GetRailProps
}

external interface RailProps : Props, StandardEventHandlers, StandardEventEmitters {
    /**
     * A function to render the rail. Note: `getEventData` can be called with an event and get the value and percent at that location (used for tooltips etc). `activeHandleID` will be a string or null.  Function signature: `({ getEventData, activeHandleID, getRailProps }): element`
     */
    var children: (railObject: RailObject) -> ReactElement<*>
}
