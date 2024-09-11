@file:JsModule("three")
@file:JsNonModule
package three.js

external interface SkinnedMeshJSONObject : MeshJSONObject {
    var bindMode: Any
    var bindMatrix: dynamic /* JsTuple<n11, Number, n12, Number, n13, Number, n14, Number, n21, Number, n22, Number, n23, Number, n24, Number, n31, Number, n32, Number, n33, Number, n34, Number, n41, Number, n42, Number, n43, Number, n44, Number> */
        get() = definedExternally
        set(value) = definedExternally
    var skeleton: String?
        get() = definedExternally
        set(value) = definedExternally
}

external interface SkinnedMeshJSON : MeshJSON {
//    override var `object`: SkinnedMeshJSONObject
}

open external class SkinnedMesh<TGeometry : BufferGeometry<NormalBufferAttributes>, TMaterial/*, TEventMap : Object3DEventMap*/>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, useVertexTexture: Boolean = definedExternally) : Mesh<TGeometry, TMaterial> {
    open val isSkinnedMesh: Boolean
    override var override: Any
    override val type: String /* String | "SkinnedMesh" */
    open var bindMode: Any
    open var bindMatrix: Matrix4
    open var bindMatrixInverse: Matrix4
    open var boundingBox: Box3
    open var boundingSphere: Sphere
    open var skeleton: Skeleton
    open fun bind(skeleton: Skeleton, bindMatrix: Matrix4 = definedExternally)
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun pose()
    open fun normalizeSkinWeights()
    open fun applyBoneTransform(index: Number, vector: Vector3): Vector3
    override fun toJSON(meta: JSONMeta): SkinnedMeshJSON
}

//typealias SkinnedMesh__0 = SkinnedMesh<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>