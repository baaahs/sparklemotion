@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

import react.ElementType
import react.Props
import react.ReactElement

external val Ticks : ElementType<TicksProps>

external interface TicksObject {
    val activeHandleID: String
    val getEventData: GetEventData
    val ticks: Array<SliderItem>
}

external interface RcsProps : Props, StandardEventEmitters {
    var scale: LinearScale?
    var handles: Array<SliderItem>
    var activeHandleID: String?
    var getEventData: GetEventData?
}

external interface TicksProps : Props {
    /** @ignore */
    var scale: LinearScale?
    /**
     * Approximate number of ticks you want to render.
     * If you supply your own ticks using the values prop this prop has no effect.
     */
    var count: Number?
    /**
     * The values prop should be an array of unique numbers.
     * Use this prop if you want to specify your own tick values instead of ticks generated by the slider.
     * The numbers should be valid numbers in the domain and correspond to the step value.
     * Invalid values will be coerced to the closet matching value in the domain.
     */
    var values: Array<Number>?
    /** @ignore */
    var getEventData: GetEventData?
    /** @ignore */
    var activeHandleID: String?
//    /** @ignore */
//    var emitMouse: EmitMouse?
//    /** @ignore */
//    var emitTouch: EmitTouch?
    /**
     * A function to render the ticks.
     * The function receives an object with an array of ticks. Note: `getEventData` can be called with an event and get the value and percent at that location (used for tooltips etc). `activeHandleID` will be a string or null.  Function signature:
     * `({ getEventData, activeHandleID, ticks  }): element`
     */
    var children: (ticksObject: TicksObject) -> ReactElement<*>
}
