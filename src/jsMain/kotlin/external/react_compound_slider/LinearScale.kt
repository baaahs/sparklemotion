@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
@file:JsModule("react-compound-slider")

package external.react_compound_slider

external class LinearScale : Scale {
    var interpolator: Interpolator?
    override var domain: Array<Double>
    override var range: Array<Double>
    fun createInterpolator(domain: Array<Double>, range: Array<Double>): (x: Double) -> Double
    fun interpolateValue(a: Double, b: Double): (t: Double) -> Double
    fun deinterpolateValue(a: Double, b: Double): (x: Double) -> Double
    fun rescale(): LinearScale /* this */
    fun getValue(x: Double): Double
    fun setDomain(param_val: Array<Double>): LinearScale /* this */
    fun getDomain(): Array<Double>
    fun setRange(param_val: Array<Double>): LinearScale /* this */
    fun getRange(): Array<Double>
    fun getTicks(count: Double): Array<Double>
}