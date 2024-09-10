@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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

open external class Matrix4 {
    open val isMatrix4: Boolean
    open var elements: dynamic /* JsTuple<n11, Number, n12, Number, n13, Number, n14, Number, n21, Number, n22, Number, n23, Number, n24, Number, n31, Number, n32, Number, n33, Number, n34, Number, n41, Number, n42, Number, n43, Number, n44, Number> */
    constructor()
    constructor(n11: Number, n12: Number, n13: Number, n14: Number, n21: Number, n22: Number, n23: Number, n24: Number, n31: Number, n32: Number, n33: Number, n34: Number, n41: Number, n42: Number, n43: Number, n44: Number)
    open fun set(n11: Number, n12: Number, n13: Number, n14: Number, n21: Number, n22: Number, n23: Number, n24: Number, n31: Number, n32: Number, n33: Number, n34: Number, n41: Number, n42: Number, n43: Number, n44: Number): Matrix4 /* this */
    open fun identity(): Matrix4 /* this */
    open fun clone(): Matrix4
    open fun copy(m: Matrix4): Matrix4 /* this */
    open fun copyPosition(m: Matrix4): Matrix4 /* this */
    open fun setFromMatrix3(m: Matrix3): Matrix4 /* this */
    open fun extractBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4 /* this */
    open fun makeBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4 /* this */
    open fun extractRotation(m: Matrix4): Matrix4 /* this */
    open fun makeRotationFromEuler(euler: Euler): Matrix4 /* this */
    open fun makeRotationFromQuaternion(q: Quaternion): Matrix4 /* this */
    open fun lookAt(eye: Vector3, target: Vector3, up: Vector3): Matrix4 /* this */
    open fun multiply(m: Matrix4): Matrix4 /* this */
    open fun premultiply(m: Matrix4): Matrix4 /* this */
    open fun multiplyMatrices(a: Matrix4, b: Matrix4): Matrix4 /* this */
    open fun multiplyScalar(s: Number): Matrix4 /* this */
    open fun determinant(): Number
    open fun transpose(): Matrix4 /* this */
    open fun setPosition(v: Vector3): Matrix4 /* this */
    open fun setPosition(x: Number, y: Number, z: Number): Matrix4 /* this */
    open fun invert(): Matrix4 /* this */
    open fun scale(v: Vector3): Matrix4 /* this */
    open fun getMaxScaleOnAxis(): Number
    open fun makeTranslation(v: Vector3): Matrix4 /* this */
    open fun makeTranslation(x: Number, y: Number, z: Number): Matrix4 /* this */
    open fun makeRotationX(theta: Number): Matrix4 /* this */
    open fun makeRotationY(theta: Number): Matrix4 /* this */
    open fun makeRotationZ(theta: Number): Matrix4 /* this */
    open fun makeRotationAxis(axis: Vector3, angle: Number): Matrix4 /* this */
    open fun makeScale(x: Number, y: Number, z: Number): Matrix4 /* this */
    open fun makeShear(xy: Number, xz: Number, yx: Number, yz: Number, zx: Number, zy: Number): Matrix4 /* this */
    open fun compose(position: Vector3, quaternion: Quaternion, scale: Vector3): Matrix4 /* this */
    open fun decompose(position: Vector3, quaternion: Quaternion, scale: Vector3): Matrix4 /* this */
    open fun makePerspective(left: Number, right: Number, top: Number, bottom: Number, near: Number, far: Number, coordinateSystem: Any = definedExternally): Matrix4 /* this */
    open fun makeOrthographic(left: Number, right: Number, top: Number, bottom: Number, near: Number, far: Number, coordinateSystem: Any = definedExternally): Matrix4 /* this */
    open fun equals(matrix: Matrix4): Boolean
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Matrix4 /* this */
    open fun toArray(): dynamic /* JsTuple<n11, Number, n12, Number, n13, Number, n14, Number, n21, Number, n22, Number, n23, Number, n24, Number, n31, Number, n32, Number, n33, Number, n34, Number, n41, Number, n42, Number, n43, Number, n44, Number> */
    open fun <TArray : ArrayLike<Number>> toArray(array: TArray, offset: Number = definedExternally): TArray
    open fun <TArray : ArrayLike<Number>> toArray(array: TArray): TArray
}