@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Quaternion(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, w: Number = definedExternally) {
    open var x: Number
    open var y: Number
    open var z: Number
    open var w: Number
    open var isQuaternion: Boolean
    open fun set(x: Number, y: Number, z: Number, w: Number): Quaternion
    open fun clone(): Quaternion /* this */
    open fun copy(q: Quaternion): Quaternion /* this */
    open fun setFromEuler(euler: Euler): Quaternion
    open fun setFromAxisAngle(axis: Vector3, angle: Number): Quaternion
    open fun setFromRotationMatrix(m: Matrix4): Quaternion
    open fun setFromUnitVectors(vFrom: Vector3, vTo: Vector3): Quaternion
    open fun angleTo(q: Quaternion): Number
    open fun rotateTowards(q: Quaternion, step: Number): Quaternion
    open fun identity(): Quaternion
    open fun inverse(): Quaternion
    open fun conjugate(): Quaternion
    open fun dot(v: Quaternion): Number
    open fun lengthSq(): Number
    open fun length(): Number
    open fun normalize(): Quaternion
    open fun multiply(q: Quaternion): Quaternion
    open fun premultiply(q: Quaternion): Quaternion
    open fun multiplyQuaternions(a: Quaternion, b: Quaternion): Quaternion
    open fun slerp(qb: Quaternion, t: Number): Quaternion
    open fun equals(v: Quaternion): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Quaternion /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Quaternion /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(array: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun _onChange(callback: Function<*>): Quaternion
    open var _onChangeCallback: Function<*>
    open fun multiplyVector3(v: Any): Any

    companion object {
        fun slerp(qa: Quaternion, qb: Quaternion, qm: Quaternion, t: Number): Quaternion
        fun slerpFlat(dst: Array<Number>, dstOffset: Number, src0: Array<Number>, srcOffset: Number, src1: Array<Number>, stcOffset1: Number, t: Number): Quaternion
        fun multiplyQuaternionsFlat(dst: Array<Number>, dstOffset: Number, src0: Array<Number>, srcOffset: Number, src1: Array<Number>, stcOffset1: Number): Array<Number>
    }
}