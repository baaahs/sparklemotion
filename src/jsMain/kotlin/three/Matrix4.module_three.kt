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

open external class Matrix4 : Matrix {
    override var elements: Array<Number>
    open fun set(n11: Number, n12: Number, n13: Number, n14: Number, n21: Number, n22: Number, n23: Number, n24: Number, n31: Number, n32: Number, n33: Number, n34: Number, n41: Number, n42: Number, n43: Number, n44: Number): Matrix4
    override fun identity(): Matrix4
    override fun clone(): Matrix4 /* this */
    open fun copy(m: Matrix4): Matrix4 /* this */
    override fun copy(m: Matrix /* this */): Matrix /* this */
    open fun copyPosition(m: Matrix4): Matrix4
    open fun extractBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4
    open fun makeBasis(xAxis: Vector3, yAxis: Vector3, zAxis: Vector3): Matrix4
    open fun extractRotation(m: Matrix4): Matrix4
    open fun makeRotationFromEuler(euler: Euler): Matrix4
    open fun makeRotationFromQuaternion(q: Quaternion): Matrix4
    open fun lookAt(eye: Vector3, target: Vector3, up: Vector3): Matrix4
    open fun multiply(m: Matrix4): Matrix4
    open fun premultiply(m: Matrix4): Matrix4
    open fun multiplyMatrices(a: Matrix4, b: Matrix4): Matrix4
    open fun multiplyToArray(a: Matrix4, b: Matrix4, r: Array<Number>): Matrix4
    override fun multiplyScalar(s: Number): Matrix4
    override fun determinant(): Number
    override fun transpose(): Matrix4
    open fun setPosition(v: Vector3, y: Number = definedExternally, z: Number = definedExternally): Matrix4
    open fun setPosition(v: Number, y: Number = definedExternally, z: Number = definedExternally): Matrix4
    open fun getInverse(m: Matrix4): Matrix4
    override fun getInverse(matrix: Matrix): Matrix
    open fun scale(v: Vector3): Matrix4
    open fun getMaxScaleOnAxis(): Number
    open fun makeTranslation(x: Number, y: Number, z: Number): Matrix4
    open fun makeRotationX(theta: Number): Matrix4
    open fun makeRotationY(theta: Number): Matrix4
    open fun makeRotationZ(theta: Number): Matrix4
    open fun makeRotationAxis(axis: Vector3, angle: Number): Matrix4
    open fun makeScale(x: Number, y: Number, z: Number): Matrix4
    open fun compose(translation: Vector3, rotation: Quaternion, scale: Vector3): Matrix4
    open fun decompose(translation: Vector3, rotation: Quaternion, scale: Vector3): Matrix4
    open fun makePerspective(left: Number, right: Number, bottom: Number, top: Number, near: Number, far: Number): Matrix4
    open fun makePerspective(fov: Number, aspect: Number, near: Number, far: Number): Matrix4
    open fun makeOrthographic(left: Number, right: Number, top: Number, bottom: Number, near: Number, far: Number): Matrix4
    open fun equals(matrix: Matrix4): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Matrix4
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Matrix4
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(array: ArrayLike<Number> = definedExternally, offset: Number = definedExternally): ArrayLike<Number>
    open fun extractPosition(m: Matrix4): Matrix4
    open fun setRotationFromQuaternion(q: Quaternion): Matrix4
    open fun multiplyVector3(v: Any): Any
    open fun multiplyVector4(v: Any): Any
    open fun multiplyVector3Array(array: Array<Number>): Array<Number>
    open fun rotateAxis(v: Any)
    open fun crossVector(v: Any)
    open fun flattenToArrayOffset(array: Array<Number>, offset: Number): Array<Number>
}