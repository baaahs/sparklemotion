package three.js

import js.array.ArrayLike

open external class Matrix2 {
    open val isMatrix2: Boolean
    open var elements: dynamic /* JsTuple<n11, Number, n12, Number, n21, Number, n22, Number> */
    constructor()
    constructor(n11: Number, n12: Number, n21: Number, n22: Number)
    open fun identity(): Matrix2 /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Matrix2 /* this */
    open fun set(n11: Number, n12: Number, n21: Number, n22: Number): Matrix2 /* this */
}