@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Vector4(x: Double = definedExternally, y: Double = definedExternally, z: Double = definedExternally, w: Double = definedExternally) : Vector {
    open var x: Double
    open var y: Double
    open var z: Double
    open var w: Double
    open var width: Double
    open var height: Double
    open var isVector4: Boolean
    open fun set(x: Double, y: Double, z: Double, w: Double): Vector4 /* this */
    override fun setScalar(scalar: Double): Vector4 /* this */
    open fun setX(x: Double): Vector4 /* this */
    open fun setY(y: Double): Vector4 /* this */
    open fun setZ(z: Double): Vector4 /* this */
    open fun setW(w: Double): Vector4 /* this */
    override fun setComponent(index: Number, value: Number): Vector4 /* this */
    override fun getComponent(index: Number): Double
    override fun clone(): Vector4 /* this */
    open fun copy(v: Vector4): Vector4 /* this */
    override fun copy(v: Vector): Vector /* this */
    open fun add(v: Vector4): Vector4 /* this */
    override fun add(v: Vector): Vector /* this */
    override fun addScalar(scalar: Double): Vector4 /* this */
    open fun addVectors(a: Vector4, b: Vector4): Vector4 /* this */
    override fun addVectors(a: Vector, b: Vector): Vector /* this */
    open fun addScaledVector(v: Vector4, s: Double): Vector4 /* this */
    override fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    open fun sub(v: Vector4): Vector4 /* this */
    override fun sub(v: Vector): Vector /* this */
    open fun subScalar(s: Double): Vector4 /* this */
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
    open fun clampScalar(min: Double, max: Double): Vector4 /* this */
    open fun floor(): Vector4 /* this */
    open fun ceil(): Vector4 /* this */
    open fun round(): Vector4 /* this */
    open fun roundToZero(): Vector4 /* this */
    override fun negate(): Vector4 /* this */
    open fun dot(v: Vector4): Double
    override fun dot(v: Vector): Double
    override fun lengthSq(): Double
    override fun length(): Double
    open fun manhattanLength(): Double
    override fun normalize(): Vector4 /* this */
    override fun setLength(length: Number): Vector4 /* this */
    open fun lerp(v: Vector4, alpha: Number): Vector4 /* this */
    override fun lerp(v: Vector, alpha: Number): Vector /* this */
    open fun lerpVectors(v1: Vector4, v2: Vector4, alpha: Number): Vector4 /* this */
    open fun equals(v: Vector4): Boolean
    override fun equals(v: Vector): Boolean
    open fun fromArray(array: Array<Double>, offset: Double = definedExternally): Vector4 /* this */
    open fun fromArray(array: ArrayLike<Double>, offset: Double = definedExternally): Vector4 /* this */
    open fun toArray(array: Array<Double> = definedExternally, offset: Double = definedExternally): Array<Double>
    open fun toArray(array: ArrayLike<Double>, offset: Double = definedExternally): ArrayLike<Double>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Double): Vector4 /* this */
    open fun random(): Vector4 /* this */
}