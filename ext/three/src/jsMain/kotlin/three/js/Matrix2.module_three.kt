package three.js

open external class Matrix2 {
    open val isMatrix2: Boolean
    open var elements: DoubleArray /* JsTuple<n11, Number, n12, Number, n21, Number, n22, Number> */
    constructor()
    constructor(n11: Number, n12: Number, n21: Number, n22: Number)
    open fun identity(): Matrix2 /* this */
    open fun fromArray(array: Array<Number>, offset: Int = definedExternally): Matrix2 /* this */
    open fun fromArray(array: DoubleArray, offset: Int = definedExternally): Matrix2 /* this */
    open fun set(n11: Number, n12: Number, n21: Number, n22: Number): Matrix2 /* this */
}