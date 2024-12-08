@file:JsModule("three")
@file:JsNonModule
package three

external interface MeshJSONObject : Object3DJSONObject {
    var geometry: String
}

external interface MeshJSON : Object3DJSON {
//    override var `object`: MeshJSONObject
}

external interface `T$31`

open external class Mesh<TGeometry : BufferGeometry<NormalBufferAttributes>, TMaterial/*, TEventMap : Object3DEventMap*/>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D/*<Object3DEventMap>*/ {
    open val isMesh: Boolean
    open var override: Any
    override val type: String /* String | "Mesh" */
    open var geometry: TGeometry
    open var material: TMaterial
    open var morphTargetInfluences: Array<Number>?
    open var morphTargetDictionary: `T$31`?
    open fun updateMorphTargets()
    open fun getVertexPosition(index: Number, target: Vector3): Vector3
    override fun toJSON(meta: JSONMeta): MeshJSON
}

//typealias Mesh__0 = Mesh<BufferGeometry<NormalBufferAttributes>, dynamic /* Material | Array<Material> */, Object3DEventMap>

//typealias Mesh__2<TGeometry : BufferGeometry<NormalBufferAttributes>, TMaterial> = Mesh<TGeometry, TMaterial, Object3DEventMap>