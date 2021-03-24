@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

external interface DiscreteScale {
    var step: Number
    var domain: Array<Number>
    var range: Array<Number>
    var setDomain: (param_val: Array<Number>) -> DiscreteScale
    var setRange: (param_val: Array<Number>) -> DiscreteScale
    var setStep: (param_val: Number) -> DiscreteScale
    var getValue: (x: Number) -> Number
}