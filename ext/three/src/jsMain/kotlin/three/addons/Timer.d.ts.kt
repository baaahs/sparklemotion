@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

open external class Timer {
    open fun getDelta(): Number
    open fun getElapsed(): Number
    open fun getTimescale(): Number
    open fun setTimescale(timescale: Number): Timer /* this */
    open fun reset(): Timer /* this */
    open fun dispose(): Timer /* this */
    open fun update(timestamp: Number = definedExternally): Timer /* this */
}

open external class FixedTimer(fps: Number = definedExternally) : Timer {
    open var override: Any
    open fun update(): FixedTimer /* this */
}