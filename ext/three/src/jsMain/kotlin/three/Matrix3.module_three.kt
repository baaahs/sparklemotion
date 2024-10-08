@file:JsModule("three")
@file:JsNonModule
package three

open external class Matrix3 {
    open val isMatrix3: Boolean
    open var elements: DoubleArray /* JsTuple<n11, Number, n12, Number, n13, Number, n21, Number, n22, Number, n23, Number, n31, Number, n32, Number, n33, Number> */
    constructor()
    constructor(n11: Number, n12: Number, n13: Number, n21: Number, n22: Number, n23: Number, n31: Number, n32: Number, n33: Number)
    open fun set(n11: Number, n12: Number, n13: Number, n21: Number, n22: Number, n23: Number, n31: Number, n32: Number, n33: Number): Matrix3
    open fun identity(): Matrix3 /* this */
    open fun copy(m: Matrix3): Matrix3 /* this */
    open fun extractBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix3 /* this */
    open fun setFromMatrix4(m: Matrix4): Matrix3
    open fun multiply(m: Matrix3): Matrix3 /* this */
    open fun premultiply(m: Matrix3): Matrix3 /* this */
    open fun multiplyMatrices(a: Matrix3, b: Matrix3): Matrix3 /* this */
    open fun multiplyScalar(s: Number): Matrix3 /* this */
    open fun determinant(): Number
    open fun invert(): Matrix3 /* this */
    open fun transpose(): Matrix3 /* this */
    open fun getNormalMatrix(matrix4: Matrix4): Matrix3 /* this */
    open fun transposeIntoArray(r: Array<Number>): Matrix3 /* this */
    open fun setUvTransform(tx: Number, ty: Number, sx: Number, sy: Number, rotation: Number, cx: Number, cy: Number): Matrix3 /* this */
    open fun scale(sx: Number, sy: Number): Matrix3 /* this */
    open fun rotate(theta: Number): Matrix3 /* this */
    open fun translate(tx: Number, ty: Number): Matrix3 /* this */
    open fun makeTranslation(v: Vector2): Matrix3 /* this */
    open fun makeTranslation(x: Number, y: Number): Matrix3 /* this */
    open fun makeRotation(theta: Number): Matrix3 /* this */
    open fun makeScale(x: Number, y: Number): Matrix3 /* this */
    open fun equals(matrix: Matrix3): Boolean
    open fun fromArray(array: Array<Number>, offset: Int = definedExternally): Matrix3 /* this */
    open fun fromArray(array: DoubleArray, offset: Int = definedExternally): Matrix3 /* this */
    open fun toArray(): DoubleArray /* JsTuple<n11, Number, n12, Number, n13, Number, n21, Number, n22, Number, n23, Number, n31, Number, n32, Number, n33, Number> */
    open fun <TArray : Array<Number>> toArray(array: TArray, offset: Int = definedExternally): TArray
    open fun <TArray : Array<Number>> toArray(array: TArray): TArray
    open fun clone(): Matrix3 /* this */
}