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

open external class Ray(origin: Vector3 = definedExternally, direction: Vector3 = definedExternally) {
    open var origin: Vector3
    open var direction: Vector3
    open fun set(origin: Vector3, direction: Vector3): Ray
    open fun clone(): Ray /* this */
    open fun copy(ray: Ray): Ray /* this */
    open fun at(t: Number, target: Vector3): Vector3
    open fun lookAt(v: Vector3): Ray
    open fun recast(t: Number): Ray
    open fun closestPointToPoint(point: Vector3, target: Vector3): Vector3
    open fun distanceToPoint(point: Vector3): Number
    open fun distanceSqToPoint(point: Vector3): Number
    open fun distanceSqToSegment(v0: Vector3, v1: Vector3, optionalPointOnRay: Vector3 = definedExternally, optionalPointOnSegment: Vector3 = definedExternally): Number
    open fun intersectSphere(sphere: Sphere, target: Vector3): Vector3?
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun distanceToPlane(plane: Plane): Number
    open fun intersectPlane(plane: Plane, target: Vector3): Vector3?
    open fun intersectsPlane(plane: Plane): Boolean
    open fun intersectBox(box: Box3, target: Vector3): Vector3?
    open fun intersectsBox(box: Box3): Boolean
    open fun intersectTriangle(a: Vector3, b: Vector3, c: Vector3, backfaceCulling: Boolean, target: Vector3): Vector3?
    open fun applyMatrix4(matrix4: Matrix4): Ray
    open fun equals(ray: Ray): Boolean
    open fun isIntersectionBox(b: Any): Any
    open fun isIntersectionPlane(p: Any): Any
    open fun isIntersectionSphere(s: Any): Any
}