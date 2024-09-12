@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

import three.Object3D
import three.Ray
import three.Vector3

open external class Face {
    open var normal: Vector3
    open var midpoint: Vector3
    open var area: Number
    open var constant: Number
    open var outside: VertexNode
    open var mark: Number
    open var edge: HalfEdge
    open fun compute(): Face /* this */
    open fun getEdge(i: Number): HalfEdge

    companion object {
        fun create(a: VertexNode, b: VertexNode, c: VertexNode): Face
    }
}

open external class HalfEdge(vertex: VertexNode, face: Face) {
    open var vertex: VertexNode
    open var prev: HalfEdge
    open var next: HalfEdge
    open var twin: HalfEdge
    open var face: Face
    open fun head(): VertexNode
    open fun length(): Number
    open fun lengthSquared(): Number
    open fun setTwin(edge: HalfEdge): HalfEdge /* this */
    open fun tail(): VertexNode
}

open external class VertexNode(point: Vector3) {
    open var point: Vector3
    open var prev: VertexNode
    open var next: VertexNode
    open var face: Face
}

open external class VertexList {
    open var head: VertexNode
    open var tail: VertexNode
    open fun append(vertex: VertexNode): VertexList /* this */
    open fun appendChain(vertex: VertexNode): VertexList /* this */
    open fun clear(): VertexList /* this */
    open fun first(): VertexNode
    open fun insertAfter(target: VertexNode, vertex: VertexNode): VertexList /* this */
    open fun insertBefore(target: VertexNode, vertex: VertexNode): VertexList /* this */
    open fun isEmpty(): Boolean
    open fun last(): VertexNode
    open fun remove(vertex: VertexNode): VertexList /* this */
    open fun removeSubList(a: VertexNode, b: VertexNode): VertexList /* this */
}

open external class ConvexHull {
    open var tolerance: Number
    open var faces: Array<Face>
    open var newFaces: Array<Face>
    open var assigned: VertexList
    open var unassigned: VertexList
    open var vertices: Array<VertexNode>
    open fun addAdjoiningFace(eyeVertex: VertexNode, horizonEdge: HalfEdge): HalfEdge
    open fun addNewFaces(eyeVertex: VertexNode, horizon: Array<HalfEdge>): ConvexHull /* this */
    open fun addVertexToFace(vertex: VertexNode, face: Face): ConvexHull /* this */
    open fun addVertexToHull(eyeVertex: VertexNode): ConvexHull /* this */
    open fun cleanup(): ConvexHull /* this */
    open fun compute(): ConvexHull /* this */
    open fun computeExtremes(): Any?
    open fun computeHorizon(eyePoint: Vector3, crossEdge: HalfEdge, face: Face, horizon: Array<HalfEdge>): ConvexHull /* this */
    open fun computeInitialHull(): ConvexHull /* this */
    open fun containsPoint(point: Vector3): Boolean
    open fun deleteFaceVertices(face: Face, absorbingFace: Face): ConvexHull /* this */
    open fun intersectRay(ray: Ray, target: Vector3): Vector3?
    open fun intersectsRay(ray: Ray): Boolean
    open fun makeEmpty(): ConvexHull /* this */
    open fun nextVertexToAdd(): VertexNode?
    open fun reindexFaces(): ConvexHull /* this */
    open fun removeAllVerticesFromFace(face: Face): VertexNode?
    open fun removeVertexFromFace(vertex: VertexNode, face: Face): ConvexHull /* this */
    open fun resolveUnassignedPoints(newFaces: Array<Face>): ConvexHull /* this */
    open fun setFromPoints(points: Array<Vector3>): ConvexHull /* this */
    open fun setFromObject(obj: Object3D): ConvexHull /* this */
}