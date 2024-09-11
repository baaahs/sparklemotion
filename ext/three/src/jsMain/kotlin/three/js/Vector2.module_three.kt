package three.js

external interface Vector2Like {
    val x: Double
    val y: Double
}

open external class Vector2(x: Number = definedExternally, y: Number = definedExternally) : Vector2Like {
    override var x: Double
    override var y: Double
    open var width: Double
    open var height: Double
    open val isVector2: Boolean
    open fun set(x: Number, y: Number): Vector2 /* this */
    open fun setScalar(scalar: Number): Vector2 /* this */
    open fun setX(x: Number): Vector2 /* this */
    open fun setY(y: Number): Vector2 /* this */
    open fun setComponent(index: Number, value: Number): Vector2 /* this */
    open fun getComponent(index: Number): Double
    open fun clone(): Vector2 /* this */
    open fun copy(v: Vector2Like): Vector2 /* this */
    open fun add(v: Vector2Like): Vector2 /* this */
    open fun addScalar(s: Number): Vector2 /* this */
    open fun addVectors(a: Vector2Like, b: Vector2Like): Vector2 /* this */
    open fun addScaledVector(v: Vector2Like, s: Number): Vector2 /* this */
    open fun sub(v: Vector2Like): Vector2 /* this */
    open fun subScalar(s: Number): Vector2 /* this */
    open fun subVectors(a: Vector2Like, b: Vector2Like): Vector2 /* this */
    open fun multiply(v: Vector2Like): Vector2 /* this */
    open fun multiplyScalar(scalar: Number): Vector2 /* this */
    open fun divide(v: Vector2Like): Vector2 /* this */
    open fun divideScalar(s: Number): Vector2 /* this */
    open fun applyMatrix3(m: Matrix3): Vector2 /* this */
    open fun min(v: Vector2Like): Vector2 /* this */
    open fun max(v: Vector2Like): Vector2 /* this */
    open fun clamp(min: Vector2Like, max: Vector2Like): Vector2 /* this */
    open fun clampScalar(min: Number, max: Number): Vector2 /* this */
    open fun clampLength(min: Number, max: Number): Vector2 /* this */
    open fun floor(): Vector2 /* this */
    open fun ceil(): Vector2 /* this */
    open fun round(): Vector2 /* this */
    open fun roundToZero(): Vector2 /* this */
    open fun negate(): Vector2 /* this */
    open fun dot(v: Vector2Like): Double
    open fun cross(v: Vector2Like): Double
    open fun lengthSq(): Double
    open fun length(): Double
    open fun manhattanLength(): Double
    open fun normalize(): Vector2 /* this */
    open fun angle(): Double
    open fun angleTo(v: Vector2): Double
    open fun distanceTo(v: Vector2Like): Double
    open fun distanceToSquared(v: Vector2Like): Double
    open fun manhattanDistanceTo(v: Vector2Like): Double
    open fun setLength(length: Number): Vector2 /* this */
    open fun lerp(v: Vector2Like, alpha: Number): Vector2 /* this */
    open fun lerpVectors(v1: Vector2Like, v2: Vector2Like, alpha: Number): Vector2 /* this */
    open fun equals(v: Vector2Like): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Vector2 /* this */
//    open fun fromArray(array: Array<Number>): Vector2 /* this */
//    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Vector2 /* this */
//    open fun fromArray(array: Array<Number>): Vector2 /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(): dynamic /* Array */
//    open fun toArray(array: Array<Number> = definedExternally): Array<Number>
    open fun toArray(array: Any /* JsTuple<x, Number, y, Number> */ = definedExternally, offset: Number /* 0 */ = definedExternally): dynamic /* JsTuple<x, Number, y, Number> */
    open fun toArray(array: Any /* JsTuple<x, Number, y, Number> */ = definedExternally): dynamic /* JsTuple<x, Number, y, Number> */
//    open fun toArray(array: Array<Number>, offset: Number = definedExternally): Array<Number>
//    open fun toArray(array: Array<Number>): Array<Number>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Vector2 /* this */
    open fun rotateAround(center: Vector2Like, angle: Number): Vector2 /* this */
    open fun random(): Vector2 /* this */
}