package three.js

import js.array.ArrayLike
import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

open external class Matrix3 {
    open val isMatrix3: Boolean
    open var elements: dynamic /* JsTuple<n11, Number, n12, Number, n13, Number, n21, Number, n22, Number, n23, Number, n31, Number, n32, Number, n33, Number> */
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
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Matrix3 /* this */
    open fun toArray(): dynamic /* JsTuple<n11, Number, n12, Number, n13, Number, n21, Number, n22, Number, n23, Number, n31, Number, n32, Number, n33, Number> */
    open fun <TArray : ArrayLike<Number>> toArray(array: TArray, offset: Number = definedExternally): TArray
    open fun <TArray : ArrayLike<Number>> toArray(array: TArray): TArray
    open fun clone(): Matrix3 /* this */
}