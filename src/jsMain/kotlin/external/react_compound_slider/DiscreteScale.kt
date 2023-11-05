@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

external class DiscreteScale : Scale {
    var step: Double
    override var domain: Range
    override var range: Range
    var setDomain: (param_val: Range) -> DiscreteScale
    var setRange: (param_val: Range) -> DiscreteScale
    var setStep: (param_val: Double) -> DiscreteScale
    var getValue: (x: Double) -> Double
}