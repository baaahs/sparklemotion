@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class QuaternionLinearInterpolant(parameterPositions: Any, samplesValues: Any, sampleSize: Number, resultBuffer: Any = definedExternally) : Interpolant {
    open fun interpolate_(i1: Number, t0: Number, t: Number, t1: Number): Any
}