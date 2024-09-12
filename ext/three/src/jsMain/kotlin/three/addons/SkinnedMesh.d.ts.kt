@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
package three.addons

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
    override var `object`: SkinnedMeshJSONObject
}

external open class SkinnedMesh<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally, useVertexTexture: Boolean = definedExternally) : Mesh<TGeometry, TMaterial, TEventMap> {
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

external open class SkinnedMesh__0 : SkinnedMesh<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>