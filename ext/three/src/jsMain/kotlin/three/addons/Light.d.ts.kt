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

external open class Light<TShadowSupport : LightShadow__0?> : Object3D__0 {
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
    override fun copy(obj: Object3D__0, recursive: Boolean): Object3D<TEventMap> /* this */
    open fun dispose()
    override fun toJSON(meta: JSONMeta): LightJSON
}

external open class Light__0 : Light<LightShadow__0?>