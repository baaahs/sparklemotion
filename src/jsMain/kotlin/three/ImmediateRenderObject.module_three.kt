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

open external class ImmediateRenderObject(material: Material) : Object3D {
    open var isImmediateRenderObject: Boolean
    open var material: Material
    open var hasPositions: Boolean
    open var hasNormals: Boolean
    open var hasColors: Boolean
    open var hasUvs: Boolean
    open var positionArray: Float32Array?
    open var normalArray: Float32Array?
    open var colorArray: Float32Array?
    open var uvArray: Float32Array?
    open var count: Number
    open fun render(renderCallback: Function<*>)
}