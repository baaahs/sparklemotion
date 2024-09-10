package three.js

external interface SceneJSONObject : Object3DJSONObject {
    var fog: dynamic /* FogJSON? | FogExp2JSON? */
        get() = definedExternally
        set(value) = definedExternally
    var backgroundBlurriness: Number?
        get() = definedExternally
        set(value) = definedExternally
    var backgroundIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var backgroundRotation: dynamic /* JsTuple<x, Number, y, Number, z, Number, Any, String> */
        get() = definedExternally
        set(value) = definedExternally
    var environmentIntensity: Number?
        get() = definedExternally
        set(value) = definedExternally
    var environmentRotation: dynamic /* JsTuple<x, Number, y, Number, z, Number, Any, String> */
        get() = definedExternally
        set(value) = definedExternally
}

external interface SceneJSON : Object3DJSON {
//    override var `object`: SceneJSONObject
}

open external class Scene : Object3D__0 {
    open val isScene: Boolean
    override var type: String /* "Scene" */
    open var fog: dynamic /* Fog? | FogExp2? */
    open var backgroundBlurriness: Number
    open var backgroundIntensity: Number
    open var overrideMaterial: Material?
    open var background: dynamic /* Color? | Texture? | CubeTexture? */
    open var backgroundRotation: Euler
    open var environment: Texture?
    open var environmentIntensity: Number
    open var environmentRotation: Euler
    override fun toJSON(meta: JSONMeta): SceneJSON
}