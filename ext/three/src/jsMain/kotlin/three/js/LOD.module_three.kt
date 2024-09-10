package three.js

external interface `T$106` {
    var `object`: String
    var distance: Number
    var hysteresis: Number
}

external interface LODJSONObject : Object3DJSONObject {
    var autoUpdate: Boolean?
        get() = definedExternally
        set(value) = definedExternally
    var levels: Array<`T$106`>
}

external interface LODJSON : Object3DJSON {
//    override var `object`: LODJSONObject
}

external interface `T$107` {
    var `object`: Object3D__0
    var distance: Number
    var hysteresis: Number
}

open external class LOD<TEventMap : Object3DEventMap> : Object3D<TEventMap> {
    open val isLOD: Boolean
    open var override: Any
    override val type: String /* String | "LOD" */
    open var levels: Array<`T$107`>
    open var autoUpdate: Boolean
    open fun addLevel(obj: Object3D__0, distance: Number = definedExternally, hysteresis: Number = definedExternally): LOD<TEventMap> /* this */
    open fun getCurrentLevel(): Number
    open fun getObjectForDistance(distance: Number): Object3D__0?
    open fun update(camera: Camera)
    override fun toJSON(meta: JSONMeta): LODJSON
}