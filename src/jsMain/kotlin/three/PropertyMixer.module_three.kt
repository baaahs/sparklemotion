@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class PropertyMixer(binding: Any, typeName: String, valueSize: Number) {
    open var binding: Any
    open var valueSize: Number
    open var buffer: Any
    open var cumulativeWeight: Number
    open var cumulativeWeightAdditive: Number
    open var useCount: Number
    open var referenceCount: Number
    open fun accumulate(accuIndex: Number, weight: Number)
    open fun accumulateAdditive(weight: Number)
    open fun apply(accuIndex: Number)
    open fun saveOriginalState()
    open fun restoreOriginalState()
}