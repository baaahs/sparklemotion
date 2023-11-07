@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

import react.ElementType
import react.PropsWithChildren

external interface SliderData {
    var activeHandleID: String
}

external val Slider : ElementType<SliderProps>

external interface SliderProps : PropsWithChildren {
    /**
     * String component used for slider root. Defaults to 'div'.
     */
    var component: String?
    /**
     * An object with any inline styles you want applied to the root element.
     */
    var rootStyle: Any?
    /**
     * An object with any props you want applied to the root element.
     */
    var rootProps: Any?
    /**
     * CSS class name applied to the root element of the slider.
     */
    var className: String?
    /**
     * Two element array of numbers providing the min and max values for the slider [min, max] e.g. [0, 100].
     * It does not matter if the slider is reversed on the screen, domain is always [min, max] with min < max.
     */
    var domain: Array<Double>?
    /**
     * An array of numbers. You can supply one for a value slider, two for a range slider or more to create n-handled sliders.
     * The values should correspond to valid step values in the domain.
     * The numbers will be forced into the domain if they are two small or large.
     */
    var values: Map<String, Double>
    /**
     * The step value for the slider.
     */
    var step: Double?
    /**
     * The interaction mode. Value of 1 will allow handles to cross each other.
     * Value of 2 will keep the sliders from crossing and separated by a step.
     * Value of 3 will make the handles pushable and keep them a step apart.
     * ADVANCED: You can also supply a function that will be passed the current values and the incoming update.
     * Your function should return what the state should be set as.
     */
    var mode: Any? // ?: 1 | 2 | 3 | CustomMode;
    /**
     * Set to true if the slider is displayed vertically to tell the slider to use the height to calculate positions.
     */
    var vertical: Boolean?
    /**
     * Reverse the display of slider values.
     */
    var reversed: Boolean?
    /**
     * Function triggered when the value of the slider has changed. This will recieve changes at the end of a slide as well as changes from clicks on rails and tracks. Receives values.
     */
    var onChange: ((values: Map<String, Double>) -> Unit)?
    /**
     * Function called with the values at each update (caution: high-volume updates when dragging). Receives values.
     */
    var onUpdate: ((values: Map<String, Double>) -> Unit)?
    /**
     * Function triggered with ontouchstart or onmousedown on a handle. Receives values.
     */
    var onSlideStart: ((values: Map<String, Double>, data: SliderData) -> Unit)?
    /**
     * Function triggered on ontouchend or onmouseup on a handle. Receives values.
     */
    var onSlideEnd: ((values: Map<String, Double>, data: SliderData) -> Unit)?
    /**
     * Ignore all mouse, touch and keyboard events.
     */
    var disabled: Boolean
    /**
     * Render slider children as siblings. This is primarily for SVG sliders. See the SVG example.
     */
    var flatten: Boolean
    /**
     * When true, the slider will warn if values are changed to fit domain and step values.  Defaults to false.
     */
    var warnOnChanges: Boolean
}

external interface SliderItem {
    var id: String
    var value: Double
    var percent: Double
}
