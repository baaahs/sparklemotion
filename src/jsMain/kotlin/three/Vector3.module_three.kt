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

open external class Vector3(x: Number = definedExternally, y: Number = definedExternally, z: Number = definedExternally) : Vector {
    open var x: Number
    open var y: Number
    open var z: Number
    open var isVector3: Boolean
    open fun set(x: Number, y: Number, z: Number): Vector3 /* this */
    override fun setScalar(scalar: Number): Vector3 /* this */
    open fun setX(x: Number): Vector3
    open fun setY(y: Number): Vector3
    open fun setZ(z: Number): Vector3
    override fun setComponent(index: Number, value: Number): Vector3 /* this */
    override fun getComponent(index: Number): Number
    override fun clone(): Vector3 /* this */
    open fun copy(v: Vector3): Vector3 /* this */
    override fun copy(v: Vector): Vector /* this */
    open fun add(v: Vector3): Vector3 /* this */
    override fun add(v: Vector): Vector /* this */
    override fun addScalar(s: Number): Vector3 /* this */
    open fun addScaledVector(v: Vector3, s: Number): Vector3 /* this */
    override fun addScaledVector(vector: Vector, scale: Number): Vector /* this */
    open fun addVectors(a: Vector3, b: Vector3): Vector3 /* this */
    override fun addVectors(a: Vector, b: Vector): Vector /* this */
    open fun sub(a: Vector3): Vector3 /* this */
    override fun sub(v: Vector): Vector /* this */
    open fun subScalar(s: Number): Vector3 /* this */
    open fun subVectors(a: Vector3, b: Vector3): Vector3 /* this */
    override fun subVectors(a: Vector, b: Vector): Vector /* this */
    open fun multiply(v: Vector3): Vector3 /* this */
    override fun multiplyScalar(s: Number): Vector3 /* this */
    open fun multiplyVectors(a: Vector3, b: Vector3): Vector3 /* this */
    open fun applyEuler(euler: Euler): Vector3 /* this */
    open fun applyAxisAngle(axis: Vector3, angle: Number): Vector3 /* this */
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
    open fun clampScalar(min: Number, max: Number): Vector3 /* this */
    open fun clampLength(min: Number, max: Number): Vector3 /* this */
    open fun floor(): Vector3 /* this */
    open fun ceil(): Vector3 /* this */
    open fun round(): Vector3 /* this */
    open fun roundToZero(): Vector3 /* this */
    override fun negate(): Vector3 /* this */
    open fun dot(v: Vector3): Number
    override fun dot(v: Vector): Number
    override fun lengthSq(): Number
    override fun length(): Number
    open fun lengthManhattan(): Number
    open fun manhattanLength(): Number
    open fun manhattanDistanceTo(v: Vector3): Number
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
    open fun angleTo(v: Vector3): Number
    open fun distanceTo(v: Vector3): Number
    open fun distanceToSquared(v: Vector3): Number
    open fun distanceToManhattan(v: Vector3): Number
    open fun setFromSpherical(s: Spherical): Vector3 /* this */
    open fun setFromSphericalCoords(r: Number, phi: Number, theta: Number): Vector3 /* this */
    open fun setFromCylindrical(s: Cylindrical): Vector3 /* this */
    open fun setFromCylindricalCoords(radius: Number, theta: Number, y: Number): Vector3 /* this */
    open fun setFromMatrixPosition(m: Matrix4): Vector3 /* this */
    open fun setFromMatrixScale(m: Matrix4): Vector3 /* this */
    open fun setFromMatrixColumn(matrix: Matrix4, index: Number): Vector3 /* this */
    open fun setFromMatrix3Column(matrix: Matrix3, index: Number): Vector3 /* this */
    open fun equals(v: Vector3): Boolean
    override fun equals(v: Vector): Boolean
    open fun fromArray(array: Array<Number>, offset: Number = definedExternally): Vector3 /* this */
    open fun fromArray(array: ArrayLike<Number>, offset: Number = definedExternally): Vector3 /* this */
    open fun toArray(array: Array<Number> = definedExternally, offset: Number = definedExternally): Array<Number>
    open fun toArray(array: ArrayLike<Number>, offset: Number = definedExternally): ArrayLike<Number>
    open fun fromBufferAttribute(attribute: BufferAttribute, index: Number): Vector3 /* this */
    open fun random(): Vector3 /* this */
}