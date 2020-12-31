@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

external interface Matrix {
    var elements: Array<Number>
    fun identity(): Matrix
    fun copy(m: Matrix /* this */): Matrix /* this */
    fun multiplyScalar(s: Number): Matrix
    fun determinant(): Number
    fun getInverse(matrix: Matrix): Matrix
    fun transpose(): Matrix
    fun clone(): Matrix /* this */
}

open external class Matrix3 : Matrix {
    override var elements: Array<Number>
    open fun set(n11: Number, n12: Number, n13: Number, n21: Number, n22: Number, n23: Number, n31: Number, n32: Number, n33: Number): Matrix3
    override fun identity(): Matrix3
    override fun clone(): Matrix3 /* this */
    open fun copy(m: Matrix3): Matrix3 /* this */
    override fun copy(m: Matrix /* this */): Matrix /* this */
    open fun extractBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix3
    open fun setFromMatrix4(m: Matrix4): Matrix3
    override fun multiplyScalar(s: Number): Matrix3
    override fun determinant(): Number
    open fun getInverse(matrix: Matrix3): Matrix3
    override fun getInverse(matrix: Matrix): Matrix
    override fun transpose(): Matrix3
    open fun getNormalMatrix(matrix4: Matrix4): Matrix3
    open fun transposeIntoArray(r: Array<Number>): Matrix3
    open fun setUvTransform(tx: Number, ty: Number, sx: Number, sy: Number, rotation: Number, cx: Number, cy: Number): Matrix3
    open fun scale(sx: Number, sy: Number): Matrix3
    open fun rotate(theta: Number): Matrix3
    open fun translate(tx: Number, ty: Number): Matrix3
    open fun equals(matrix: Matrix3): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Matrix3
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Matrix3
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(array: ArrayLike<Number> = definedExternally, offset: Number = definedExternally): ArrayLike<Number>
    open fun multiply(m: Matrix3): Matrix3
    open fun premultiply(m: Matrix3): Matrix3
    open fun multiplyMatrices(a: Matrix3, b: Matrix3): Matrix3
    open fun multiplyVector3(vector: Vector3): Any
    open fun multiplyVector3Array(a: Any): Any
    open fun getInverse(matrix: Matrix4, throwOnDegenerate: Boolean = definedExternally): Matrix3
    open fun flattenToArrayOffset(array: Array<Number>, offset: Number): Array<Number>
}