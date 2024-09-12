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

external interface MeshJSONObject : Object3DJSONObject {
    var geometry: String
}

external interface MeshJSON : Object3DJSON {
    override var `object`: MeshJSONObject
}

external interface `T$31` {
    @nativeGetter
    operator fun get(key: String): Number?
    @nativeSetter
    operator fun set(key: String, value: Number)
}

external open class Mesh<TGeometry : BufferGeometry__0, TMaterial, TEventMap : Object3DEventMap>(geometry: TGeometry = definedExternally, material: TMaterial = definedExternally) : Object3D<TEventMap> {
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

external open class Mesh__0 : Mesh<BufferGeometry__0, dynamic /* Material | Array<Material> */, Object3DEventMap>

external open class Mesh__2<TGeometry : BufferGeometry__0, TMaterial> : Mesh<TGeometry, TMaterial, Object3DEventMap>

external open class Mesh__1<TGeometry : BufferGeometry__0> : Mesh<TGeometry, dynamic /* Material | Array<Material> */, Object3DEventMap>