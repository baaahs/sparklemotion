@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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

external interface Vector4Like {
    val x: Number
    val y: Number
    val z: Number
    val w: Number
}

external open class Vector4(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, w: Number = definedExternally) {
    open var x: Number
    open var y: Number
    open var z: Number
    open var w: Number
    open var width: Number
    open var height: Number
    open val isVector4: Boolean
    open fun set(x: Number, y: Number, z: Number, w: Number): Vector4 /* this */
    open fun setScalar(scalar: Number): Vector4 /* this */
    open fun setX(x: Number): Vector4 /* this */
    open fun setY(y: Number): Vector4 /* this */
    open fun setZ(z: Number): Vector4 /* this */
    open fun setW(w: Number): Vector4 /* this */
    open fun setComponent(index: Number, value: Number): Vector4 /* this */
    open fun getComponent(index: Number): Number
    open fun clone(): Vector4 /* this */
    open fun copy(v: Vector4Like): Vector4 /* this */
    open fun add(v: Vector4Like): Vector4 /* this */
    open fun addScalar(scalar: Number): Vector4 /* this */
    open fun addVectors(a: Vector4Like, b: Vector4Like): Vector4 /* this */
    open fun addScaledVector(v: Vector4Like, s: Number): Vector4 /* this */
    open fun sub(v: Vector4Like): Vector4 /* this */
    open fun subScalar(s: Number): Vector4 /* this */
    open fun subVectors(a: Vector4Like, b: Vector4Like): Vector4 /* this */
    open fun multiply(v: Vector4Like): Vector4 /* this */
    open fun multiplyScalar(s: Number): Vector4 /* this */
    open fun applyMatrix4(m: Matrix4): Vector4 /* this */
    open fun divideScalar(s: Number): Vector4 /* this */
    open fun setAxisAngleFromQuaternion(q: QuaternionLike): Vector4 /* this */
    open fun setAxisAngleFromRotationMatrix(m: Matrix4): Vector4 /* this */
    open fun setFromMatrixPosition(m: Matrix4): Vector4 /* this */
    open fun min(v: Vector4Like): Vector4 /* this */
    open fun max(v: Vector4Like): Vector4 /* this */
    open fun clamp(min: Vector4Like, max: Vector4Like): Vector4 /* this */
    open fun clampScalar(min: Number, max: Number): Vector4 /* this */
    open fun floor(): Vector4 /* this */
    open fun ceil(): Vector4 /* this */
    open fun round(): Vector4 /* this */
    open fun roundToZero(): Vector4 /* this */
    open fun negate(): Vector4 /* this */
    open fun dot(v: Vector4Like): Number
    open fun lengthSq(): Number
    open fun length(): Number
    open fun manhattanLength(): Number
    open fun normalize(): Vector4 /* this */
    open fun setLength(length: Number): Vector4 /* this */
    open fun lerp(v: Vector4Like, alpha: Number): Vector4 /* this */
    open fun lerpVectors(v1: Vector4Like, v2: Vector4Like, alpha: Number): Vector4 /* this */
    open fun equals(v: Vector4Like): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Vector4 /* this */
    open fun fromArray(array: Array<Number>): Vector4 /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Vector4 /* this */
    open fun fromArray(array: ArrayLike<Number>): Vector4 /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(): dynamic /* Array */
    open fun toArray(array: Array<Number> = definedExternally): Array<Number>
    open fun toArray(array: Any /* JsTuple<Number, Number, Number, Number> */ = definedExternally, offset: Number /* 0 */ = definedExternally): dynamic /* JsTuple<Number, Number, Number, Number> */
    open fun toArray(array: Any /* JsTuple<Number, Number, Number, Number> */ = definedExternally): dynamic /* JsTuple<Number, Number, Number, Number> */
    open fun toArray(array: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun toArray(array: ArrayLike<Number>): ArrayLike<Number>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Vector4 /* this */
    open fun random(): Vector4 /* this */
}