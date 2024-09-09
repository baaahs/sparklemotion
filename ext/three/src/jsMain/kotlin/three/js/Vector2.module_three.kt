@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface Vector {
    fun setComponent(index: Number, value: Number): Vector /* this */
    fun getComponent(index: Number): Double
//    fun set(vararg args: Double): Vector /* this */
    fun setScalar(scalar: Double): Vector /* this */
    fun copy(v: Vector): Vector /* this */
    fun add(v: Vector): Vector /* this */
    fun addVectors(a: Vector, b: Vector): Vector /* this */
    fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    fun addScalar(scalar: Double): Vector /* this */
    fun sub(v: Vector): Vector /* this */
    fun subVectors(a: Vector, b: Vector): Vector /* this */
    fun multiplyScalar(s: Number): Vector /* this */
    fun divideScalar(s: Number): Vector /* this */
    fun negate(): Vector /* this */
    fun dot(v: Vector): Double
    fun lengthSq(): Double
    fun length(): Double
    fun normalize(): Vector /* this */
    val distanceTo: ((v: Vector) -> Double)?
        get() = definedExternally
    val distanceToSquared: ((v: Vector) -> Double)?
        get() = definedExternally
    fun setLength(l: Number): Vector /* this */
    fun lerp(v: Vector, alpha: Number): Vector /* this */
    fun equals(v: Vector): Boolean
    fun clone(): Vector /* this */
}

open external class Vector2(x: Double = definedExternally, y: Double = definedExternally) : Vector {
    constructor(x: Number, y: Number)

    open var x: Double
    open var y: Double
    open var width: Double
    open var height: Double
    open var isVector2: Boolean
    open fun set(x: Double, y: Double): Vector2 /* this */
    override fun setScalar(scalar: Double): Vector2 /* this */
    open fun setX(x: Double): Vector2 /* this */
    open fun setY(y: Double): Vector2 /* this */
    override fun setComponent(index: Number, value: Number): Vector2 /* this */
    override fun getComponent(index: Number): Double
    override fun clone(): Vector2 /* this */
    open fun copy(v: Vector2): Vector2 /* this */
    override fun copy(v: Vector): Vector /* this */
    override fun add(v: Vector): Vector2 /* this */
    open fun add(v: Vector2, w: Vector2 = definedExternally): Vector2 /* this */
    override fun addScalar(s: Double): Vector2 /* this */
    open fun addVectors(a: Vector2, b: Vector2): Vector2 /* this */
    override fun addVectors(a: Vector, b: Vector): Vector /* this */
    open fun addScaledVector(v: Vector2, s: Double): Vector2 /* this */
    override fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    open fun sub(v: Vector2): Vector2 /* this */
    override fun sub(v: Vector): Vector /* this */
    open fun subScalar(s: Double): Vector2 /* this */
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
    open fun clampScalar(min: Double, max: Double): Vector2 /* this */
    open fun clampLength(min: Double, max: Double): Vector2 /* this */
    open fun floor(): Vector2 /* this */
    open fun ceil(): Vector2 /* this */
    open fun round(): Vector2 /* this */
    open fun roundToZero(): Vector2 /* this */
    override fun negate(): Vector2 /* this */
    open fun dot(v: Vector2): Double
    override fun dot(v: Vector): Double
    open fun cross(v: Vector2): Double
    override fun lengthSq(): Double
    override fun length(): Double
    open fun lengthManhattan(): Double
    open fun manhattanLength(): Double
    override fun normalize(): Vector2 /* this */
    open fun angle(): Double
    open fun distanceTo(v: Vector2): Double
    open fun distanceToSquared(v: Vector2): Double
    open fun distanceToManhattan(v: Vector2): Double
    open fun manhattanDistanceTo(v: Vector2): Double
    override fun setLength(length: Number): Vector2 /* this */
    open fun lerp(v: Vector2, alpha: Number): Vector2 /* this */
    override fun lerp(v: Vector, alpha: Number): Vector /* this */
    open fun lerpVectors(v1: Vector2, v2: Vector2, alpha: Double): Vector2 /* this */
    open fun equals(v: Vector2): Boolean
    override fun equals(v: Vector): Boolean
    open fun fromArray(array: Array<Double>, offset: Double = definedExternally): Vector2 /* this */
    open fun fromArray(array: ArrayLike<Double>, offset: Double = definedExternally): Vector2 /* this */
    open fun toArray(array: Array<Double> = definedExternally, offset: Double = definedExternally): Array<Double>
    open fun toArray(array: ArrayLike<Double>, offset: Double = definedExternally): ArrayLike<Double>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Double): Vector2 /* this */
    open fun rotateAround(center: Vector2, angle: Double): Vector2 /* this */
    open fun random(): Vector2 /* this */
}