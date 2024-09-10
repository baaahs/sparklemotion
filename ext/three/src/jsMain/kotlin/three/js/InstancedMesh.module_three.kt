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

external interface InstancedMeshJSONObject : MeshJSONObject {
    var count: Number
    var instanceMatrix: BufferAttributeJSON
    var instanceColor: BufferAttributeJSON?
        get() = definedExternally
        set(value) = definedExternally
}

external interface InstancedMeshJSON : MeshJSONObject {
    var `object`: InstancedMeshJSONObject
}

external interface InstancedMeshEventMap : Object3DEventMap {
    var dispose: Any
}

open external class InstancedMesh<TGeometry : BufferGeometry__0, TMaterial, TEventMap : InstancedMeshEventMap>(geometry: TGeometry?, material: TMaterial?, count: Number) : Mesh<TGeometry, TMaterial, TEventMap> {
    open val isInstancedMesh: Boolean
    open var boundingBox: Box3?
    open var boundingSphere: Sphere?
    open var count: Number
    open var instanceColor: InstancedBufferAttribute?
    open var instanceMatrix: InstancedBufferAttribute
    open var morphTexture: DataTexture?
    open fun computeBoundingBox()
    open fun computeBoundingSphere()
    open fun getColorAt(index: Number, color: Color)
    open fun setColorAt(index: Number, color: Color)
    open fun getMatrixAt(index: Number, matrix: Matrix4)
    open fun getMorphAt(index: Number, mesh: Mesh__0)
    open fun setMatrixAt(index: Number, matrix: Matrix4)
    open fun setMorphAt(index: Number, mesh: Mesh__0)
    override var override: Any
    override fun updateMorphTargets()
    open fun dispose(): InstancedMesh<TGeometry, TMaterial, TEventMap> /* this */
//    open fun toJSON(meta: JSONMeta = definedExternally): InstancedMeshJSON
}