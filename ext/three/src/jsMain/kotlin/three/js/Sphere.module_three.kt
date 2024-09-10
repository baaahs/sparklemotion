package three.js

open external class Sphere(center: Vector3 = definedExternally, radius: Number = definedExternally) {
    open val isSphere: Boolean
    open var center: Vector3
    open var radius: Number
    open fun set(center: Vector3, radius: Number): Sphere
    open fun setFromPoints(points: Array<Vector3>, optionalCenter: Vector3 = definedExternally): Sphere
    open fun clone(): Sphere /* this */
    open fun copy(sphere: Sphere): Sphere /* this */
    open fun expandByPoint(point: Vector3): Sphere /* this */
    open fun isEmpty(): Boolean
    open fun makeEmpty(): Sphere /* this */
    open fun containsPoint(point: Vector3): Boolean
    open fun distanceToPoint(point: Vector3): Number
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun intersectsBox(box: Box3): Boolean
    open fun intersectsPlane(plane: Plane): Boolean
    open fun clampPoint(point: Vector3, target: Vector3): Vector3
    open fun getBoundingBox(target: Box3): Box3
    open fun applyMatrix4(matrix: Matrix4): Sphere
    open fun translate(offset: Vector3): Sphere
    open fun equals(sphere: Sphere): Boolean
    open fun union(sphere: Sphere): Sphere /* this */
    open fun empty(): Any
}