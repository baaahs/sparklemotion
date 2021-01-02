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

open external class Plane(normal: Vector3 = definedExternally, constant: Number = definedExternally) {
    open var normal: Vector3
    open var constant: Number
    open var isPlane: Boolean
    open fun set(normal: Vector3, constant: Number): Plane
    open fun setComponents(x: Number, y: Number, z: Number, w: Number): Plane
    open fun setFromNormalAndCoplanarPoint(normal: Vector3, point: Vector3): Plane
    open fun setFromCoplanarPoints(a: Vector3, b: Vector3, c: Vector3): Plane
    open fun clone(): Plane /* this */
    open fun copy(plane: Plane): Plane /* this */
    open fun normalize(): Plane
    open fun negate(): Plane
    open fun distanceToPoint(point: Vector3): Number
    open fun distanceToSphere(sphere: Sphere): Number
    open fun projectPoint(point: Vector3, target: Vector3): Vector3
    open fun orthoPoint(point: Vector3, target: Vector3): Vector3
    open fun intersectLine(line: Line3, target: Vector3): Vector3?
    open fun intersectsLine(line: Line3): Boolean
    open fun intersectsBox(box: Box3): Boolean
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun coplanarPoint(target: Vector3): Vector3
    open fun applyMatrix4(matrix: Matrix4, optionalNormalMatrix: Matrix3 = definedExternally): Plane
    open fun translate(offset: Vector3): Plane
    open fun equals(plane: Plane): Boolean
    open fun isIntersectionLine(l: Any): Any
}