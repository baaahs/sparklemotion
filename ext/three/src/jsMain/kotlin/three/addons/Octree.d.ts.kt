@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.*

open external class Octree(box: Box3? = definedExternally) {
    open var box: Box3?
    open var bounds: Box3
    open var subTrees: Array<Octree>
    open var triangles: Array<Triangle>
    open var layers: Layers
    open fun addTriangle(triangle: Triangle): Octree /* this */
    open fun calcBox(): Octree /* this */
    open fun split(level: Number): Octree /* this */
    open fun build(): Octree /* this */
    open fun getRayTriangles(ray: Ray, triangles: Array<Triangle>): Array<Triangle>
    open fun triangleCapsuleIntersect(capsule: Capsule, triangle: Triangle): Any
    open fun triangleSphereIntersect(sphere: Sphere, triangle: Triangle): Any
    open fun getSphereTriangles(sphere: Sphere, triangles: Array<Triangle>): Array<Triangle>
    open fun getCapsuleTriangles(capsule: Capsule, triangles: Array<Triangle>): Array<Triangle>
    open fun sphereIntersect(sphere: Sphere): Any
    open fun capsuleIntersect(capsule: Capsule): Any
    open fun rayIntersect(ray: Ray): Any
    open fun fromGraphNode(group: Object3D): Octree /* this */
    open fun clear(): Octree /* this */
}