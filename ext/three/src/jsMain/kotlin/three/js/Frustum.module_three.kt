@file:JsModule("three")
@file:JsNonModule
package three.js

open external class Frustum(p0: Plane = definedExternally, p1: Plane = definedExternally, p2: Plane = definedExternally, p3: Plane = definedExternally, p4: Plane = definedExternally, p5: Plane = definedExternally) {
    open var planes: Array<Plane>
    open fun set(p0: Plane, p1: Plane, p2: Plane, p3: Plane, p4: Plane, p5: Plane): Frustum
    open fun clone(): Frustum /* this */
    open fun copy(frustum: Frustum): Frustum /* this */
    open fun setFromProjectionMatrix(m: Matrix4, coordinateSystem: Any = definedExternally): Frustum /* this */
    open fun intersectsObject(obj: Object3D/*<Object3DEventMap>*/): Boolean
    open fun intersectsSprite(sprite: Sprite__0): Boolean
    open fun intersectsSphere(sphere: Sphere): Boolean
    open fun intersectsBox(box: Box3): Boolean
    open fun containsPoint(point: Vector3): Boolean
}