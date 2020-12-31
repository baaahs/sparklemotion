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

external interface `T$48` {
    var enabled: Boolean
    var fullWidth: Number
    var fullHeight: Number
    var offsetX: Number
    var offsetY: Number
    var width: Number
    var height: Number
}

open external class PerspectiveCamera(fov: Number = definedExternally, aspect: Number = definedExternally, near: Number = definedExternally, far: Number = definedExternally) : Camera {
    override var type: String /* 'PerspectiveCamera' */
    open var isPerspectiveCamera: Boolean
    open var zoom: Number
    open var fov: Number
    open var aspect: Number
    open var near: Number
    open var far: Number
    open var focus: Number
    open var view: `T$48`?
    open var filmGauge: Number
    open var filmOffset: Number
    open fun setFocalLength(focalLength: Number)
    open fun getFocalLength(): Number
    open fun getEffectiveFOV(): Number
    open fun getFilmWidth(): Number
    open fun getFilmHeight(): Number
    open fun setViewOffset(fullWidth: Number, fullHeight: Number, x: Number, y: Number, width: Number, height: Number)
    open fun clearViewOffset()
    open fun updateProjectionMatrix()
    open fun toJSON(meta: Any = definedExternally): Any
    override fun toJSON(meta: `T$0`): Any
    open fun setLens(focalLength: Number, frameHeight: Number = definedExternally)
}