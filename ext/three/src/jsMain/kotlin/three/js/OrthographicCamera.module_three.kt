@file:JsModule("three")
@file:JsNonModule
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

external interface OrthographicCameraJSONObject : Object3DJSONObject {
    var zoom: Number
    var left: Number
    var right: Number
    var top: Number
    var bottom: Number
    var near: Number
    var far: Number
    var view: `T$30`?
        get() = definedExternally
        set(value) = definedExternally
}

external interface OrthographicCameraJSON : Object3DJSON {
//    override var `object`: OrthographicCameraJSONObject
}

open external class OrthographicCamera(left: Number = definedExternally, right: Number = definedExternally, top: Number = definedExternally, bottom: Number = definedExternally, near: Number = definedExternally, far: Number = definedExternally) : Camera {
    open val isOrthographicCamera: Boolean
    override var override: Any
    override val type: String /* String | "OrthographicCamera" */
    open var zoom: Number
    open var view: `T$30`?
    open var left: Number
    open var right: Number
    open var top: Number
    open var bottom: Number
    open var near: Number
    open var far: Number
    open fun updateProjectionMatrix()
    open fun setViewOffset(fullWidth: Number, fullHeight: Number, offsetX: Number, offsetY: Number, width: Number, height: Number)
    open fun clearViewOffset()
    override fun toJSON(meta: JSONMeta): OrthographicCameraJSON
}