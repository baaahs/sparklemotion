package three.js

external interface `T$105` {
    var start: Number
    var count: Number
    var z: Number
}

open external class BatchedMesh(maxInstanceCount: Number, maxVertexCount: Number, maxIndexCount: Number = definedExternally, material: Material = definedExternally) : Mesh<BufferGeometry<NormalBufferAttributes>, Material> {
    open var boundingBox: Box3?
    open var boundingSphere: Sphere?
    open var customSort: ((self: BatchedMesh /* this */, list: Array<`T$105`>, camera: Camera) -> Unit)?
    open var perObjectFrustumCulled: Boolean
    open var sortObjects: Boolean
    open val isBatchedMesh: Boolean
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun dispose(): BatchedMesh /* this */
    open fun setCustomSort(sortFunction: ((self: BatchedMesh /* this */, list: Array<`T$105`>, camera: Camera) -> Unit)?): BatchedMesh /* this */
    open fun getColorAt(instanceId: Number, target: Color)
    open fun getMatrixAt(instanceId: Number, target: Matrix4): Matrix4
    open fun getVisibleAt(instanceId: Number): Boolean
    open fun setColorAt(instanceId: Number, color: Color)
    open fun setMatrixAt(instanceId: Number, matrix: Matrix4): BatchedMesh /* this */
    open fun setVisibleAt(instanceId: Number, visible: Boolean): BatchedMesh /* this */
    open fun addGeometry(geometry: BufferGeometry<NormalBufferAttributes>, reservedVertexRange: Number = definedExternally, reservedIndexRange: Number = definedExternally): Number
    open fun addInstance(geometryId: Number): Number
    open fun setGeometryAt(geometryId: Number, geometry: BufferGeometry<NormalBufferAttributes>): Number
    open fun getBoundingBoxAt(geometryId: Number, target: Box3): Box3?
    open fun getBoundingSphereAt(geometryId: Number, target: Sphere): Sphere?
}