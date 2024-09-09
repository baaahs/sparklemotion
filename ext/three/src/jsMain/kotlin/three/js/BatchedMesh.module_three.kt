@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.js

import kotlin.js.*
import org.khronos.webgl.*
import org.w3c.dom.*
import org.w3c.dom.events.*
import org.w3c.dom.parsing.*
import org.w3c.dom.svg.*
import org.w3c.dom.url.*
import org.w3c.fetch.*
import org.w3c.files.*
import org.w3c.notifications.*
import org.w3c.performance.*
import org.w3c.workers.*
import org.w3c.xhr.*

external interface `T$105` {
    var start: Number
    var count: Number
    var z: Number
}

external open class BatchedMesh(maxInstanceCount: Number, maxVertexCount: Number, maxIndexCount: Number = definedExternally, material: Material = definedExternally) : Mesh__2<BufferGeometry__0, Material> {
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
    open fun addGeometry(geometry: BufferGeometry__0, reservedVertexRange: Number = definedExternally, reservedIndexRange: Number = definedExternally): Number
    open fun addInstance(geometryId: Number): Number
    open fun setGeometryAt(geometryId: Number, geometry: BufferGeometry__0): Number
    open fun getBoundingBoxAt(geometryId: Number, target: Box3): Box3?
    open fun getBoundingSphereAt(geometryId: Number, target: Sphere): Sphere?
}