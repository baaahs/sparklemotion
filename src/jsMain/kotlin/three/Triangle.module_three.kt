@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

open external class Triangle(a: Vector3 = definedExternally, b: Vector3 = definedExternally, c: Vector3 = definedExternally) {
    open var a: Vector3
    open var b: Vector3
    open var c: Vector3
    open fun set(a: Vector3, b: Vector3, c: Vector3): Triangle
    open fun setFromPointsAndIndices(points: Array<Vector3>, i0: Number, i1: Number, i2: Number): Triangle
    open fun clone(): Triangle /* this */
    open fun copy(triangle: Triangle): Triangle /* this */
    open fun getArea(): Number
    open fun getMidpoint(target: Vector3): Vector3
    open fun getNormal(target: Vector3): Vector3
    open fun getPlane(target: Plane): Plane
    open fun getBarycoord(point: Vector3, target: Vector3): Vector3
    open fun getUV(point: Vector3, uv1: Vector2, uv2: Vector2, uv3: Vector2, target: Vector2): Vector2
    open fun containsPoint(point: Vector3): Boolean
    open fun intersectsBox(box: Box3): Boolean
    open fun isFrontFacing(direction: Vector3): Boolean
    open fun closestPointToPoint(point: Vector3, target: Vector3): Vector3
    open fun equals(triangle: Triangle): Boolean

    companion object {
        fun getNormal(a: Vector3, b: Vector3, c: Vector3, target: Vector3): Vector3
        fun getBarycoord(point: Vector3, a: Vector3, b: Vector3, c: Vector3, target: Vector3): Vector3
        fun containsPoint(point: Vector3, a: Vector3, b: Vector3, c: Vector3): Boolean
        fun getUV(point: Vector3, p1: Vector3, p2: Vector3, p3: Vector3, uv1: Vector2, uv2: Vector2, uv3: Vector2, target: Vector2): Vector2
        fun isFrontFacing(a: Vector3, b: Vector3, c: Vector3, direction: Vector3): Boolean
    }
}