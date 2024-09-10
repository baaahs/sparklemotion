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

external interface QuaternionLike {
    val x: Number
    val y: Number
    val z: Number
    val w: Number
}

open external class Quaternion(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, w: Number = definedExternally) {
    open var x: Number
    open var y: Number
    open var z: Number
    open var w: Number
    open val isQuaternion: Boolean
    open fun set(x: Number, y: Number, z: Number, w: Number): Quaternion /* this */
    open fun clone(): Quaternion /* this */
    open fun copy(q: QuaternionLike): Quaternion /* this */
    open fun setFromEuler(euler: Euler, update: Boolean = definedExternally): Quaternion /* this */
    open fun setFromAxisAngle(axis: Vector3Like, angle: Number): Quaternion /* this */
    open fun setFromRotationMatrix(m: Matrix4): Quaternion /* this */
    open fun setFromUnitVectors(vFrom: Vector3, vTo: Vector3Like): Quaternion /* this */
    open fun angleTo(q: Quaternion): Number
    open fun rotateTowards(q: Quaternion, step: Number): Quaternion /* this */
    open fun identity(): Quaternion /* this */
    open fun invert(): Quaternion /* this */
    open fun conjugate(): Quaternion /* this */
    open fun dot(v: Quaternion): Number
    open fun lengthSq(): Number
    open fun length(): Number
    open fun normalize(): Quaternion /* this */
    open fun multiply(q: Quaternion): Quaternion /* this */
    open fun premultiply(q: Quaternion): Quaternion /* this */
    open fun multiplyQuaternions(a: Quaternion, b: Quaternion): Quaternion /* this */
    open fun slerp(qb: Quaternion, t: Number): Quaternion /* this */
    open fun slerpQuaternions(qa: Quaternion, qb: Quaternion, t: Number): Quaternion /* this */
    open fun equals(v: Quaternion): Boolean
    open fun fromArray(array: Any /* JsTuple<x, Number, y, Number, z, Number, w, Number> */, offset: Number = definedExternally): Quaternion /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): dynamic /* JsTuple<x, Number, y, Number, z, Number, w, Number> */
    open fun toArray(): dynamic /* JsTuple<x, Number, y, Number, z, Number, w, Number> */
    open fun toArray(array: Array<Number> = definedExternally): dynamic /* JsTuple<x, Number, y, Number, z, Number, w, Number> */
    open fun toArray(array: ArrayLike<Number>, offset: Number = definedExternally): dynamic /* JsTuple<x, Number, y, Number, z, Number, w, Number> */
    open fun toArray(array: ArrayLike<Number>): dynamic /* JsTuple<x, Number, y, Number, z, Number, w, Number> */
    open fun toJSON(): dynamic /* JsTuple<Number, Number, Number, Number> */
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Quaternion /* this */
    open fun fromBufferAttribute(attribute: InterleavedBufferAttribute, index: Number): Quaternion /* this */
    open fun _onChange(callback: () -> Unit): Quaternion /* this */
    open var _onChangeCallback: () -> Unit
    open fun random(): Quaternion /* this */

    companion object {
        fun slerpFlat(dst: Array<Number>, dstOffset: Number, src0: Array<Number>, srcOffset: Number, src1: Array<Number>, stcOffset1: Number, t: Number)
        fun multiplyQuaternionsFlat(dst: Array<Number>, dstOffset: Number, src0: Array<Number>, srcOffset: Number, src1: Array<Number>, stcOffset1: Number): Array<Number>
    }
}