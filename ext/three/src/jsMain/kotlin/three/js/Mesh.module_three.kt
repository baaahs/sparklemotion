package three.js

external interface MeshJSONObject : Object3DJSONObject {
    var geometry: String
}

external interface MeshJSON : Object3DJSON {
//    override var `object`: MeshJSONObject
}

external interface `T$31` {
    @nativeGetter
    operator fun get(key: String): Number?
    @nativeSetter
    operator fun set(key: String, value: Number)
}

open external class Mesh<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
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

typealias Mesh__0 = Mesh<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>

open external class Mesh__2<TGeometry : BufferGeometry__0, TMaterial> : Mesh<TGeometry, TMaterial, Object3DEventMap>