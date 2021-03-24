@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

external interface LinearScale {
    var interpolator: Interpolator?
    var domain: Array<Number>
    var range: Array<Number>
    fun createInterpolator(domain: Array<Number>, range: Array<Number>): (x: Number) -> Number
    fun interpolateValue(a: Number, b: Number): (t: Number) -> Number
    fun deinterpolateValue(a: Number, b: Number): (x: Number) -> Number
    fun rescale(): LinearScale /* this */
    fun getValue(x: Number): Number
    fun setDomain(param_val: Array<Number>): LinearScale /* this */
    fun getDomain(): Array<Number>
    fun setRange(param_val: Array<Number>): LinearScale /* this */
    fun getRange(): Array<Number>
    fun getTicks(count: Number): Array<Number>
}