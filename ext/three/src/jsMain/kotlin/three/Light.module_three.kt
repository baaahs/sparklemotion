@file:JsModule("three")
@file:JsNonModule
package three

external interface LightJSON : Object3DJSON {
    var color: Number
    var intensity: Number
    var groundColor: Number?
        get() = definedExternally
        set(value) = definedExternally
    var distance: Number?
        get() = definedExternally
        set(value) = definedExternally
    var angle: Number?
        get() = definedExternally
        set(value) = definedExternally
    var decay: Number?
        get() = definedExternally
        set(value) = definedExternally
    var penumbra: Number?
        get() = definedExternally
        set(value) = definedExternally
    var shadow: LightShadowJSON?
        get() = definedExternally
        set(value) = definedExternally
    var target: String?
        get() = definedExternally
        set(value) = definedExternally
}

open external class Light<TShadowSupport : LightShadow<out Camera>?> : Object3D/*<Object3DEventMap>*/ {
    constructor(color: Color = definedExternally, intensity: Number = definedExternally)
    constructor()
    constructor(color: Color = definedExternally)
    constructor(color: String = definedExternally, intensity: Number = definedExternally)
    constructor(color: String = definedExternally)
    constructor(color: Number = definedExternally, intensity: Number = definedExternally)
    constructor(color: Number = definedExternally)
    open val isLight: Boolean
    open var override: Any
    override val type: String /* String | "Light" */
    open var color: Color
    open var intensity: Number
    open var shadow: TShadowSupport
    open fun copy(source: Light<TShadowSupport> /* this */, recursive: Boolean = definedExternally): Light<TShadowSupport> /* this */
//    override fun copy(obj: Object3D/*<Object3DEventMap>*/, recursive: Boolean): Object3D/*<Object3DEventMap>*/ /* this */
    open fun dispose()
    override fun toJSON(meta: JSONMeta): LightJSON
}

open external class Light__0 : Light<LightShadow<Camera>?>