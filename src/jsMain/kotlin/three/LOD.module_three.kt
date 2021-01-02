@file:JsModule("three")
@file:JsNonModule
@file:Suppress("ABSTRACT_MEMBER_NOT_IMPLEMENTED", "VAR_TYPE_MISMATCH_ON_OVERRIDE", "INTERFACE_WITH_SUPERCLASS", "OVERRIDING_FINAL_MEMBER", "RETURN_TYPE_MISMATCH_ON_OVERRIDE", "CONFLICTING_OVERLOADS", "EXTERNAL_DELEGATION", "PackageDirectoryMismatch")
package three.js

import kotlin.js.*
import kotlin.js.Json
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

external interface `T$25` {
    var distance: Number
    var `object`: Object3D
}

open external class LOD : Object3D {
    override var type: String /* 'LOD' */
    open var levels: Array<`T$25`>
    open var autoUpdate: Boolean
    open var isLOD: Boolean
    open fun addLevel(obj: Object3D, distance: Number = definedExternally): LOD /* this */
    open fun getCurrentLevel(): Number
    open fun getObjectForDistance(distance: Number): Object3D?
    override fun raycast(raycaster: Raycaster, intersects: Array<Intersection>)
    open fun update(camera: Camera)
    open fun toJSON(meta: Any): Any
    override fun toJSON(meta: `T$0`): Any
    open var objects: Array<Any>
}