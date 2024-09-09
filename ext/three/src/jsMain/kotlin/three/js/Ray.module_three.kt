@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

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