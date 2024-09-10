@file:Suppress("INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS")
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