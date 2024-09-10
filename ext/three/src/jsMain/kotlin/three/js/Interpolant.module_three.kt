package three.js

open external class Interpolant(parameterPositions: Any, sampleValues: Any, sampleSize: Number, resultBuffer: Any = definedExternally) {
    open var parameterPositions: Any
    open var sampleValues: Any
    open var valueSize: Number
    open var resultBuffer: Any
    open fun evaluate(time: Number): Any
}