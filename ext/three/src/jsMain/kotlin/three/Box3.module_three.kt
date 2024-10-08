@file:JsModule("three")
@file:JsNonModule
package three

open external class Box3(min: Vector3 = definedExternally, max: Vector3 = definedExternally) {
    open var min: Vector3
    open var max: Vector3
    open val isBox3: Boolean
    open fun set(min: Vector3, max: Vector3): Box3 /* this */
    open fun setFromArray(array: Array<Number>): Box3 /* this */
    open fun setFromBufferAttribute(bufferAttribute: BufferAttribute): Box3 /* this */
    open fun setFromPoints(points: Array<Vector3>): Box3 /* this */
    open fun setFromCenterAndSize(center: Vector3, size: Vector3): Box3 /* this */
    open fun setFromObject(obj: Object3D/*<Object3DEventMap>*/, precise: Boolean = definedExternally): Box3 /* this */
    open fun clone(): Box3 /* this */
    open fun copy(box: Box3): Box3 /* this */
    open fun makeEmpty(): Box3 /* this */
    open fun isEmpty(): Boolean
    open fun getCenter(target: Vector3): Vector3
    open fun getSize(target: Vector3): Vector3
    open fun expandByPoint(point: Vector3): Box3 /* this */
    open fun expandByVector(vector: Vector3): Box3 /* this */
    open fun expandByScalar(scalar: Number): Box3 /* this */
    open fun expandByObject(obj: Object3D/*<Object3DEventMap>*/, precise: Boolean = definedExternally): Box3 /* this */
    open fun containsPoint(point: Vector3): Boolean
    open fun containsBox(box: Box3): Boolean
    open fun getParameter(point: Vector3, target: Vector3): Vector3
    open fun intersectsBox(box: Box3): Boolean
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun intersectsPlane(plane: Plane): Boolean
    open fun intersectsTriangle(triangle: Triangle): Boolean
    open fun clampPoint(point: Vector3, target: Vector3): Vector3
    open fun distanceToPoint(point: Vector3): Number
    open fun getBoundingSphere(target: Sphere): Sphere
    open fun intersect(box: Box3): Box3 /* this */
    open fun union(box: Box3): Box3 /* this */
    open fun applyMatrix4(matrix: Matrix4): Box3 /* this */
    open fun translate(offset: Vector3): Box3 /* this */
    open fun equals(box: Box3): Boolean
    open fun empty(): Any
    open fun isIntersectionBox(b: Any): Any
    open fun isIntersectionSphere(s: Any): Any
}