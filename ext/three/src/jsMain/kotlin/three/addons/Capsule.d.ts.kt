package three.addons

import three.Box3
import three.Vector3

open external class Capsule(start: Vector3 = definedExternally, end: Vector3 = definedExternally, radius: Number = definedExternally) {
    open var start: Vector3
    open var end: Vector3
    open var radius: Number
    open fun set(start: Vector3, end: Vector3, radius: Number): Capsule /* this */
    open fun clone(): Capsule
    open fun copy(capsule: Capsule): Capsule /* this */
    open fun getCenter(target: Vector3): Vector3
    open fun translate(v: Vector3): Capsule /* this */
    open fun checkAABBAxis(p1x: Number, p1y: Number, p2x: Number, p2y: Number, minx: Number, maxx: Number, miny: Number, maxy: Number, radius: Number): Boolean
    open fun intersectsBox(box: Box3): Boolean
}