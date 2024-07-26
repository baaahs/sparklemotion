@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Vector3(x: Double = definedExternally, y: Double = definedExternally, z: Double = definedExternally) : Vector {
    constructor(x: Number, y: Number, z: Number)

    open var x: Double
    open var y: Double
    open var z: Double
    open var isVector3: Boolean
    open fun set(x: Double, y: Double, z: Double): Vector3 /* this */
    open fun set(x: Number, y: Number, z: Number): Vector3 /* this */
    override fun setScalar(scalar: Double): Vector3 /* this */
    open fun setX(x: Double): Vector3
    open fun setY(y: Double): Vector3
    open fun setZ(z: Double): Vector3
    override fun setComponent(index: Number, value: Number): Vector3 /* this */
    override fun getComponent(index: Number): Double
    override fun clone(): Vector3 /* this */
    open fun copy(v: Vector3): Vector3 /* this */
    override fun copy(v: Vector): Vector /* this */
    open fun add(v: Vector3): Vector3 /* this */
    override fun add(v: Vector): Vector /* this */
    override fun addScalar(s: Double): Vector3 /* this */
    open fun addScaledVector(v: Vector3, s: Number): Vector3 /* this */
    override fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    open fun addVectors(a: Vector3, b: Vector3): Vector3 /* this */
    override fun addVectors(a: Vector, b: Vector): Vector /* this */
    open fun sub(a: Vector3): Vector3 /* this */
    override fun sub(v: Vector): Vector /* this */
    open fun subScalar(s: Double): Vector3 /* this */
    open fun subVectors(a: Vector3, b: Vector3): Vector3 /* this */
    override fun subVectors(a: Vector, b: Vector): Vector /* this */
    open fun multiply(v: Vector3): Vector3 /* this */
    override fun multiplyScalar(s: Number): Vector3 /* this */
    open fun multiplyVectors(a: Vector3, b: Vector3): Vector3 /* this */
    open fun applyEuler(euler: Euler): Vector3 /* this */
    open fun applyAxisAngle(axis: Vector3, angle: Double): Vector3 /* this */
    open fun applyMatrix3(m: Matrix3): Vector3 /* this */
    open fun applyNormalMatrix(m: Matrix3): Vector3 /* this */
    open fun applyMatrix4(m: Matrix4): Vector3 /* this */
    open fun applyQuaternion(q: Quaternion): Vector3 /* this */
    open fun project(camera: Camera): Vector3 /* this */
    open fun unproject(camera: Camera): Vector3 /* this */
    open fun transformDirection(m: Matrix4): Vector3 /* this */
    open fun divide(v: Vector3): Vector3 /* this */
    override fun divideScalar(s: Number): Vector3 /* this */
    open fun min(v: Vector3): Vector3 /* this */
    open fun max(v: Vector3): Vector3 /* this */
    open fun clamp(min: Vector3, max: Vector3): Vector3 /* this */
    open fun clampScalar(min: Double, max: Double): Vector3 /* this */
    open fun clampLength(min: Double, max: Double): Vector3 /* this */
    open fun floor(): Vector3 /* this */
    open fun ceil(): Vector3 /* this */
    open fun round(): Vector3 /* this */
    open fun roundToZero(): Vector3 /* this */
    override fun negate(): Vector3 /* this */
    open fun dot(v: Vector3): Double
    override fun dot(v: Vector): Double
    override fun lengthSq(): Double
    override fun length(): Double
    open fun lengthManhattan(): Double
    open fun manhattanLength(): Double
    open fun manhattanDistanceTo(v: Vector3): Double
    override fun normalize(): Vector3 /* this */
    override fun setLength(l: Number): Vector3 /* this */
    open fun lerp(v: Vector3, alpha: Number): Vector3 /* this */
    override fun lerp(v: Vector, alpha: Number): Vector /* this */
    open fun lerpVectors(v1: Vector3, v2: Vector3, alpha: Number): Vector3 /* this */
    open fun cross(a: Vector3): Vector3 /* this */
    open fun crossVectors(a: Vector3, b: Vector3): Vector3 /* this */
    open fun projectOnVector(v: Vector3): Vector3 /* this */
    open fun projectOnPlane(planeNormal: Vector3): Vector3 /* this */
    open fun reflect(vector: Vector3): Vector3 /* this */
    open fun angleTo(v: Vector3): Double
    open fun distanceTo(v: Vector3): Double
    open fun distanceToSquared(v: Vector3): Double
    open fun distanceToManhattan(v: Vector3): Double
    open fun setFromSpherical(s: Spherical): Vector3 /* this */
    open fun setFromSphericalCoords(r: Double, phi: Double, theta: Double): Vector3 /* this */
    open fun setFromCylindrical(s: Cylindrical): Vector3 /* this */
    open fun setFromCylindricalCoords(radius: Double, theta: Double, y: Double): Vector3 /* this */
    open fun setFromMatrixPosition(m: Matrix4): Vector3 /* this */
    open fun setFromMatrixScale(m: Matrix4): Vector3 /* this */
    open fun setFromMatrixColumn(matrix: Matrix4, index: Double): Vector3 /* this */
    open fun setFromMatrix3Column(matrix: Matrix3, index: Double): Vector3 /* this */
    open fun equals(v: Vector3): Boolean
    override fun equals(v: Vector): Boolean
    open fun fromArray(array: Array<Double>, offset: Double = definedExternally): Vector3 /* this */
    open fun fromArray(array: ArrayLike<Double>, offset: Double = definedExternally): Vector3 /* this */
    open fun toArray(array: Array<Double> = definedExternally, offset: Double = definedExternally): Array<Double>
    open fun toArray(array: ArrayLike<Double>, offset: Double = definedExternally): ArrayLike<Double>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Double): Vector3 /* this */
    open fun random(): Vector3 /* this */
}