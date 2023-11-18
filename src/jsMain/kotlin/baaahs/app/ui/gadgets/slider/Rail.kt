package baaahs.app.ui.gadgets.slider

import baaahs.ui.xComponent
import js.core.jso
import react.*

external interface RailObject {
    var activeHandleId: String
    var getEventData: GetEventData
    var getRailProps: GetRailProps
}

val BetterRail = xComponent<RailProps>("BetterRail") { props ->
    // render():
    val railObject = jso<RailObject> {
        getRailProps = {
            jso {
                onPointerDown = { e ->
                    props.onPointerDown?.invoke(e)
                    props.emitPointer?.invoke(e, Location.Rail, null)
                }
            }
        }
    }
    +Children.only(props.children.invoke(railObject))
}

external interface RailProps : Props, StandardEventHandlers, StandardEventEmitters {
    /**
     * A function to render the rail. Note: `getEventData` can be called with an event and get the value and percent at that location (used for tooltips etc). `activeHandleID` will be a string or null.  Function signature: `({ getEventData, activeHandleID, getRailProps }): element`
     */
    var children: (railObject: RailObject) -> ReactElement<*>
}

fun RBuilder.betterRail(handler: RHandler<RailProps>) =
    child(BetterRail, handler = handler)
