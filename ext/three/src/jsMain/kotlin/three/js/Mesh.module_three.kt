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