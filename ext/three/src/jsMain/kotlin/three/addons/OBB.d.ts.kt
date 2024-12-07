package three.addons

import three.*

open external class OBB(center: Vector3 = definedExternally, halfSize: Vector3 = definedExternally, rotation: Matrix3 = definedExternally) {
    open var center: Vector3
    open var halfSize: Vector3
    open var rotation: Matrix3
    open fun set(center: Vector3, halfSize: Vector3, rotation: Matrix3): OBB /* this */
    open fun copy(obb: OBB): OBB /* this */
    open fun clone(): OBB /* this */
    open fun getSize(result: Vector3): Vector3
    open fun clampPoint(point: Vector3, result: Vector3): Vector3
    open fun containsPoint(point: Vector3): Boolean
    open fun intersectsBox3(box3: Box3): Boolean
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun intersectsOBB(obb: OBB, epsilon: Number = definedExternally): Boolean
    open fun intersectsPlane(plane: Plane): Boolean
    open fun intersectRay(ray: Ray, result: Vector3): Vector3?
    open fun intersectsRay(ray: Ray): Boolean
    open fun fromBox3(box3: Box3): OBB /* this */
    open fun equals(obb: OBB): Boolean
    open fun applyMatrix4(matrix: Matrix4): OBB /* this */
}