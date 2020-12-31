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

open external class Vector4(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally, w: Number = definedExternally) : Vector {
    open var x: Number
    open var y: Number
    open var z: Number
    open var w: Number
    open var width: Number
    open var height: Number
    open var isVector4: Boolean
    open fun set(x: Number, y: Number, z: Number, w: Number): Vector4 /* this */
    override fun setScalar(scalar: Number): Vector4 /* this */
    open fun setX(x: Number): Vector4 /* this */
    open fun setY(y: Number): Vector4 /* this */
    open fun setZ(z: Number): Vector4 /* this */
    open fun setW(w: Number): Vector4 /* this */
    override fun setComponent(index: Number, value: Number): Vector4 /* this */
    override fun getComponent(index: Number): Number
    override fun clone(): Vector4 /* this */
    open fun copy(v: Vector4): Vector4 /* this */
    override fun copy(v: Vector): Vector /* this */
    open fun add(v: Vector4): Vector4 /* this */
    override fun add(v: Vector): Vector /* this */
    override fun addScalar(scalar: Number): Vector4 /* this */
    open fun addVectors(a: Vector4, b: Vector4): Vector4 /* this */
    override fun addVectors(a: Vector, b: Vector): Vector /* this */
    open fun addScaledVector(v: Vector4, s: Number): Vector4 /* this */
    override fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    open fun sub(v: Vector4): Vector4 /* this */
    override fun sub(v: Vector): Vector /* this */
    open fun subScalar(s: Number): Vector4 /* this */
    open fun subVectors(a: Vector4, b: Vector4): Vector4 /* this */
    override fun subVectors(a: Vector, b: Vector): Vector /* this */
    override fun multiplyScalar(s: Number): Vector4 /* this */
    open fun applyMatrix4(m: Matrix4): Vector4 /* this */
    override fun divideScalar(s: Number): Vector4 /* this */
    open fun setAxisAngleFromQuaternion(q: Quaternion): Vector4 /* this */
    open fun setAxisAngleFromRotationMatrix(m: Matrix3): Vector4 /* this */
    open fun min(v: Vector4): Vector4 /* this */
    open fun max(v: Vector4): Vector4 /* this */
    open fun clamp(min: Vector4, max: Vector4): Vector4 /* this */
    open fun clampScalar(min: Number, max: Number): Vector4 /* this */
    open fun floor(): Vector4 /* this */
    open fun ceil(): Vector4 /* this */
    open fun round(): Vector4 /* this */
    open fun roundToZero(): Vector4 /* this */
    override fun negate(): Vector4 /* this */
    open fun dot(v: Vector4): Number
    override fun dot(v: Vector): Number
    override fun lengthSq(): Number
    override fun length(): Number
    open fun manhattanLength(): Number
    override fun normalize(): Vector4 /* this */
    override fun setLength(length: Number): Vector4 /* this */
    open fun lerp(v: Vector4, alpha: Number): Vector4 /* this */
    override fun lerp(v: Vector, alpha: Number): Vector /* this */
    open fun lerpVectors(v1: Vector4, v2: Vector4, alpha: Number): Vector4 /* this */
    open fun equals(v: Vector4): Boolean
    override fun equals(v: Vector): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Vector4 /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Vector4 /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(array: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Vector4 /* this */
    open fun random(): Vector4 /* this */
}