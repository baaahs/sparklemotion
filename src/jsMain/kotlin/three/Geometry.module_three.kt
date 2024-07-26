@file:JsModule("three")
@file:JsNonModule
@file:Suppress("PackageDirectoryMismatch")
package three.js

external interface MorphTarget {
    var name: String
    var vertices: Array<Vector3>
}

external interface MorphNormals {
    var name: String
    var normals: Array<Vector3>
}

open external class Geometry : EventDispatcher {
    open var id: Number
    open var uuid: String
    open var isGeometry: Boolean
    open var name: String
    open var type: String
    open var vertices: Array<Vector3>
    open var colors: Array<Color>
    open var faces: Array<Face3>
    open var faceVertexUvs: Array<Array<Array<Vector2>>>
    open var morphTargets: Array<MorphTarget>
    open var morphNormals: Array<MorphNormals>
    open var skinWeights: Array<Vector4>
    open var skinIndices: Array<Vector4>
    open var lineDistances: Array<Number>
    open var boundingBox: Box3?
    open var boundingSphere: Sphere?
    open var verticesNeedUpdate: Boolean
    open var elementsNeedUpdate: Boolean
    open var uvsNeedUpdate: Boolean
    open var normalsNeedUpdate: Boolean
    open var colorsNeedUpdate: Boolean
    open var lineDistancesNeedUpdate: Boolean
    open var groupsNeedUpdate: Boolean
    open fun applyMatrix4(matrix: Matrix4): Geometry
    open fun rotateX(angle: Number): Geometry
    open fun rotateY(angle: Number): Geometry
    open fun rotateZ(angle: Number): Geometry
    open fun translate(x: Number, y: Number, z: Number): Geometry
    open fun scale(x: Number, y: Number, z: Number): Geometry
    open fun lookAt(vector: Vector3)
    open fun fromBufferGeometry(geometry: BufferGeometry): Geometry
    open fun center(): Geometry
    open fun normalize(): Geometry
    open fun computeFaceNormals()
    open fun computeVertexNormals(areaWeighted: Boolean = definedExternally)
    open fun computeFlatVertexNormals()
    open fun computeMorphNormals()
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun merge(geometry: Geometry, matrix: Matrix = definedExternally, materialIndexOffset: Number = definedExternally)
    open fun mergeMesh(mesh: Mesh<dynamic, dynamic>)
    open fun mergeVertices(): Number
    open fun setFromPoints(points: Array<Vector2>): Geometry /* this */
    open fun setFromPoints(points: Array<Vector3>): Geometry /* this */
    open fun sortFacesByMaterialIndex()
    open fun toJSON(): Any
    open fun clone(): Geometry /* this */
    open fun copy(source: Geometry): Geometry /* this */
    open fun dispose()
    open var bones: Array<Bone>
    open var animation: AnimationClip
    open var animations: Array<AnimationClip>
}