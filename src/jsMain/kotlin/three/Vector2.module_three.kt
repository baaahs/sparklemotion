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

external interface Vector {
    fun setComponent(index: Number, value: Number): Vector /* this */
    fun getComponent(index: Number): Number
    fun set(vararg args: Number): Vector /* this */
    fun setScalar(scalar: Number): Vector /* this */
    fun copy(v: Vector): Vector /* this */
    fun add(v: Vector): Vector /* this */
    fun addVectors(a: Vector, b: Vector): Vector /* this */
    fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    fun addScalar(scalar: Number): Vector /* this */
    fun sub(v: Vector): Vector /* this */
    fun subVectors(a: Vector, b: Vector): Vector /* this */
    fun multiplyScalar(s: Number): Vector /* this */
    fun divideScalar(s: Number): Vector /* this */
    fun negate(): Vector /* this */
    fun dot(v: Vector): Number
    fun lengthSq(): Number
    fun length(): Number
    fun normalize(): Vector /* this */
    val distanceTo: ((v: Vector) -> Number)?
        get() = definedExternally
    val distanceToSquared: ((v: Vector) -> Number)?
        get() = definedExternally
    fun setLength(l: Number): Vector /* this */
    fun lerp(v: Vector, alpha: Number): Vector /* this */
    fun equals(v: Vector): Boolean
    fun clone(): Vector /* this */
}

open external class Vector2(x: Number = definedExternally, y: Number = definedExternally) : Vector {
    open var x: Number
    open var y: Number
    open var width: Number
    open var height: Number
    open var isVector2: Boolean
    open fun set(x: Number, y: Number): Vector2 /* this */
    override fun setScalar(scalar: Number): Vector2 /* this */
    open fun setX(x: Number): Vector2 /* this */
    open fun setY(y: Number): Vector2 /* this */
    override fun setComponent(index: Number, value: Number): Vector2 /* this */
    override fun getComponent(index: Number): Number
    override fun clone(): Vector2 /* this */
    open fun copy(v: Vector2): Vector2 /* this */
    override fun copy(v: Vector): Vector /* this */
    open fun add(v: Vector2, w: Vector2 = definedExternally): Vector2 /* this */
    override fun addScalar(s: Number): Vector2 /* this */
    open fun addVectors(a: Vector2, b: Vector2): Vector2 /* this */
    override fun addVectors(a: Vector, b: Vector): Vector /* this */
    open fun addScaledVector(v: Vector2, s: Number): Vector2 /* this */
    override fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    open fun sub(v: Vector2): Vector2 /* this */
    override fun sub(v: Vector): Vector /* this */
    open fun subScalar(s: Number): Vector2 /* this */
    open fun subVectors(a: Vector2, b: Vector2): Vector2 /* this */
    override fun subVectors(a: Vector, b: Vector): Vector /* this */
    open fun multiply(v: Vector2): Vector2 /* this */
    override fun multiplyScalar(scalar: Number): Vector2 /* this */
    open fun divide(v: Vector2): Vector2 /* this */
    override fun divideScalar(s: Number): Vector2 /* this */
    open fun applyMatrix3(m: Matrix3): Vector2 /* this */
    open fun min(v: Vector2): Vector2 /* this */
    open fun max(v: Vector2): Vector2 /* this */
    open fun clamp(min: Vector2, max: Vector2): Vector2 /* this */
    open fun clampScalar(min: Number, max: Number): Vector2 /* this */
    open fun clampLength(min: Number, max: Number): Vector2 /* this */
    open fun floor(): Vector2 /* this */
    open fun ceil(): Vector2 /* this */
    open fun round(): Vector2 /* this */
    open fun roundToZero(): Vector2 /* this */
    override fun negate(): Vector2 /* this */
    open fun dot(v: Vector2): Number
    override fun dot(v: Vector): Number
    open fun cross(v: Vector2): Number
    override fun lengthSq(): Number
    override fun length(): Number
    open fun lengthManhattan(): Number
    open fun manhattanLength(): Number
    override fun normalize(): Vector2 /* this */
    open fun angle(): Number
    open fun distanceTo(v: Vector2): Number
    open fun distanceToSquared(v: Vector2): Number
    open fun distanceToManhattan(v: Vector2): Number
    open fun manhattanDistanceTo(v: Vector2): Number
    override fun setLength(length: Number): Vector2 /* this */
    open fun lerp(v: Vector2, alpha: Number): Vector2 /* this */
    override fun lerp(v: Vector, alpha: Number): Vector /* this */
    open fun lerpVectors(v1: Vector2, v2: Vector2, alpha: Number): Vector2 /* this */
    open fun equals(v: Vector2): Boolean
    override fun equals(v: Vector): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Vector2 /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Vector2 /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(array: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Vector2 /* this */
    open fun rotateAround(center: Vector2, angle: Number): Vector2 /* this */
    open fun random(): Vector2 /* this */
}